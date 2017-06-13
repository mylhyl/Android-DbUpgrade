package com.nylhyl.dbupgrade;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库升级工具类
 * Created by hupei on 2017/6/9.
 */
final class UpgradeMigration {

    public static boolean DEBUG = BuildConfig.DEBUG;
    private static String TAG = "MigrationHelper";
    private static final String SQLITE_MASTER = "sqlite_master";
    private static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";

    public static void migrate(SQLiteDatabase db, int oldVersion, List<Upgrade> upgradeList) {
        SQLiteDatabase database = db;

        printLog("【旧数据库版本】>>>" + oldVersion);

        //step:1
        printLog("【创建临时表】>>>开始");
        generateTempTables(database, upgradeList);
        printLog("【创建临时表】>>>完成");

        //step:2
        dropAllTables(database, upgradeList);
        printLog("【删除旧表完成】");

        //step:3
        createAllTables(database, upgradeList);
        printLog("【创建新表完成】");

        //step:4
        printLog("【还原数据】开始");
        restoreData(database, upgradeList);
        printLog("【还原数据】完成");
    }

    private static void generateTempTables(SQLiteDatabase db, List<Upgrade> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            String tempTableName = null;
            Upgrade upgrade = upgradeList.get(i);
            String tableName = upgrade.tableName;
            //判断表是否存在
            if (!tableIsExist(db, false, tableName)) {
                printLog("【旧表不存在】" + tableName);
                return;
            }
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

                printLog("【表】" + tableName + "\n ---列-->" + getColumnsStr(upgrade));
                printLog("【临时表名】" + tempTableName);

            } catch (SQLException e) {
                Log.e(TAG, "【创建临时表失败】" + tempTableName, e);
            }
        }
    }

    private static boolean tableIsExist(SQLiteDatabase db, boolean isTemp, String tableName) {
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


    private static String getColumnsStr(Upgrade upgrade) {
        List<String> columns = upgrade.columns;
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


    private static void dropAllTables(SQLiteDatabase db, List<Upgrade> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            Upgrade upgrade = upgradeList.get(i);
            db.execSQL("DROP TABLE IF EXISTS \"" + upgrade.tableName + "\"");
        }
    }

    private static void createAllTables(SQLiteDatabase db, List<Upgrade> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            Upgrade upgrade = upgradeList.get(i);
            createTable(db, upgrade);
            LinkedHashMap<String, ColumnType> newFieldMap = upgrade.addColumns;
            if (newFieldMap.isEmpty()) continue;
            Iterator<Map.Entry<String, ColumnType>> iterator = newFieldMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ColumnType> entry = iterator.next();
                addColumn(db, upgrade.tableName, entry.getKey(), entry.getValue().toString());
            }
        }
    }

    private static void createTable(SQLiteDatabase db, Upgrade upgrade) {
        //判断表是否存在
        if (!tableIsExist(db, false, upgrade.tableName)) {
            String sql = upgrade.sqlCreateTable;
            db.execSQL(sql);
            printLog("【创建新表成功】\n" + sql);
        }
    }

    private static void addColumn(SQLiteDatabase db, String tableName, String columnName, String
            columnType) {
        if (tableIsExist(db, false, tableName) && !columnIsExist(db, tableName, columnName)) {
            db.execSQL("ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType);
        }
    }

    static boolean columnIsExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean result = false;

        Cursor cursor = db.rawQuery("SELECT sql FROM sqlite_master WHERE tbl_name='" + tableName +
                "' AND type='table'", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String createSql = cursor.getString(0);
                if (createSql.indexOf(fieldName) > 0) {
                    result = true;
                }
            }
            cursor.close();
        }
        return result;
    }

    static String createTableSql(SQLiteDatabase db, String tableName) {
        String createSql = "";
        Cursor cursor = db.rawQuery("SELECT sql FROM sqlite_master WHERE tbl_name=?",
                new String[]{tableName});
        if (cursor != null) {
            if (cursor.moveToNext()) {
                createSql = cursor.getString(0);
            }
            cursor.close();
        }
        return createSql;
    }

    private static void restoreData(SQLiteDatabase db, List<Upgrade> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            Upgrade upgrade = upgradeList.get(i);
            String tableName = upgrade.tableName;
            String tempTableName = tableName.concat("_TEMP");
            if (!tableIsExist(db, true, tempTableName)) {
                continue;
            }

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
                Log.e(TAG, "【临时表还原数据失败 】" + tempTableName, e);
            }
        }
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

    private static void printLog(String info) {
        if (DEBUG) {
            Log.d(TAG, info);
        }
    }
}
