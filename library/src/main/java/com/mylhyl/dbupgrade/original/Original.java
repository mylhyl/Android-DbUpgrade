package com.mylhyl.dbupgrade.original;

import android.database.sqlite.SQLiteDatabase;

import com.mylhyl.dbupgrade.base.AbsWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/16.
 */

public final class Original {
    private With mWith;
    private SQLiteDatabase mSQLiteDatabase;

    public Original(int oldVersion, int newVersion, SQLiteDatabase db) {
        this.mWith = new With(oldVersion, newVersion);
        this.mSQLiteDatabase = db;
    }

    public With setUpgradeVersion(int upgradeVersion) {
        mWith.setUpgradeVersion(upgradeVersion);
        return mWith;
    }

    public final class With extends AbsWith<TableOriginal> {
        private List<TableOriginal> mUpgradeList = new ArrayList<>();
        private ControllerOriginal mUpgradeController;

        private With(int mOldVersion, int mNewVersion) {
            super(mOldVersion, mNewVersion);
        }

        @Override
        protected void setUpgradeVersion(int upgradeVersion) {
            mUpgradeVersion = upgradeVersion;
        }

        @Override
        protected void addUpgrade(TableOriginal upgradeTable) {
            mUpgradeList.add(upgradeTable);
        }

        @Override
        protected void clearUpgradeList() {
            mUpgradeList.clear();
        }

        @Override
        protected void upgrade() {
            new MigrationOriginal().migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
        }

        /**
         * 设置需要升级的表名
         *
         * @param tableName 表名
         * @return Controller
         */
        public ControllerOriginal setUpgradeTable(String tableName) {
            return setUpgradeTable(tableName, "");
        }

        /**
         * 设置需要升级的表名
         *
         * @param tableName      表名
         * @param sqlCreateTable 创建表的 sql
         * @return Controller
         */
        public ControllerOriginal setUpgradeTable(String tableName, String sqlCreateTable) {
            mUpgradeController = new ControllerOriginal(this, tableName, sqlCreateTable);
            return mUpgradeController;
        }

        protected SQLiteDatabase getSQLiteDatabase() {
            return mSQLiteDatabase;
        }
    }
}
