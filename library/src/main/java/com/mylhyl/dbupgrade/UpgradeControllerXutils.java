package com.mylhyl.dbupgrade;


/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeControllerXutils {
    private DbUpgrade.Xutils.With mWith;
    private UpgradeTableXutils upgradeTable;

    UpgradeControllerXutils(DbUpgrade.Xutils.With with, Class<?> entityType) {
        this.mWith = with;
        this.upgradeTable = new UpgradeTableXutils(entityType);
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
     * @return
     */
    public UpgradeControllerXutils setUpgradeTable(Class<?> entityType) {
        if (mWith.isUpgrade()) addUpgrade();
        return mWith.setUpgradeTable(entityType);
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
        mWith.addUpgrade(upgradeTable);
    }
}
