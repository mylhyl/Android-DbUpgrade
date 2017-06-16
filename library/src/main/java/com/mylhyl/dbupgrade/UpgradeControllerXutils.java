package com.mylhyl.dbupgrade;


/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeControllerXutils {
    private DbUpgrade.Xutils mXutils;
    private UpgradeTableXutils upgradeTable;

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
        upgradeTable.sqlCreateTable = sqlCreateTable;
        return this;
    }

    /**
     * 表配置结束，并配置另一个表
     *
     * @param entityType
     * @param upgradeVersion
     * @return
     */
    public UpgradeControllerXutils setUpgradeTable(Class<?> entityType, int upgradeVersion) {
        if (isUpgrade()) addUpgrade();
        return mXutils.setUpgradeTable(entityType, upgradeVersion);
    }

    /**
     * 升级
     *
     * @return
     */
    public void upgrade() {
        if (isUpgrade()) {
            addUpgrade();
            mXutils.upgrade();
            mXutils.addOldVersion();
        }
        mXutils.clearUpgradeList();
    }

    private boolean isUpgrade() {
        return mXutils.getOldVersion() == upgradeTable.upgradeVersion
                && upgradeTable.upgradeVersion < mXutils.getNewVersion();
    }

    UpgradeControllerXutils newUpgradeTable(Class<?> entityType, int upgradeVersion) {
        this.upgradeTable = new UpgradeTableXutils(upgradeVersion, entityType);
        return this;
    }

    void addUpgrade() {
        mXutils.addUpgrade(upgradeTable);
    }
}
