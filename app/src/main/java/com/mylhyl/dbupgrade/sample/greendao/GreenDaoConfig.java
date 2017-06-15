package com.mylhyl.dbupgrade.sample.greendao;

import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.mylhyl.dbupgrade.sample.App;

import java.io.File;

/**
 * Created by hupei on 2017/6/15.
 */

public class GreenDaoConfig extends ContextWrapper {
    private File dbDir;
    private String dbName = "greenDao.db"; // default db name

    public GreenDaoConfig() {
        super(App.getInstance());
    }

    public GreenDaoConfig setDbDir(String dbDirPath) {
        return setDbDir(new File(dbDirPath));
    }

    public GreenDaoConfig setDbDir(File dbDir) {
        this.dbDir = dbDir;
        return this;
    }

    public GreenDaoConfig setDbName(String dbName) {
        if (!TextUtils.isEmpty(dbName)) {
            this.dbName = dbName;
        }
        return this;
    }

    public String getDbName() {
        return dbName;
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象
     *
     * @param name
     */
    @Override
    public File getDatabasePath(String name) {
        if (dbDir != null && (dbDir.exists() || dbDir.mkdirs())) {
            return new File(dbDir, getDbName());
        }
        return super.getDatabasePath(name);
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param name
     * @param mode
     * @param factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase
            .CursorFactory factory) {
        if (dbDir != null) {
            return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        }
        return super.openOrCreateDatabase(name, mode, factory);
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @param name
     * @param mode
     * @param factory
     * @param errorHandler
     * @see android.content.ContextWrapper#openOrCreateDatabase(java.lang.String, int,
     * android.database.sqlite.SQLiteDatabase.CursorFactory,
     * android.database.DatabaseErrorHandler)
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase
            .CursorFactory factory, DatabaseErrorHandler errorHandler) {
        if (dbDir != null) {
            return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), factory);
        }
        return super.openOrCreateDatabase(name, mode, factory, errorHandler);
    }
}
