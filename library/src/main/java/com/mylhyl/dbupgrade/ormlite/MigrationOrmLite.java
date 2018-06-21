package com.mylhyl.dbupgrade.ormlite;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.BaseMigration;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hupei on 2017/6/16.
 */
final class MigrationOrmLite extends BaseMigration {

    public void migrate(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
                        List<TableOrmLite> upgradeList) {
        SQLiteDatabase database = db;
        setDatabase(database);
        printLog("【旧数据库版本】>>>" + oldVersion);

        beginTransaction();
        try {

            //step:1
            printLog("【创建临时表】>>>开始");
            generateTempTables(upgradeList);
            printLog("【创建临时表】>>>完成");

            //step:2
            dropAllTables(connectionSource, upgradeList);
            printLog("【删除旧表完成】");

            //step:3
            createAllTables(db, connectionSource, upgradeList);
            printLog("【创建新表完成】");

            //step:4
            printLog("【还原数据】开始");
            restoreData(upgradeList);
            printLog("【还原数据】完成");

            setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void generateTempTables(List<TableOrmLite> upgradeList) {
        for (TableOrmLite upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            String tableName = DatabaseTableConfig.extractTableName(upgradeTable.entityType);
            //判断表是否存在
            if (!tableIsExist(false, tableName)) {
                printLog("【旧表不存在】" + tableName);
                return;
            }
            generateTempTables(tableName);
        }
    }

    private void dropAllTables(ConnectionSource connectionSource, List<TableOrmLite> upgradeList)
            throws SQLException {
        for (TableOrmLite upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            TableUtils.dropTable(connectionSource, upgradeTable.entityType, true);
        }
    }

    private void createAllTables(SQLiteDatabase db, ConnectionSource connectionSource,
                                 List<TableOrmLite> upgradeList) throws SQLException {
        for (TableOrmLite upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                String tableName = DatabaseTableConfig.extractTableName(upgradeTable.entityType);
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
            if (TextUtils.isEmpty(upgradeTable.sqlCreateTable)) {
                TableUtils.createTableIfNotExists(connectionSource, upgradeTable.entityType);
            } else {
                db.execSQL(upgradeTable.sqlCreateTable);
            }
            printLog("【创建单主键新表成功】");
        }
    }

    private void restoreData(List<TableOrmLite> upgradeList) {
        for (TableOrmLite upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            String tableName = DatabaseTableConfig.extractTableName(upgradeTable.entityType);
            String tempTableName = tableName.concat("_TEMP");
            if (!tableIsExist(true, tempTableName)) {
                printLog("【临时表不存在】" + tempTableName);
                continue;
            }
            restoreData(tableName, tempTableName);
        }
    }
}
