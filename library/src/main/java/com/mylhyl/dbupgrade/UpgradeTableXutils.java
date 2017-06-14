package com.mylhyl.dbupgrade;

/**
 * Created by hupei on 2017/6/14.
 */
final class UpgradeTableXutils {
    public Class<?> entityType;
    public String sqlCreateTable;

    public UpgradeTableXutils(Class<?> entityType) {
        this.entityType = entityType;
    }
}
