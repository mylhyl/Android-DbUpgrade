package com.mylhyl.dbupgrade.base;

import com.mylhyl.dbupgrade.ColumnType;

/**
 * Created by hupei on 2017/6/16.
 */

public abstract class AbsController<Table extends BaseTable, Name, With extends AbsWith, Controller> {
    protected With mWith;
    protected Table mTable;

    /**
     * 是否走升级策略(1建临时，2删旧表，3建新表，4还原数据)
     * 如果只是添加字段，建议 false
     *
     * @param migration true 是 false 否
     * @return Controller
     */
    public abstract Controller setMigration(boolean migration);

    /**
     * 添加列
     *
     * @param columnName 列名
     * @param fieldType  列类型
     * @return Controller
     */
    public abstract Controller addColumn(String columnName, ColumnType fieldType);

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
     * 升级
     */
    public final void upgrade() {
        if (mWith.isUpgrade()) {
            addUpgrade();
            mWith.upgrade();
            mWith.addOldVersion();
        }
        mWith.clearUpgradeList();
    }

    protected void addUpgrade() {
        if (mWith.isUpgrade())
            mWith.addUpgrade(mTable);
    }
}
