package com.mylhyl.dbupgrade.greendao;


import android.database.sqlite.SQLiteDatabase;

import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.AbsController;
import com.mylhyl.dbupgrade.base.BaseMigration;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

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
    public ControllerGreenDao addColumn(String columnName, ColumnType fieldType) {
        SQLiteDatabase db = mWith.getSQLiteDatabase();
        Database database = new StandardDatabase(db);
        Class<? extends AbstractDao<?, ?>> abstractDao = mTable.abstractDao;
        DaoConfig daoConfig = new DaoConfig(database, abstractDao);
        String tableName = daoConfig.tablename;
        //列不存在才添加
        if (!BaseMigration.columnIsExist(mWith.getSQLiteDatabase(), tableName, columnName))
            mTable.addColumn(columnName, fieldType);
        return this;
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
