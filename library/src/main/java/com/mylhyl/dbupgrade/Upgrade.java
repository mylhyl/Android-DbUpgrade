package com.mylhyl.dbupgrade;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by hupei on 2017/6/9.
 */

final class Upgrade {
    public String tableName;
    public String sqlCreateTable;
    public LinkedHashMap<String, ColumnType> addColumns = new LinkedHashMap<>();
    public LinkedList<String> removeColumns = new LinkedList<>();

    public Upgrade(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(String columnName, ColumnType columnType) {
        addColumns.put(columnName, columnType);
    }

    public void removeColumn(String columnName) {
        removeColumns.add(columnName);
    }
}
