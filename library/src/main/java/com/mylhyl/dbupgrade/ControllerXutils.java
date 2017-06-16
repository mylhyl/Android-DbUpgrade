package com.mylhyl.dbupgrade;


/**
 * Created by hupei on 2017/6/9.
 */

public final class ControllerXutils extends BaseController<TableXutils, Class<?>,
        DbUpgrade.Xutils.With, ControllerXutils> {

    ControllerXutils(DbUpgrade.Xutils.With with, Class<?> entityType) {
        this.mWith = with;
        this.mTable = new TableXutils(entityType);
    }


    @Override
    public ControllerXutils setUpgradeTable(Class<?> entityType) {
        if (mWith.isUpgrade()) addUpgrade();
        return mWith.setUpgradeTable(entityType);
    }

    @Override
    public ControllerXutils setSqlCreateTable(String sqlCreateTable) {
        mTable.sqlCreateTable = sqlCreateTable;
        return this;
    }

}
