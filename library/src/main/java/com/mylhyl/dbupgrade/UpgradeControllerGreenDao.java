package com.mylhyl.dbupgrade;


import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeControllerGreenDao {
    private DbUpgrade.GreenDao mGreenDao;
    private UpgradeTableGreenDao mUpgrade;
    private Database mDatabase;
    private int mUpgradeVersion;

    private UpgradeControllerGreenDao() {
    }

    UpgradeControllerGreenDao(DbUpgrade.GreenDao greenDao, Database db) {
        this.mGreenDao = greenDao;
        this.mDatabase = db;
    }

    UpgradeControllerGreenDao setAbstractDao(Class<? extends AbstractDao<?, ?>> entityType,
                                             int upgradeVersion) {
        this.mUpgrade = new UpgradeTableGreenDao(entityType);
        this.mUpgradeVersion = upgradeVersion;
        return this;
    }

    public UpgradeControllerGreenDao setSqlCreateTable(String sqlCreateTable) {
        mUpgrade.sqlCreateTable = sqlCreateTable;
        return this;
    }

    public DbUpgrade.GreenDao build() {
        addUpgrade(mUpgrade);
        return mGreenDao;
    }


    private void addUpgrade(UpgradeTableGreenDao upgrade) {
        mGreenDao.getUpgradeList().add(upgrade);
    }

    int getUpgradeVersion() {
        return mUpgradeVersion;
    }
}
