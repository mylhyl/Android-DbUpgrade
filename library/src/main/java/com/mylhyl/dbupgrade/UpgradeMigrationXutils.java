package com.mylhyl.dbupgrade;

import android.text.TextUtils;

import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

import java.util.Iterator;
import java.util.List;

/**
 * xutils3数据库升级工具
 * 1、创建临时表
 * 2、删除旧表
 * 3、创建新表
 * 4、还原数据
 * Created by hupei on 2017/6/14.
 */
final class UpgradeMigrationXutils extends BaseUpgradeMigration {

    public static void migrate(DbManager db, int oldVersion, List<UpgradeTableXutils> upgradeList) {
        DbManager database = db;
        try {
            printLog("【旧数据库版本】>>>" + oldVersion);

            //step:1
            printLog("【创建临时表】>>>开始");
            generateTempTables(database, upgradeList);
            printLog("【创建临时表】>>>完成");

            //step:2
            dropAllTables(database, upgradeList);
            printLog("【删除旧表完成】");

            //step:3
            createAllTables(database, upgradeList);
            printLog("【创建新表完成】");

            //step:4
            printLog("【还原数据】开始");
            restoreData(database, upgradeList);
            printLog("【还原数据】完成");


        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private static void generateTempTables(DbManager db, List<UpgradeTableXutils> upgradeList)
            throws DbException {
        for (UpgradeTableXutils upgrade : upgradeList) {
            String tempTableName = null;
            TableEntity table = db.getTable(upgrade.entityType);
            String tableName = table.getName();
            //判断表是否存在
            if (!tableIsExist(db.getDatabase(), false, tableName)) {
                printLog("【旧表不存在】" + tableName);
                return;
            }
            generateTempTables(db.getDatabase(), tempTableName, tableName);
        }
    }

    private static void dropAllTables(DbManager db, List<UpgradeTableXutils> upgradeList) throws
            DbException {
        for (UpgradeTableXutils upgrade : upgradeList) {
            db.dropTable(upgrade.entityType);
        }
    }

    private static void createAllTables(DbManager db, List<UpgradeTableXutils> upgradeList)
            throws DbException {
        for (UpgradeTableXutils upgrade : upgradeList) {
            TableEntity tableEntity = db.getTable(upgrade.entityType);
            if (tableEntity.getId() != null) {
                createTable(db, tableEntity);
            } else if (!TextUtils.isEmpty(upgrade.sqlCreateTable)) {
                createTable(db, tableEntity, upgrade.sqlCreateTable);
            }
            Iterator<String> iterator = tableEntity.getColumnMap().keySet().iterator();
            String columnsStr = getColumnsStr(copyIterator(iterator));
            printLog("【表】" + tableEntity.getName() + "\n ---列-->" + columnsStr);
        }
    }

    private static void createTable(DbManager db, TableEntity tableEntity) throws
            DbException {
        if (!tableEntity.tableIsExist()) {
            synchronized (tableEntity.getClass()) {
                SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(tableEntity);
                db.execNonQuery(sqlInfo);
                printLog("【创建单主键新表成功】\n" + sqlInfo.getSql());
            }
        }
    }

    private static void createTable(DbManager db, TableEntity tableEntity, String sql) throws
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

    private static void restoreData(DbManager db, List<UpgradeTableXutils> upgradeList)
            throws DbException {
        for (UpgradeTableXutils upgrade : upgradeList) {
            TableEntity table = db.getTable(upgrade.entityType);
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
