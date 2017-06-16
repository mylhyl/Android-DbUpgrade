package com.mylhyl.dbupgrade.ormlite;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;
import com.mylhyl.dbupgrade.base.BaseMigration;

import org.xutils.ex.DbException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by hupei on 2017/6/16.
 */
final class MigrationOrmLite extends BaseMigration {

    public void migrate(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
                        List<TableOrmLite> upgradeList) {
        SQLiteDatabase database = db;
        setDatabase(database);
        beginTransaction();
        try {
            printLog("【旧数据库版本】>>>" + oldVersion);

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
        for (TableOrmLite upgrade : upgradeList) {
            String tableName = DatabaseTableConfig.extractTableName(upgrade.entityType);
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
        for (TableOrmLite upgrade : upgradeList) {
            TableUtils.dropTable(connectionSource, upgrade.entityType, true);
        }
    }

    private void createAllTables(SQLiteDatabase db, ConnectionSource connectionSource,
                                 List<TableOrmLite> upgradeList) throws SQLException {
        for (TableOrmLite upgrade : upgradeList) {
            if (TextUtils.isEmpty(upgrade.sqlCreateTable)) {
                TableUtils.createTableIfNotExists(connectionSource, upgrade.entityType);
            } else {
                db.execSQL(upgrade.sqlCreateTable);
            }
            printLog("【创建单主键新表成功】");
        }
    }

    private void restoreData(List<TableOrmLite> upgradeList)
            throws DbException {
        for (TableOrmLite upgrade : upgradeList) {
            String tableName = DatabaseTableConfig.extractTableName(upgrade.entityType);
            String tempTableName = tableName.concat("_TEMP");
            if (!tableIsExist(true, tempTableName)) {
                printLog("【临时表不存在】" + tempTableName);
                continue;
            }
            restoreData(tableName, tempTableName);
        }
    }
}
