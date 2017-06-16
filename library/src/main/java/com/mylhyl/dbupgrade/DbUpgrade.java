package com.mylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.mylhyl.dbupgrade.greendao.GreenDao;
import com.mylhyl.dbupgrade.original.Original;
import com.mylhyl.dbupgrade.ormlite.OrmLite;
import com.mylhyl.dbupgrade.xuitls3.Xutils;

import org.greenrobot.greendao.database.Database;
import org.xutils.DbManager;

/**
 * Created by hupei on 2017/6/9.
 */

public final class DbUpgrade {
    private int mOldVersion, mNewVersion;

    public DbUpgrade(int oldVersion, int newVersion) {
        this.mOldVersion = oldVersion;
        this.mNewVersion = newVersion;
    }

    /**
     * 原生
     *
     * @param db
     * @return
     */
    public Original with(SQLiteDatabase db) {
        return new Original(mOldVersion, mNewVersion, db);
    }

    /**
     * xutils3 框架
     *
     * @param db
     * @return
     */
    public Xutils withXutils(DbManager db) {
        return new Xutils(mOldVersion, mNewVersion, db);
    }

    /**
     * greenDao 框架
     *
     * @param db
     * @return
     */
    public GreenDao withGreenDao(Database db) {
        return new GreenDao(mOldVersion, mNewVersion, db);
    }

    /**
     * greenDao 框架
     *
     * @param db
     * @return
     */
    public GreenDao withGreenDao(SQLiteDatabase db) {
        return new GreenDao(mOldVersion, mNewVersion, db);
    }

    /**
     * OrmLite 框架
     *
     * @param db
     * @param connectionSource
     * @return
     */
    public OrmLite withOrmLite(SQLiteDatabase db, ConnectionSource connectionSource) {
        return new OrmLite(mOldVersion, mNewVersion, db, connectionSource);
    }
}
