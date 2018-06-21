package com.mylhyl.dbupgrade.greendao;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.BaseMigration;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.EncryptedDatabase;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hupei on 2017/6/15.
 */
final class MigrationGreenDao extends BaseMigration {

    public void migrate(SQLiteDatabase db, int oldVersion, List<TableGreenDao> upgradeList) {
        Database database = new StandardDatabase(db);
        migrate(database, oldVersion, upgradeList);
    }

    public void migrate(Database database, int oldVersion, List<TableGreenDao> upgradeList) {
        if (database instanceof StandardDatabase)
            setDatabase(((StandardDatabase) database).getSQLiteDatabase());
        else if (database instanceof EncryptedDatabase)
            setEncryptedDatabase((EncryptedDatabase) database);

        printLog("【The Old Database Version】" + oldVersion);

        migrate(database, upgradeList);
    }

    private void migrate(Database database, List<TableGreenDao> upgradeList) {
        beginTransaction();
        try {
            printLog("【Generate temp table】start");
            generateTempTables(database, upgradeList);
            printLog("【Generate temp table】complete");

            dropAllTables(database, upgradeList);
            createAllTables(database, upgradeList);

            printLog("【Restore data】start");
            restoreData(database, upgradeList);
            printLog("【Restore data】complete");

            setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }
    }

    private void generateTempTables(Database db, List<TableGreenDao> upgradeList) {
        for (TableGreenDao upgradeTable : upgradeList) {
            if (!upgradeTable.migration && !upgradeTable.addColumns.isEmpty()) {
                continue;
            }
            Class<? extends AbstractDao<?, ?>> abstractDao = upgradeTable.abstractDao;
            DaoConfig daoConfig = new DaoConfig(db, abstractDao);
            String tableName = daoConfig.tablename;
            if (!tableIsExist(false, tableName)) {
                printLog("【New Table】" + tableName);
                continue;
            }
            generateTempTables(tableName);

        }
    }


    private void dropAllTables(Database db, List<TableGreenDao> upgradeList) {
        for (TableGreenDao upgradeTable : upgradeList) {
            if (!upgradeTable.migration && !upgradeTable.addColumns.isEmpty()) {
                continue;
            }
            if (TextUtils.isEmpty(upgradeTable.sqlCreateTable)) {
                reflectMethod(db, "dropTable", true, upgradeTable);
            } else {
                Class<? extends AbstractDao<?, ?>> abstractDao = upgradeTable.abstractDao;
                DaoConfig daoConfig = new DaoConfig(db, abstractDao);
                String tableName = daoConfig.tablename;
                String sql = "DROP TABLE IF EXISTS " + tableName;
                db.execSQL(sql);
            }
        }
        printLog("【Drop all table】");
    }

    private void createAllTables(Database db, List<TableGreenDao> upgradeList) {
        for (TableGreenDao upgradeTable : upgradeList) {
            if (!upgradeTable.migration && !upgradeTable.addColumns.isEmpty()) {
                Class<? extends AbstractDao<?, ?>> abstractDao = upgradeTable.abstractDao;
                DaoConfig daoConfig = new DaoConfig(db, abstractDao);
                String tableName = daoConfig.tablename;
                if (tableIsExist(false, tableName)) {
                    //加入新列
                    LinkedHashMap<String, ColumnType> addColumnMap = upgradeTable.addColumns;
                    Iterator<Map.Entry<String, ColumnType>> iterator = addColumnMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, ColumnType> entry = iterator.next();
                        addColumn(tableName, entry.getKey(), entry.getValue().toString());
                    }
                }
                continue;
            }
            if (TextUtils.isEmpty(upgradeTable.sqlCreateTable))
                reflectMethod(db, "createTable", false, upgradeTable);
            else db.execSQL(upgradeTable.sqlCreateTable);
        }
        printLog("【Create all table】");
    }

    private void restoreData(Database db, List<TableGreenDao> upgradeList) {
        for (TableGreenDao upgradeTable : upgradeList) {
            if (!upgradeTable.migration && !upgradeTable.addColumns.isEmpty()) {
                continue;
            }
            Class<? extends AbstractDao<?, ?>> abstractDao = upgradeTable.abstractDao;
            DaoConfig daoConfig = new DaoConfig(db, abstractDao);
            String tableName = daoConfig.tablename;
            String tempTableName = daoConfig.tablename.concat("_TEMP");

            if (!tableIsExist(true, tempTableName)) {
                continue;
            }
            restoreData(tableName, tempTableName);
        }
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private void reflectMethod(Database db, String methodName, boolean isExists,
                               TableGreenDao upgradeTable) {
        try {
            Class<? extends AbstractDao<?, ?>> abstractDao = upgradeTable.abstractDao;
            Method method = abstractDao.getDeclaredMethod(methodName, Database.class, boolean
                    .class);
            method.invoke(null, db, isExists);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
