package com.mylhyl.dbupgrade;

/**
 * Created by hupei on 2017/6/14.
 */
final class UpgradeTableXutils extends BaseUpgradeTable {
    public Class<?> entityType;

    public UpgradeTableXutils(int upgradeVersion, Class<?> entityType) {
        this.upgradeVersion = upgradeVersion;
        this.entityType = entityType;
    }
}
