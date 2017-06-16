package com.mylhyl.dbupgrade;

/**
 * Created by hupei on 2017/6/16.
 */

abstract class BaseController<Table extends BaseTable, Name, With extends DbUpgrade.AbsWith,
        Controller> {
    With mWith;
    Table mTable;

    /**
     * 表配置结束，并配置另一个表
     *
     * @param name
     * @return
     */
    public abstract Controller setUpgradeTable(Name name);

    /**
     * 设置创建表的 sql 语句，如多主键
     *
     * @param sqlCreateTable
     * @return
     */
    public abstract Controller setSqlCreateTable(String sqlCreateTable);


    /**
     * 升级
     *
     * @return
     */
    public final void upgrade() {
        if (mWith.isUpgrade()) {
            addUpgrade();
            mWith.upgrade();
            mWith.addOldVersion();
        }
        mWith.clearUpgradeList();
    }

    void addUpgrade() {
        mWith.addUpgrade(mTable);
    }
}
