package com.mylhyl.dbupgrade;


import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeControllerGreenDao {
    private DbUpgrade.GreenDao.With mWith;
    private UpgradeTableGreenDao mUpgrade;

    UpgradeControllerGreenDao(DbUpgrade.GreenDao.With with, Class<? extends AbstractDao<?, ?>>
            abstractDao) {
        this.mWith = with;
        this.mUpgrade = new UpgradeTableGreenDao(abstractDao);
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
     * @return
     */
    public UpgradeControllerGreenDao setUpgradeTable(Class<? extends AbstractDao<?, ?>>
                                                             abstractDao) {
        if (mWith.isUpgrade()) addUpgrade();
        return mWith.setUpgradeTable(abstractDao);
    }

    /**
     * 升级
     *
     * @return
     */
    public void upgrade() {
        if (mWith.isUpgrade()) {
            addUpgrade();
            mWith.upgrade();
            mWith.addOldVersion();
        }
        mWith.clearUpgradeList();
    }

    void addUpgrade() {
        mWith.addUpgrade(mUpgrade);
    }
}
