package com.mylhyl.dbupgrade.xuitls;

import com.mylhyl.dbupgrade.DbUpgrade;


import org.xutils.DbManager;

import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeControllerXutils {
    private DbUpgrade mDbUpgrade;
    private UpgradeXutils mUpgrade;
    private DbManager mDbManager;
    private List<UpgradeXutils> mUpgradeList;
    private int mUpgradeVersion;

    private UpgradeControllerXutils() {
    }

    UpgradeControllerXutils(DbUpgrade dbUpgrade, DbManager db, int upgradeVersion) {
        this.mDbUpgrade = dbUpgrade;
        this.mDbManager = db;
        this.mUpgrade = new UpgradeXutils();
        this.mUpgradeVersion = upgradeVersion;
    }

    public UpgradeControllerXutils setSqlCreateTable(String sqlCreateTable) {
        mUpgrade.sqlCreateTable = sqlCreateTable;
        return this;
    }

    public DbUpgrade build() {
        addUpgrade(mUpgrade);
        return mDbUpgrade;
    }


    private void addUpgrade(UpgradeXutils upgrade) {
        mUpgradeList.add(upgrade);
    }

    int getUpgradeVersion() {
        return mUpgradeVersion;
    }
}
