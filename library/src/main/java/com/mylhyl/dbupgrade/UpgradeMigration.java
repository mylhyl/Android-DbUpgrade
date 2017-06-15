package com.mylhyl.dbupgrade;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库升级工具类
 * Created by hupei on 2017/6/9.
 */
final class UpgradeMigration extends BaseUpgradeMigration {

    public void migrate(SQLiteDatabase db, int oldVersion, List<UpgradeTable> upgradeList) {
        SQLiteDatabase database = db;
        setDatabase(db);
        beginTransaction();
        try {
            printLog("【旧数据库版本】>>>" + oldVersion);

            //step:1
            printLog("【创建临时表】>>>开始");
            generateTempTables(upgradeList);
            printLog("【创建临时表】>>>完成");

            //step:2
            dropAllTables(database, upgradeList);
            printLog("【删除旧表完成】");

            //step:3
            createAllTables(database, upgradeList);
            printLog("【创建新表完成】");

            //step:4
            printLog("【还原数据】开始");
            restoreData(upgradeList);
            printLog("【还原数据】完成");

            setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void generateTempTables(List<UpgradeTable> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            UpgradeTable upgrade = upgradeList.get(i);
            String tableName = upgrade.tableName;
            //判断表是否存在
            if (!tableIsExist(false, tableName)) {
                printLog("【旧表不存在】" + tableName);
                return;
            }
            generateTempTables(tableName);
        }
    }

    private void dropAllTables(SQLiteDatabase db, List<UpgradeTable> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            UpgradeTable upgrade = upgradeList.get(i);
            db.execSQL("DROP TABLE IF EXISTS \"" + upgrade.tableName + "\"");
        }
    }

    private void createAllTables(SQLiteDatabase db, List<UpgradeTable> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            UpgradeTable upgrade = upgradeList.get(i);
            String tableName = upgrade.tableName;
            createTable(db, tableName, upgrade.sqlCreateTable);
            //加入新列
            LinkedHashMap<String, ColumnType> addColumnMap = upgrade.addColumns;
            if (addColumnMap.isEmpty()) continue;
            Iterator<Map.Entry<String, ColumnType>> iterator = addColumnMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ColumnType> entry = iterator.next();
                addColumn(db, upgrade.tableName, entry.getKey(), entry.getValue().toString());
            }
//            List<String> columns = getColumns(tableName);
//            printLog("【表】" + tableName + "\n ---列-->" + getColumnsStr(columns));
        }
    }

    private void createTable(SQLiteDatabase db, String tableName, String sql) {
        //判断表是否存在
        if (!tableIsExist(false, tableName)) {
            db.execSQL(sql);
            printLog("【创建新表成功】\n" + sql);
        }
    }

    private void addColumn(SQLiteDatabase db, String tableName, String columnName, String
            columnType) {
        if (!columnIsExist(db, tableName, columnName))
            db.execSQL("ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType);
    }

    static boolean columnIsExist(SQLiteDatabase db, String tableName, String fieldName) {
        boolean result = false;
        Cursor cursor = db.rawQuery("SELECT sql FROM sqlite_master WHERE tbl_name='" + tableName +
                "' AND type='table'", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String createSql = cursor.getString(0);
                if (createSql.indexOf(fieldName) > 0) result = true;
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
            if (cursor.moveToNext()) createSql = cursor.getString(0);
            cursor.close();
        }
        return createSql;
    }

    private void restoreData(List<UpgradeTable> upgradeList) {
        int size = upgradeList.size();
        for (int i = 0; i < size; i++) {
            UpgradeTable upgrade = upgradeList.get(i);
            String tableName = upgrade.tableName;
            String tempTableName = tableName.concat("_TEMP");
            if (!tableIsExist(true, tempTableName)) {
                printLog("【临时表不存在】" + tempTableName);
                continue;
            }
            restoreData(tableName, tempTableName);
        }
    }
}
