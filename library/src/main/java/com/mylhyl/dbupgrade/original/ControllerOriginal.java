package com.mylhyl.dbupgrade.original;

import android.text.TextUtils;

import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.AbsController;

import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class ControllerOriginal extends AbsController<TableOriginal, String, Original.With
        , ControllerOriginal> {

    public ControllerOriginal(Original.With with, String tableName, String sqlCreateTable) {
        this.mWith = with;
        this.mTable = new TableOriginal(tableName, sqlCreateTable);
    }

    @Override
    public ControllerOriginal setMigration(boolean migration) {
        this.mTable.migration = migration;
        return this;
    }

    @Override
    public ControllerOriginal addColumn(String columnName, ColumnType fieldType) {
        mTable.addColumn(columnName, fieldType);
        return this;
    }

    @Override
    public ControllerOriginal setUpgradeTable(String tableName) {
        return setUpgradeTable(tableName, "");
    }

    @Override
    public ControllerOriginal setUpgradeTable(String tableName, String sqlCreateTable) {
        addUpgrade();
        return mWith.setUpgradeTable(tableName, sqlCreateTable);
    }

    @Override
    protected void addUpgrade() {
        setSqlCreateTable();
        mWith.addUpgrade(mTable);
    }

    private void setSqlCreateTable() {
        //判断是否有自定义sql
        if (!TextUtils.isEmpty(mTable.sqlCreateTable)) return;
        String tableSql = MigrationOriginal.createTableSql(mWith.getSQLiteDatabase(), mTable.tableName);
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

    /**
     * 删除列
     *
     * @param columnName 列名
     * @return
     */
    public ControllerOriginal removeColumn(String columnName) {
        mTable.removeColumn(columnName);
        return this;
    }
}
