package com.nylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class UpgradeConfig {
    private DbUpgrade mDbUpgrade;
    private Upgrade mUpgrade;
    private SQLiteDatabase mSQLiteDatabase;
    private List<Upgrade> mUpgradeList;
    private int mUpgradeVersion;

    private UpgradeConfig() {
    }

    UpgradeConfig(DbUpgrade dbUpgrade, SQLiteDatabase db,
                  List<Upgrade> upgradeList, String tableName, int upgradeVersion) {
        this.mDbUpgrade = dbUpgrade;
        this.mSQLiteDatabase = db;
        this.mUpgradeList = upgradeList;
        this.mUpgrade = new Upgrade(tableName);
        this.mUpgradeVersion = upgradeVersion;
    }

    public UpgradeConfig addColumn(String fieldName, ColumnType fieldType) {
        mUpgrade.addColumn(fieldName, fieldType);
        return this;
    }

    public UpgradeConfig setSqlCreateTable(String sqlCreateTable) {
        mUpgrade.sqlCreateTable = sqlCreateTable;
        return this;
    }

    public DbUpgrade build() {
        if (isUpgrade()) {
            setSqlCreateTable();
            findColumnAll();
            addUpgrade(mUpgrade);
        }
        return mDbUpgrade;
    }

    private void setSqlCreateTable() {
        if (TextUtils.isEmpty(mUpgrade.sqlCreateTable))
            mUpgrade.sqlCreateTable = UpgradeMigration.createTableSql(mSQLiteDatabase, mUpgrade
                    .tableName);
    }

    private void findColumnAll() {
        List<String> columns = UpgradeMigration.getColumns(mSQLiteDatabase, mUpgrade.tableName);
        mUpgrade.columns = columns;
    }

    private boolean isUpgrade() {
        boolean result = false;
        LinkedHashMap<String, ColumnType> newFieldMap = mUpgrade.addColumns;
        Iterator<String> iterator = newFieldMap.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            boolean isExist = UpgradeMigration.columnIsExist(mSQLiteDatabase, mUpgrade.tableName,
                    column);
            //只要有一个列不存在，则需要更新
            if (!isExist) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void addUpgrade(Upgrade upgrade) {
        mUpgradeList.add(upgrade);
    }

    int getUpgradeVersion() {
        return mUpgradeVersion;
    }
}
