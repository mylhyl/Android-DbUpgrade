package com.mylhyl.dbupgrade;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.greendao.database.EncryptedDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by hupei on 2017/6/14.
 */

class BaseUpgradeMigration {
    public static boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "DbUpgrade";
    private static final String SQLITE_MASTER = "sqlite_master";
    private static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";

    private SQLiteDatabase mDatabase;
    private EncryptedDatabase mEncryptedDatabase;

    void setEncryptedDatabase(EncryptedDatabase encryptedDatabase) {
        this.mEncryptedDatabase = encryptedDatabase;
    }

    void setDatabase(SQLiteDatabase database) {
        this.mDatabase = database;
    }

    void beginTransaction() {
        if (mDatabase != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && mDatabase.isWriteAheadLoggingEnabled())
                mDatabase.beginTransactionNonExclusive();
            else mDatabase.beginTransaction();
        } else
            mEncryptedDatabase.beginTransaction();
    }

    void setTransactionSuccessful() {
        if (mDatabase != null) mDatabase.setTransactionSuccessful();
        else mEncryptedDatabase.setTransactionSuccessful();
    }

    void endTransaction() {
        if (mDatabase != null) mDatabase.endTransaction();
        else mEncryptedDatabase.endTransaction();
    }

    void generateTempTables(String tableName) {
        String tempTableName = tableName.concat("_TEMP");
        //安全起见，不管存不存在先删除临时表
        StringBuilder dropTableStringBuilder = new StringBuilder();
        dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");

        if (mDatabase != null) mDatabase.execSQL(dropTableStringBuilder.toString());
        else mEncryptedDatabase.execSQL(dropTableStringBuilder.toString());

        //创建临时表
        StringBuilder insertTableStringBuilder = new StringBuilder();
        insertTableStringBuilder.append("CREATE TEMPORARY TABLE ").append(tempTableName);
        insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";");

        if (mDatabase != null) mDatabase.execSQL(insertTableStringBuilder.toString());
        else mEncryptedDatabase.execSQL(insertTableStringBuilder.toString());

        printLog("【临时表名】" + tempTableName);

    }

//    String getColumnsStr(List<String> columns) {
//        StringBuilder builder = new StringBuilder();
//        for (String column : columns) {
//            builder.append(column);
//            builder.append(",");
//        }
//        if (builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
//        return builder.toString();
//    }

    List<String> getColumns(String tableName) {
        List<String> columns = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " limit 1";
        Cursor cursor = null;
        try {
            if (mDatabase != null) cursor = mDatabase.rawQuery(sql, null);
            else cursor = mEncryptedDatabase.rawQuery(sql, null);

            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return columns;
    }

    boolean tableIsExist(boolean isTemp, String tableName) {
        if ((mDatabase == null && mEncryptedDatabase == null) || TextUtils.isEmpty(tableName)) {
            return false;
        }
        String dbName = isTemp ? SQLITE_TEMP_MASTER : SQLITE_MASTER;
        String sql = "SELECT COUNT(*) FROM " + dbName + " WHERE type = ? AND name = ?";
        String[] selectionArgs = new String[]{"table", tableName};
        Cursor cursor = null;
        int count = 0;
        try {
            if (mDatabase != null) cursor = mDatabase.rawQuery(sql, selectionArgs);
            else cursor = mEncryptedDatabase.rawQuery(sql, selectionArgs);

            if (cursor == null || !cursor.moveToFirst()) return false;
            count = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return count > 0;
    }

    void restoreData(String tableName, String tempTableName) {
        // 取出临时表列
        List<String> tempColumns = getColumns(tempTableName);
        ArrayList<String> properties = new ArrayList<>(tempColumns.size());
        //取出新表列
        String[] newColumns = getNewColumns(tableName);
        for (String columnName : newColumns) {
            if (tempColumns.contains(columnName)) properties.add(columnName);
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

            if (mDatabase != null) mDatabase.execSQL(insertTableStringBuilder.toString());
            else mEncryptedDatabase.execSQL(insertTableStringBuilder.toString());

            printLog("【还原数据至】" + tableName);
        }
        StringBuilder dropTableStringBuilder = new StringBuilder();
        dropTableStringBuilder.append("DROP TABLE ").append(tempTableName);
        if (mDatabase != null)
            mDatabase.execSQL(dropTableStringBuilder.toString());
        printLog("【删除临时表】" + tempTableName);
    }

    private String[] getNewColumns(String tableName) {
        String[] columnNames = null;
        String sql = "PRAGMA table_info(" + tableName + ")";
        Cursor cursor = null;
        try {
            if (mDatabase != null) cursor = mDatabase.rawQuery(sql, null);
            else cursor = mEncryptedDatabase.rawQuery(sql, null);

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex("name");
                if (columnIndex == -1) return columnNames;

                int index = 0;
                columnNames = new String[cursor.getCount()];
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    columnNames[index] = cursor.getString(columnIndex);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return columnNames;
    }

    void printLog(String info) {
        if (DEBUG) Log.d(TAG, info);
    }
}
