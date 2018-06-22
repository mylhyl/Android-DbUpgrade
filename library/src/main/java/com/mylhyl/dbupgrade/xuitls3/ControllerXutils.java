package com.mylhyl.dbupgrade.xuitls3;


import com.mylhyl.dbupgrade.base.AbsController;

/**
 * Created by hupei on 2017/6/9.
 */

public final class ControllerXutils extends AbsController<TableXutils, Class<?>,
        Xutils.With, ControllerXutils> {

    ControllerXutils(Xutils.With with, Class<?> entityType, String sqlCreateTable) {
        this.mWith = with;
        this.mTable = new TableXutils(entityType, sqlCreateTable);
    }

    @Override
    public ControllerXutils setUpgradeTable(Class<?> entityType) {
        return setUpgradeTable(entityType, "");
    }

    @Override
    public ControllerXutils setUpgradeTable(Class<?> entityType, String sqlCreateTable) {
        addUpgrade();
        return mWith.setUpgradeTable(entityType, sqlCreateTable);
    }

}
