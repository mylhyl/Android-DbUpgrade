package com.mylhyl.dbupgrade;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/15.
 */
final class UpgradeTableGreenDao {
    public Class<? extends AbstractDao<?, ?>> abstractDao;
    public String sqlCreateTable;

    public UpgradeTableGreenDao(Class<? extends AbstractDao<?, ?>> abstractDao) {
        this.abstractDao = abstractDao;
    }
}
