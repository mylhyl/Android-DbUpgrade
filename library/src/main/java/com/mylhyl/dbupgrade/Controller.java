package com.mylhyl.dbupgrade;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class Controller extends BaseController<Table, String, DbUpgrade.Native.With,
        Controller> {

    Controller(DbUpgrade.Native.With with, String tableName) {
        this.mWith = with;
        this.mTable = new Table(tableName);
    }

    /**
     * 添加列
     *
     * @param columnName 列名
     * @param fieldType  列类型
     * @return
     */
    public Controller addColumn(String columnName, ColumnType fieldType) {
        //列不存在才添加
        if (!Migration.columnIsExist(mWith.getSQLiteDatabase(), mTable.tableName, columnName))
            mTable.addColumn(columnName, fieldType);
        return this;
    }

    /**
     * 删除列
     *
     * @param columnName 列名
     * @return
     */
    public Controller removeColumn(String columnName) {
        //列存在才添加
        if (Migration.columnIsExist(mWith.getSQLiteDatabase(), mTable.tableName, columnName))
            mTable.removeColumn(columnName);
        return this;
    }

    @Override
    public Controller setUpgradeTable(String tableName) {
        if (mWith.isUpgrade()) addUpgrade();
        return mWith.setUpgradeTable(tableName);
    }

    @Override
    public Controller setSqlCreateTable(String sqlCreateTable) {
        mTable.sqlCreateTable = sqlCreateTable;
        return this;
    }

    @Override
    void addUpgrade() {
        setSqlCreateTable();
        mWith.addUpgrade(mTable);
    }

    private void setSqlCreateTable() {
        //判断是否有自定义sql
        if (!TextUtils.isEmpty(mTable.sqlCreateTable)) return;
        String tableSql = Migration.createTableSql(mWith.getSQLiteDatabase(), mTable.tableName);
        //检查是否有删除字段
        List<String> removeColumns = mTable.removeColumns;
        if (removeColumns.isEmpty()) {
            mTable.sqlCreateTable = tableSql;
            return;
        }
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
        mTable.sqlCreateTable = tableSql;
    }
}
