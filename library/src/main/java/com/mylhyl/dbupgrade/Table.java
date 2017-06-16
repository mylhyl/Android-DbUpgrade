package com.mylhyl.dbupgrade;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

final class Table extends BaseTable {
    public String tableName;
    public String sqlCreateTable;
    public LinkedHashMap<String, ColumnType> addColumns = new LinkedHashMap<>();
    public List<String> removeColumns = new ArrayList<>();

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(String columnName, ColumnType columnType) {
        addColumns.put(columnName, columnType);
    }

    public void removeColumn(String columnName) {
        removeColumns.add(columnName);
    }
}
