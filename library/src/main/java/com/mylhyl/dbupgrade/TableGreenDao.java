package com.mylhyl.dbupgrade;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/15.
 */
final class TableGreenDao extends BaseTable {
    public Class<? extends AbstractDao<?, ?>> abstractDao;
    public String sqlCreateTable;

    public TableGreenDao(Class<? extends AbstractDao<?, ?>> abstractDao) {
        this.abstractDao = abstractDao;
    }
}
