package com.mylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.LinkedList;

/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeController {
    private DbUpgrade.Native mNative;
    private UpgradeTable mUpgrade;
    private SQLiteDatabase mDb;
    private int mUpgradeVersion;

    private UpgradeController() {
    }

    UpgradeController(DbUpgrade.Native aNative) {
        this.mNative = aNative;
        this.mDb = mNative.getSQLiteDatabase();
    }

    UpgradeController setTableName(String tableName, int upgradeVersion) {
        this.mUpgrade = new UpgradeTable(tableName);
        this.mUpgradeVersion = upgradeVersion;
        return this;
    }

    public UpgradeController addColumn(String columnName, ColumnType fieldType) {
        //列不存在才添加
        if (!UpgradeMigration.columnIsExist(mDb, mUpgrade.tableName, columnName))
            mUpgrade.addColumn(columnName, fieldType);
        return this;
    }

    public UpgradeController removeColumn(String columnName) {
        //列存在才添加
        if (UpgradeMigration.columnIsExist(mDb, mUpgrade.tableName, columnName))
            mUpgrade.removeColumn(columnName);
        return this;
    }

    public UpgradeController setSqlCreateTable(String sqlCreateTable) {
        mUpgrade.sqlCreateTable = sqlCreateTable;
        return this;
    }

    public DbUpgrade.Native build() {
        setSqlCreateTable();
        addUpgrade(mUpgrade);
        return mNative;
    }

    private void setSqlCreateTable() {
        //判断是否有自定义sql
        if (TextUtils.isEmpty(mUpgrade.sqlCreateTable)) {
            String tableSql = UpgradeMigration.createTableSql(mDb, mUpgrade.tableName);
            //检查是否有删除字段
            LinkedList<String> removeColumns = mUpgrade.removeColumns;
            if (!removeColumns.isEmpty()) {
                String[] split = tableSql.split(",");
                for (String str : split) {
                    for (String column : removeColumns) {
                        int indexOf = str.indexOf(column);
                        if (indexOf > 0) {
                            if (str.replaceAll(" ", "").contains("))")) {
                                tableSql = tableSql.replace("," + str, "))");
                            } else {
                                tableSql = tableSql.replace(str + ",", "");
                            }
                        }
                    }
                }
            }
            mUpgrade.sqlCreateTable = tableSql;
        }
    }

    private void addUpgrade(UpgradeTable upgrade) {
        mNative.getUpgradeList().add(upgrade);
    }

    int getUpgradeVersion() {
        return mUpgradeVersion;
    }
}
