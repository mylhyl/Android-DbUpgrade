package com.mylhyl.dbupgrade.xuitls3;

import android.text.TextUtils;

import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.BaseMigration;

import org.xutils.DbManager;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.SqlInfoBuilder;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hupei on 2017/6/14.
 */
final class MigrationXutils extends BaseMigration {

    public void migrate(DbManager db, int oldVersion, List<TableXutils> upgradeList) {
        DbManager database = db;
        setDatabase(database.getDatabase());
        printLog("【旧数据库版本】>>>" + oldVersion);

        beginTransaction();
        try {

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

            setTransactionSuccessful();
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void generateTempTables(DbManager db, List<TableXutils> upgradeList)
            throws DbException {
        for (TableXutils upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            TableEntity table = db.getTable(upgradeTable.entityType);
            String tableName = table.getName();
            //判断表是否存在
            if (!tableIsExist(false, tableName)) {
                printLog("【旧表不存在】" + tableName);
                return;
            }
            generateTempTables(tableName);
        }
    }

    private void dropAllTables(DbManager db, List<TableXutils> upgradeList) throws
            DbException {
        for (TableXutils upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            db.dropTable(upgradeTable.entityType);
        }
    }

    private void createAllTables(DbManager db, List<TableXutils> upgradeList)
            throws DbException {
        for (TableXutils upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                TableEntity table = db.getTable(upgradeTable.entityType);
                String tableName = table.getName();
                if (tableIsExist(false, tableName)) {
                    //加入新列
                    LinkedHashMap<String, ColumnType> addColumnMap = upgradeTable.addColumns;
                    if (addColumnMap.isEmpty()) continue;
                    Iterator<Map.Entry<String, ColumnType>> iterator = addColumnMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, ColumnType> entry = iterator.next();
                        addColumn(tableName, entry.getKey(), entry.getValue().toString());
                    }
                }
                continue;
            }
            TableEntity tableEntity = db.getTable(upgradeTable.entityType);
            if (TextUtils.isEmpty(upgradeTable.sqlCreateTable)) {
                createTable(db, tableEntity);
            } else {
                createTable(db, tableEntity, upgradeTable.sqlCreateTable);
            }
        }
    }

    private void restoreData(DbManager db, List<TableXutils> upgradeList)
            throws DbException {
        for (TableXutils upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            TableEntity table = db.getTable(upgradeTable.entityType);
            String tableName = table.getName();
            String tempTableName = tableName.concat("_TEMP");
            if (!tableIsExist(true, tempTableName)) {
                printLog("【临时表不存在】" + tempTableName);
                continue;
            }
            restoreData(tableName, tempTableName);
        }
    }

    private void createTable(DbManager db, TableEntity tableEntity) throws
            DbException {
        if (!tableEntity.tableIsExist()) {
            synchronized (tableEntity.getClass()) {
                SqlInfo sqlInfo = SqlInfoBuilder.buildCreateTableSqlInfo(tableEntity);
                db.execNonQuery(sqlInfo);
                printLog("【创建单主键新表成功】\n" + sqlInfo.getSql());
            }
        }
    }

    private void createTable(DbManager db, TableEntity tableEntity, String sql) throws
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
}
