package com.nylhyl.dbupgrade;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

final class Upgrade {
    public String tableName;
    public String sqlCreateTable;
    public List<String> columns = new ArrayList<>();
    public LinkedHashMap<String, ColumnType> addColumns = new LinkedHashMap<>();

    public Upgrade(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(String columnName, ColumnType columnType) {
        addColumns.put(columnName, columnType);
    }
}
