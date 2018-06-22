package com.mylhyl.dbupgrade.base;

import com.mylhyl.dbupgrade.ColumnType;

/**
 * Created by hupei on 2017/6/16.
 */

public abstract class AbsController<Table extends BaseTable, Name, With extends AbsWith
        , Controller extends AbsController> {
    protected With mWith;
    protected Table mTable;

    /**
     * 表配置结束，并配置另一个表
     *
     * @param name name
     * @return Controller
     */
    public abstract Controller setUpgradeTable(Name name);

    /**
     * 表配置结束，并配置另一个表
     *
     * @param name           name
     * @param sqlCreateTable 创建表的 sql 语句，如多主键
     * @return Controller
     */
    public abstract Controller setUpgradeTable(Name name, String sqlCreateTable);

    /**
     * 升级，建议有删除表字段使用
     * 策略(1建临时，2删旧表，3建新表，4还原数据)
     */
    public final void upgrade() {
        upgrade(true);
    }

    /**
     * 升级
     * migration 为 true 走升级策略(1建临时，2删旧表，3建新表，4还原数据)
     * 建议表只有添加字段设置 false 提高效率，但 addColumn 为空也会走升级策略
     *
     * @param migration 升级策略
     */
    public final void upgrade(boolean migration) {
        if (mWith.isUpgrade()) {
            addUpgrade();
            mTable.migration = migration;
            mWith.upgrade();
            mWith.addOldVersion();
        }
        mWith.clearUpgradeList();
    }

    protected void addUpgrade() {
        if (mWith.isUpgrade())
            mWith.addUpgrade(mTable);
    }

    /**
     * 添加列
     *
     * @param columnName 列名
     * @param fieldType  列类型
     * @return Controller
     */
    public final Controller addColumn(String columnName, ColumnType fieldType) {
        mTable.addColumn(columnName, fieldType);
        return (Controller) this;
    }
}
