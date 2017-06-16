package com.mylhyl.dbupgrade;


import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/9.
 */

public final class ControllerGreenDao extends BaseController<TableGreenDao,
        Class<? extends AbstractDao<?, ?>>, DbUpgrade.GreenDao.With, ControllerGreenDao> {

    ControllerGreenDao(DbUpgrade.GreenDao.With with, Class<? extends AbstractDao<?, ?>>
            abstractDao) {
        this.mWith = with;
        this.mTable = new TableGreenDao(abstractDao);
    }


    @Override
    public ControllerGreenDao setUpgradeTable(Class<? extends AbstractDao<?, ?>> abstractDao) {
        if (mWith.isUpgrade()) addUpgrade();
        return mWith.setUpgradeTable(abstractDao);
    }

    @Override
    public ControllerGreenDao setSqlCreateTable(String sqlCreateTable) {
        mTable.sqlCreateTable = sqlCreateTable;
        return this;
    }
}
