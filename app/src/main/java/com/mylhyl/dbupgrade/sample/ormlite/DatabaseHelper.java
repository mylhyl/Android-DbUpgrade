package com.mylhyl.dbupgrade.sample.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mylhyl.dbupgrade.DbUpgrade;
import com.mylhyl.dbupgrade.ormlite.OrmLite;

import java.sql.SQLException;

/**
 * Created by hupei on 2017/6/16.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "sqlite-test.db";
    public static int DB_VERSION = 1;

    public static synchronized DatabaseHelper getHelper(Context context) {
        return new DatabaseHelper(context);
    }

    private Dao<User, Integer> userDao;
    private Dao<User1, Integer> user1Dao;
    private Dao<User2, Integer> user2Dao;

    public Dao<User, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    public Dao<User1, Integer> getUser1Dao() throws SQLException {
        if (user1Dao == null) {
            user1Dao = getDao(User1.class);
        }
        return user1Dao;
    }

    public Dao<User2, Integer> getUser2Dao() throws SQLException {
        if (user2Dao == null) {
            user2Dao = getDao(User2.class);
        }
        return user2Dao;
    }

    @Override
    public void close() {
        super.close();
        userDao = null;
    }

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        if (newVersion <= oldVersion) return;

        DbUpgrade dbUpgrade = new DbUpgrade(oldVersion, newVersion);
        OrmLite ormLite = dbUpgrade.withOrmLite(db, connectionSource);
        ormLite.setUpgradeVersion(1).setUpgradeTable(User1.class).upgrade();
        ormLite.setUpgradeVersion(2).setUpgradeTable(User2.class).upgrade();
    }
}
