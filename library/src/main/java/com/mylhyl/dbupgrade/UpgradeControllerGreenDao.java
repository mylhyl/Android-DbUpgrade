package com.mylhyl.dbupgrade;


import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeControllerGreenDao {
    private DbUpgrade.GreenDao mGreenDao;
    private UpgradeTableGreenDao mUpgrade;
    private int mUpgradeVersion;

    private UpgradeControllerGreenDao() {
    }

    UpgradeControllerGreenDao(DbUpgrade.GreenDao greenDao) {
        this.mGreenDao = greenDao;
    }

    UpgradeControllerGreenDao setAbstractDao(Class<? extends AbstractDao<?, ?>> abstractDao,
                                             int upgradeVersion) {
        this.mUpgrade = new UpgradeTableGreenDao(abstractDao);
        this.mUpgradeVersion = upgradeVersion;
        return this;
    }

    /**
     * 设置创建表的 sql 语句，如多主键
     *
     * @param sqlCreateTable
     * @return
     */
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
