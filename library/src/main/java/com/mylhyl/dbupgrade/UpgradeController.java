package com.mylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.List;

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

    /**
     * 添加列
     *
     * @param columnName 列名
     * @param fieldType  列类型
     * @return
     */
    public UpgradeController addColumn(String columnName, ColumnType fieldType) {
        //列不存在才添加
        if (!UpgradeMigration.columnIsExist(mDb, mUpgrade.tableName, columnName))
            mUpgrade.addColumn(columnName, fieldType);
        return this;
    }

    /**
     * 删除列
     *
     * @param columnName 列名
     * @return
     */
    public UpgradeController removeColumn(String columnName) {
        //列存在才添加
        if (UpgradeMigration.columnIsExist(mDb, mUpgrade.tableName, columnName))
            mUpgrade.removeColumn(columnName);
        return this;
    }

    /**
     * 设置创建表的 sql 语句，如多主键
     *
     * @param sqlCreateTable
     * @return
     */
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
        if (!TextUtils.isEmpty(mUpgrade.sqlCreateTable)) return;
        String tableSql = UpgradeMigration.createTableSql(mDb, mUpgrade.tableName);
        //检查是否有删除字段
        List<String> removeColumns = mUpgrade.removeColumns;
        if (removeColumns.isEmpty()) return;
        String[] split = tableSql.split(",");
        for (String str : split) {
            for (String column : removeColumns) {
                int indexOf = str.indexOf(column);
                if (indexOf < 0) continue;
                if (str.replaceAll(" ", "").contains("))"))
                    tableSql = tableSql.replace("," + str, "))");
                else {
                    if (str.contains(")"))
                        tableSql = tableSql.replace("," + str, ")");
                    else
                        tableSql = tableSql.replace(str + ",", "");
                }
            }
        }
        mUpgrade.sqlCreateTable = tableSql;
    }

    private void addUpgrade(UpgradeTable upgrade) {
        mNative.getUpgradeList().add(upgrade);
    }

    int getUpgradeVersion() {
        return mUpgradeVersion;
    }
}
