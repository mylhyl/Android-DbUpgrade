package com.mylhyl.dbupgrade;

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
final class UpgradeMigration extends BaseUpgradeMigration {

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
            generateTempTables(db, tempTableName, tableName, upgrade.columns);
        }
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
            //加入新列
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
                printLog("【临时表不存在】" + tempTableName);
                continue;
            }
            restoreData(db, tableName, tempTableName);
        }
    }

}
