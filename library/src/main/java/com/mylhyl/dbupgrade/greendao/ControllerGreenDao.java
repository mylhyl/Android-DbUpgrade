package com.mylhyl.dbupgrade.greendao;


import com.mylhyl.dbupgrade.base.AbsController;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/9.
 */

public final class ControllerGreenDao extends AbsController<TableGreenDao,
        Class<? extends AbstractDao<?, ?>>, GreenDao.With, ControllerGreenDao> {

    ControllerGreenDao(GreenDao.With with, Class<? extends AbstractDao<?, ?>> abstractDao
            , String sqlCreateTable) {
        this.mWith = with;
        this.mTable = new TableGreenDao(abstractDao, sqlCreateTable);
    }

    @Override
    public ControllerGreenDao setUpgradeTable(Class<? extends AbstractDao<?, ?>> abstractDao) {
        return setUpgradeTable(abstractDao, "");
    }

    @Override
    public ControllerGreenDao setUpgradeTable(Class<? extends AbstractDao<?, ?>> abstractDao
            , String sqlCreateTable) {
        addUpgrade();
        return mWith.setUpgradeTable(abstractDao, sqlCreateTable);
    }
}
