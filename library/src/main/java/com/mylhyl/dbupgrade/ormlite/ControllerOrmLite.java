package com.mylhyl.dbupgrade.ormlite;


import com.mylhyl.dbupgrade.base.AbsController;

/**
 * Created by hupei on 2017/6/16.
 */

public final class ControllerOrmLite extends AbsController<TableOrmLite, Class<?>,
        OrmLite.With, ControllerOrmLite> {

    ControllerOrmLite(OrmLite.With with, Class<?> entityType, String sqlCreateTable) {
        this.mWith = with;
        this.mTable = new TableOrmLite(entityType, sqlCreateTable);
    }

    @Override
    public ControllerOrmLite setUpgradeTable(Class<?> entityType) {
        return setUpgradeTable(entityType, "");
    }

    @Override
    public ControllerOrmLite setUpgradeTable(Class<?> entityType, String sqlCreateTable) {
        addUpgrade();
        return mWith.setUpgradeTable(entityType);
    }
}
