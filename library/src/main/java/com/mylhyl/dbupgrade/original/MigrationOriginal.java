package com.mylhyl.dbupgrade.original;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.BaseMigration;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hupei on 2017/6/9.
 */
public final class MigrationOriginal extends BaseMigration {

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

    public void migrate(SQLiteDatabase db, int oldVersion, List<TableOriginal> upgradeList) {
        SQLiteDatabase database = db;
        setDatabase(db);
        printLog("【旧数据库版本】>>>" + oldVersion);

        beginTransaction();
        try {

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

    private void generateTempTables(List<TableOriginal> upgradeList) {
        for (TableOriginal upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            String tableName = upgradeTable.tableName;
            //判断表是否存在
            if (!tableIsExist(false, tableName)) {
                printLog("【旧表不存在】" + tableName);
                return;
            }
            generateTempTables(tableName);
        }
    }

    private void dropAllTables(SQLiteDatabase db, List<TableOriginal> upgradeList) {
        for (TableOriginal upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            db.execSQL("DROP TABLE IF EXISTS \"" + upgradeTable.tableName + "\"");
        }
    }

    private void createAllTables(SQLiteDatabase db, List<TableOriginal> upgradeList) {
        for (TableOriginal upgradeTable : upgradeList) {
            String tableName = upgradeTable.tableName;
            createTable(db, tableName, upgradeTable.sqlCreateTable);
            //加入新列
            LinkedHashMap<String, ColumnType> addColumnMap = upgradeTable.addColumns;
            if (addColumnMap.isEmpty()) continue;
            Iterator<Map.Entry<String, ColumnType>> iterator = addColumnMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ColumnType> entry = iterator.next();
                addColumn(upgradeTable.tableName, entry.getKey(), entry.getValue().toString());
            }
//            List<String> columns = getColumns(tableName);
//            printLog("【表】" + tableName + "\n ---列-->" + getColumnsStr(columns));
        }
    }

    private void restoreData(List<TableOriginal> upgradeList) {
        for (TableOriginal upgradeTable : upgradeList) {
            if (!upgradeTable.migration) {
                continue;
            }
            String tableName = upgradeTable.tableName;
            String tempTableName = tableName.concat("_TEMP");
            if (!tableIsExist(true, tempTableName)) {
                printLog("【临时表不存在】" + tempTableName);
                continue;
            }
            restoreData(tableName, tempTableName);
        }
    }

    private void createTable(SQLiteDatabase db, String tableName, String sql) {
        //判断表是否存在
        if (!tableIsExist(false, tableName)) {
            db.execSQL(sql);
            printLog("【创建新表成功】\n" + sql);
        }
    }
}
