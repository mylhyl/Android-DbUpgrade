package com.mylhyl.dbupgrade.greendao;

import com.mylhyl.dbupgrade.base.BaseTable;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by hupei on 2017/6/15.
 */
final class TableGreenDao extends BaseTable {
    public Class<? extends AbstractDao<?, ?>> abstractDao;

    public TableGreenDao(Class<? extends AbstractDao<?, ?>> abstractDao, String sqlCreateTable) {
        this.abstractDao = abstractDao;
        this.sqlCreateTable = sqlCreateTable;
    }
}
