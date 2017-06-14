package com.mylhyl.dbupgrade;

import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * xutils3数据库升级工具
 * 1、创建临时表
 * 2、删除旧表
 * 3、创建新表
 * 4、还原数据
 * Created by hupei on 2017/6/14.
 */
final class UpgradeMigrationXutils extends BaseUpgradeMigration {

    /**
     * 单主键升级
     *
     * @param db
     * @param oldVersion
     * @param entityTypes 需要升级的实体类
     */
    public static void migrate(DbManager db, int oldVersion, Class... entityTypes) {
        migrate(db, oldVersion, null, entityTypes);
    }

    /**
     * 多主键升级
     *
     * @param db
     * @param oldVersion
     * @param linkedHashMap 升级的实体类与创建多主键的sql语句
     * @param entityTypes   需要升级的实体类
     */
    public static void migrate(DbManager db, int oldVersion, LinkedHashMap<Class, String>
            linkedHashMap, Class... entityTypes) {
        DbManager database = db;
        try {
            printLog("【旧数据库版本】>>>" + oldVersion);

            //step:1
            printLog("【创建临时表】>>>开始");
            generateTempTables(database, entityTypes);
            printLog("【创建临时表】>>>完成");

            //step:2
            dropAllTables(database, entityTypes);
            printLog("【删除旧表完成】");

            //step:3
            createAllTables(database, linkedHashMap, entityTypes);
            printLog("【创建新表完成】");

            //step:4
            printLog("【还原数据】开始");
            restoreData(database, entityTypes);
            printLog("【还原数据】完成");


        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private static <T> void generateTempTables(DbManager db, Class<T>... entityTypes) throws
            DbException {
        int length = entityTypes.length;
        for (int i = 0; i < length; i++) {
            String tempTableName = null;
            TableEntity<T> table = db.getTable(entityTypes[i]);
            String tableName = table.getName();
            //判断表是否存在
            if (!tableIsExist(db.getDatabase(), false, tableName)) {
                printLog("【旧表不存在】" + tableName);
                return;
            }
            generateTempTables(db.getDatabase(), tempTableName, tableName);
        }
    }

    private static <T> void dropAllTables(DbManager db, Class<T>... entityTypes) throws
            DbException {
        int length = entityTypes.length;
        for (int i = 0; i < length; i++) {
            db.dropTable(entityTypes[i]);
        }
    }

    private static <T> void createAllTables(DbManager db, LinkedHashMap<Class, String>
            linkedHashMap, Class<T>... entityTypes)
            throws DbException {
        int length = entityTypes.length;
        for (int i = 0; i < length; i++) {
            Class<T> entityType = entityTypes[i];
            TableEntity<T> tableEntity = db.getTable(entityType);
            if (tableEntity.getId() != null) {
                createTable(db, tableEntity);
            } else if (linkedHashMap != null) {
                createTable(db, tableEntity, linkedHashMap.get(entityType));
            }
            Iterator<String> iterator = tableEntity.getColumnMap().keySet().iterator();
            String columnsStr = getColumnsStr(copyIterator(iterator));
            printLog("【表】" + tableEntity.getName() + "\n ---列-->" + columnsStr);
        }
    }

    private static <T> void createTable(DbManager db, TableEntity<T> tableEntity) throws
            DbException {
        if (!tableEntity.tableIsExist()) {
            synchronized (tableEntity.getClass()) {
                SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(tableEntity);
                db.execNonQuery(sqlInfo);
                printLog("【创建单主键新表成功】\n" + sqlInfo.getSql());
            }
        }
    }

    private static <T> void createTable(DbManager db, TableEntity<T> tableEntity, String sql) throws
            DbException {
        if (!tableEntity.tableIsExist()) {//判断表是否存在
            synchronized (tableEntity.getClass()) {
                if (!tableEntity.tableIsExist()) {
                    db.execNonQuery(sql);
                    printLog("【创建多主键新表成功】\n" + sql);
                }
            }
        }
    }

    private static <T> void restoreData(DbManager db, Class<T>... entityTypes) throws DbException {
        int length = entityTypes.length;
        for (int i = 0; i < length; i++) {
            TableEntity<T> table = db.getTable(entityTypes[i]);
            String tableName = table.getName();
            String tempTableName = tableName.concat("_TEMP");
            if (!tableIsExist(db.getDatabase(), true, tempTableName)) {
                printLog("【临时表不存在】" + tempTableName);
                continue;
            }
            restoreData(db.getDatabase(), tableName, tempTableName);
        }
    }
}
