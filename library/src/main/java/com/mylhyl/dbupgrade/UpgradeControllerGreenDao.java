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

    /**
     * 表配置结束，并配置另一个表
     *
     * @param abstractDao
     * @param upgradeVersion
     * @return
     */
    public UpgradeControllerGreenDao setAbstractDao(Class<? extends AbstractDao<?, ?>> abstractDao,
                                                    int upgradeVersion) {
        addUpgrade();
        return mGreenDao.setAbstractDao(abstractDao, upgradeVersion);
    }

    /**
     * 升级
     *
     * @return
     */
    public void upgrade() {
        addUpgrade();
        if (mGreenDao.getOldVersion() == mUpgradeVersion) {
            mGreenDao.upgrade();
            mGreenDao.addOldVersion();
        }
        mGreenDao.clearUpgradeList();
    }

    UpgradeControllerGreenDao newAbstractDao(Class<? extends AbstractDao<?, ?>> abstractDao,
                                             int upgradeVersion) {
        this.mUpgrade = new UpgradeTableGreenDao(abstractDao);
        this.mUpgradeVersion = upgradeVersion;
        return this;
    }

    DbUpgrade.GreenDao addUpgrade() {
        mGreenDao.addUpgrade(mUpgrade);
        return mGreenDao;
    }
}
