package com.nylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class DbUpgrade {
    private List<Upgrade> mUpgradeList;
    private SQLiteDatabase mSQLiteDatabase;
    private int mOldVersion;

    public DbUpgrade(SQLiteDatabase db, int oldVersion) {
        this.mSQLiteDatabase = db;
        this.mOldVersion = oldVersion;
        mUpgradeList = new ArrayList<>();
    }

    public UpgradeConfig setTableName(String tableName) {
        return new UpgradeConfig(this, mSQLiteDatabase, mUpgradeList, tableName);
    }

    public void upgrade() {
        UpgradeMigration.migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
    }
}
