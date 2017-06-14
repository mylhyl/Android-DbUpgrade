package com.mylhyl.dbupgrade;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hupei on 2017/6/14.
 */

class BaseUpgradeMigration {
    private static final String TAG = "DbUpgrade";
    public static boolean DEBUG = BuildConfig.DEBUG;
    static final String SQLITE_MASTER = "sqlite_master";
    static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";

    static void generateTempTables(SQLiteDatabase db, String tempTableName, String
            tableName, List<String> columns) {
        try {
            tempTableName = tableName.concat("_TEMP");
            //安全起见，不管存不存在先删除临时表
            StringBuilder dropTableStringBuilder = new StringBuilder();
            dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName)
                    .append(";");
            db.execSQL(dropTableStringBuilder.toString());

            //创建临时表
            StringBuilder insertTableStringBuilder = new StringBuilder();
            insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
            insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");
            db.execSQL(insertTableStringBuilder.toString());

            printLog("【表】" + tableName + "\n ---列-->" + getColumnsStr(columns));
            printLog("【临时表名】" + tempTableName);

        } catch (SQLException e) {
            printLog("【创建临时表失败】" + tempTableName + ">>>" + e.getMessage());
        }
    }

    static String getColumnsStr(List<String> columns) {
        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            builder.append(column);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    static List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = Arrays.asList(cursor.getColumnNames());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new ArrayList<>();
        }
        return columns;
    }

    static boolean tableIsExist(SQLiteDatabase db, boolean isTemp, String tableName) {
        if (db == null || TextUtils.isEmpty(tableName)) {
            return false;
        }
        String dbName = isTemp ? SQLITE_TEMP_MASTER : SQLITE_MASTER;
        String sql = "SELECT COUNT(*) FROM " + dbName + " WHERE type = ? AND name = ?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[]{"table", tableName});
            if (cursor == null || !cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return count > 0;
    }

    static void restoreData(SQLiteDatabase db, String tableName, String tempTableName) {
        try {
            // 取出临时表所有列
            List<String> tempColumns = getColumns(db, tempTableName);

            ArrayList<String> properties = new ArrayList<>(tempColumns.size());
            //取出新表所有列
            List<String> newColumns = getColumns(db, tableName);
            for (String columnName : newColumns) {
                //只装入临时表存在的列，新加入的列不需要还原数据
                // 也保证insert into 与select 列数一样
                if (tempColumns.contains(columnName)) {
                    properties.add(columnName);
                }
            }

            if (properties.size() > 0) {
                final String columnSQL = TextUtils.join(",", properties);
                //还原数据
                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(columnSQL);
                insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());
                printLog("【还原数据至】" + tableName);
            }
            StringBuilder dropTableStringBuilder = new StringBuilder();
            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
            db.execSQL(dropTableStringBuilder.toString());
            printLog("【删除临时表】" + tempTableName);
        } catch (SQLException e) {
            printLog("【临时表还原数据失败 】" + tempTableName + ">>" + e.getMessage());
        }
    }

    static <T> List<T> copyIterator(Iterator<T> iter) {
        List<T> copy = new ArrayList<>();
        while (iter.hasNext())
            copy.add(iter.next());
        return copy;
    }


    static void printLog(String info) {
        if (DEBUG) {
            Log.d(TAG, info);
        }
    }
}
