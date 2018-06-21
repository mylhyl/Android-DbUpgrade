package com.mylhyl.dbupgrade.xuitls3;


import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.AbsController;
import com.mylhyl.dbupgrade.base.BaseMigration;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;

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
    public ControllerXutils addColumn(String columnName, ColumnType fieldType) {
        DbManager dbManager = mWith.getDbManager();
        TableEntity table = null;
        try {
            table = dbManager.getTable(mTable.entityType);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (table != null) {
            String tableName = table.getName();
            //列不存在才添加
            if (!BaseMigration.columnIsExist(dbManager.getDatabase(), tableName, columnName))
                mTable.addColumn(columnName, fieldType);
        }
        return this;
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
