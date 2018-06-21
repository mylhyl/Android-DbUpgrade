package com.mylhyl.dbupgrade.base;

import com.mylhyl.dbupgrade.ColumnType;

import java.util.LinkedHashMap;

/**
 * Created by hupei on 2017/6/16.
 */
public class BaseTable {
    public String sqlCreateTable;
    public LinkedHashMap<String, ColumnType> addColumns = new LinkedHashMap<>();
    public boolean migration = true;

    public void addColumn(String columnName, ColumnType columnType) {
        migration = false;
        addColumns.put(columnName, columnType);
    }
}
