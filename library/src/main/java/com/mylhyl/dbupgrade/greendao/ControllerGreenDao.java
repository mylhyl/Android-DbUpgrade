package com.mylhyl.dbupgrade.greendao;


import com.mylhyl.dbupgrade.base.AbsController;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/9.
 */

public final class ControllerGreenDao extends AbsController<TableGreenDao,
        Class<? extends AbstractDao<?, ?>>, GreenDao.With, ControllerGreenDao> {

    ControllerGreenDao(GreenDao.With with, Class<? extends AbstractDao<?, ?>>
            abstractDao) {
        this.mWith = with;
        this.mTable = new TableGreenDao(abstractDao);
    }


    @Override
    public ControllerGreenDao setUpgradeTable(Class<? extends AbstractDao<?, ?>> abstractDao) {
        addUpgrade();
        return mWith.setUpgradeTable(abstractDao);
    }

    @Override
    public ControllerGreenDao setSqlCreateTable(String sqlCreateTable) {
        mTable.sqlCreateTable = sqlCreateTable;
        return this;
    }
}
