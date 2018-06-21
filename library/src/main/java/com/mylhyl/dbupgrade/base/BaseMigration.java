package com.mylhyl.dbupgrade.base;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.mylhyl.dbupgrade.BuildConfig;

import org.greenrobot.greendao.database.EncryptedDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by hupei on 2017/6/14.
 */

public class BaseMigration {
    private static final String TAG = "DbUpgrade";
    private static final String SQLITE_MASTER = "sqlite_master";
    private static final String SQLITE_TEMP_MASTER = "sqlite_temp_master";
    public static boolean DEBUG = BuildConfig.DEBUG;
    private SQLiteDatabase mDatabase;
    private EncryptedDatabase mEncryptedDatabase;

    protected void setEncryptedDatabase(EncryptedDatabase encryptedDatabase) {
        this.mEncryptedDatabase = encryptedDatabase;
    }

    protected void setDatabase(SQLiteDatabase database) {
        this.mDatabase = database;
    }

    protected void beginTransaction() {
        if (mDatabase != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && mDatabase.isWriteAheadLoggingEnabled())
                mDatabase.beginTransactionNonExclusive();
            else mDatabase.beginTransaction();
        } else
            mEncryptedDatabase.beginTransaction();
    }

    protected void setTransactionSuccessful() {
        if (mDatabase != null) mDatabase.setTransactionSuccessful();
        else mEncryptedDatabase.setTransactionSuccessful();
    }

    protected void endTransaction() {
        if (mDatabase != null) mDatabase.endTransaction();
        else mEncryptedDatabase.endTransaction();
    }

    protected void generateTempTables(String tableName) {
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

    protected void printLog(String info) {
        if (DEBUG) Log.d(TAG, info);
    }

    protected boolean tableIsExist(boolean isTemp, String tableName) {
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

    protected void restoreData(String tableName, String tempTableName) {
        // 取出临时表列
        List<String> tempColumns = getColumns(tempTableName);
        ArrayList<String> properties = new ArrayList<>(tempColumns.size());
        //取出新表列
        String[] newColumns = getNewColumns(tableName);
        //不为空的字段<字段名，数据类型>
        HashMap<String, String> propertiesNotNull = new HashMap<>();
        for (String columnName : newColumns) {
            //旧表中有的才添加，没有的则是新加的，新加的要判断是不是不能为空
            if (tempColumns.contains(columnName)) {
                properties.add("`" + columnName + "`");
            } else {
                HashMap<String, String> columnInfo = getColumnInfo(tableName, columnName);
                if (!columnInfo.isEmpty()) {
                    propertiesNotNull.putAll(columnInfo);
                }
            }
        }

        if (properties.size() > 0) {
            final String columnSQL = TextUtils.join(",", properties);
            //还原数据
            StringBuilder insertTableStringBuilder = new StringBuilder();
            insertTableStringBuilder.append("REPLACE INTO ").append(tableName).append(" (");
            insertTableStringBuilder.append(columnSQL);
            //处理新加且不能为空的字段
            if (!propertiesNotNull.isEmpty()) {
                Iterator<Map.Entry<String, String>> it = propertiesNotNull.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    insertTableStringBuilder.append(",'").append(entry.getKey()).append("'");
                }
            }
            insertTableStringBuilder.append(") SELECT ");
            insertTableStringBuilder.append(columnSQL);
            //处理新加且不能为空的字段
            if (!propertiesNotNull.isEmpty()) {
                Iterator<Map.Entry<String, String>> it = propertiesNotNull.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    if (entry.getValue().equalsIgnoreCase("TEXT")) {
                        insertTableStringBuilder.append(",'").append("'");
                    } else {
                        insertTableStringBuilder.append(",").append(0);
                    }
                }
            }
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

    private HashMap<String, String> getColumnInfo(String tableName, String columnName) {
        HashMap<String, String> result = new HashMap<>();
        String sql = "PRAGMA table_info(" + tableName + ")";
        Cursor cursor = null;
        try {
            if (mDatabase != null) cursor = mDatabase.rawQuery(sql, null);
            else cursor = mEncryptedDatabase.rawQuery(sql, null);

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex("name");
                if (columnIndex == -1) return result;

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String name = cursor.getString(columnIndex);
                    if (name.equalsIgnoreCase(columnName)) {
                        int notnull = cursor.getInt(cursor.getColumnIndex("notnull"));
                        if (notnull == 1) {
                            String dataType = cursor.getString(cursor.getColumnIndex("type"));
                            result.put(columnName, dataType);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }
}
