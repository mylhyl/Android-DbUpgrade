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

    UpgradeControllerXutils setEntityType(Class<?> entityType, int upgradeVersion) {
        this.mUpgrade = new UpgradeTableXutils(entityType);
        this.mUpgradeVersion = upgradeVersion;
        return this;
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

    public DbUpgrade.Xutils build() {
        addUpgrade(mUpgrade);
        return mXutils;
    }


    private void addUpgrade(UpgradeTableXutils upgrade) {
        mXutils.getUpgradeList().add(upgrade);
    }

    int getUpgradeVersion() {
        return mUpgradeVersion;
    }
}
