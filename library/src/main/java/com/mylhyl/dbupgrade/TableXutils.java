package com.mylhyl.dbupgrade;

/**
 * Created by hupei on 2017/6/14.
 */
final class TableXutils extends BaseTable {
    public Class<?> entityType;

    public TableXutils(Class<?> entityType) {
        this.entityType = entityType;
    }
}
