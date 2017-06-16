package com.mylhyl.dbupgrade.base;

/**
 * Created by hupei on 2017/6/16.
 */

public abstract class AbsController<Table extends BaseTable, Name, With extends AbsWith,
        Controller> {
    protected With mWith;
    protected Table mTable;

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

    protected void addUpgrade() {
        if (mWith.isUpgrade())
            mWith.addUpgrade(mTable);
    }
}
