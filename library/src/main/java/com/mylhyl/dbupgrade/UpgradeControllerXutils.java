package com.mylhyl.dbupgrade;


/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeControllerXutils {
    private DbUpgrade.Xutils mXutils;
    private UpgradeTableXutils mUpgrade;
    private int mUpgradeVersion;

    private UpgradeControllerXutils() {
    }

    UpgradeControllerXutils(DbUpgrade.Xutils xutils) {
        this.mXutils = xutils;
    }

    /**
     * 设置创建表的 sql 语句，如多主键
     *
     * @param sqlCreateTable
     * @return
     */
    public UpgradeControllerXutils setSqlCreateTable(String sqlCreateTable) {
        mUpgrade.sqlCreateTable = sqlCreateTable;
        return this;
    }

    /**
     * 表配置结束，并配置另一个表
     *
     * @param entityType
     * @param upgradeVersion
     * @return
     */
    public UpgradeControllerXutils setEntityType(Class<?> entityType, int upgradeVersion) {
        addUpgrade();
        return mXutils.setEntityType(entityType, upgradeVersion);
    }

    /**
     * 升级
     *
     * @return
     */
    public void upgrade() {
        addUpgrade();
        if (mXutils.getOldVersion() == mUpgradeVersion) {
            mXutils.upgrade();
            mXutils.addOldVersion();
        }
        mXutils.clearUpgradeList();
    }

    UpgradeControllerXutils newUpgradeTable(Class<?> entityType, int upgradeVersion) {
        this.mUpgrade = new UpgradeTableXutils(entityType);
        this.mUpgradeVersion = upgradeVersion;
        return this;
    }

    void addUpgrade() {
        mXutils.addUpgrade(mUpgrade);
    }
}
