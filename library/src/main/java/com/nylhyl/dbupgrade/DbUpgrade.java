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
    private UpgradeConfig mUpgradeConfig;

    public DbUpgrade(SQLiteDatabase db, int oldVersion) {
        this.mSQLiteDatabase = db;
        this.mOldVersion = oldVersion;
        mUpgradeList = new ArrayList<>();
    }

    /**
     * 设置需要升级的表名
     *
     * @param tableName
     * @param upgradeVersion
     * @return
     */
    public UpgradeConfig setTableName(String tableName, int upgradeVersion) {
        mUpgradeConfig = new UpgradeConfig(this, mSQLiteDatabase, mUpgradeList, tableName,
                upgradeVersion);
        return mUpgradeConfig;
    }

    public void upgrade() {
        if (mOldVersion == mUpgradeConfig.getUpgradeVersion()) {
            UpgradeMigration.migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
            mUpgradeList.clear();
            mOldVersion++;
        }
    }
}
