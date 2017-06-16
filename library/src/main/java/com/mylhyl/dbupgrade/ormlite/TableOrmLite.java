package com.mylhyl.dbupgrade.ormlite;

import com.mylhyl.dbupgrade.base.BaseTable;

/**
 * Created by hupei on 2017/6/16.
 */
final class TableOrmLite extends BaseTable {
    public Class<?> entityType;

    public TableOrmLite(Class<?> entityType) {
        this.entityType = entityType;
    }
}
