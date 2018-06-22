package com.mylhyl.dbupgrade.original;

import com.mylhyl.dbupgrade.base.BaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class TableOriginal extends BaseTable {
    public String tableName;
    public List<String> removeColumns = new ArrayList<>();

    public TableOriginal(String tableName, String sqlCreateTable) {
        this.tableName = tableName;
        this.sqlCreateTable = sqlCreateTable;
    }

    public void removeColumn(String columnName) {
        removeColumns.add(columnName);
    }
}
