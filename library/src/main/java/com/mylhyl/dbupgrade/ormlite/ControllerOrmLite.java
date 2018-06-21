package com.mylhyl.dbupgrade.ormlite;


import com.j256.ormlite.table.DatabaseTableConfig;
import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.base.AbsController;
import com.mylhyl.dbupgrade.base.BaseMigration;

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
    public ControllerOrmLite addColumn(String columnName, ColumnType fieldType) {
        String tableName = DatabaseTableConfig.extractTableName(mTable.entityType);
        if (!BaseMigration.columnIsExist(mWith.getSQLiteDatabase(), tableName, columnName))
            mTable.addColumn(columnName, fieldType);
        return this;
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
