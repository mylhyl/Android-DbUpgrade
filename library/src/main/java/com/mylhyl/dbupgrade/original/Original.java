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

    public final class With extends AbsWith<Table> {
        private List<Table> mUpgradeList = new ArrayList<>();
        private Controller mUpgradeController;

        private With(int mOldVersion, int mNewVersion) {
            super(mOldVersion, mNewVersion);
        }

        @Override
        protected void setUpgradeVersion(int upgradeVersion) {
            mUpgradeVersion = upgradeVersion;
        }

        @Override
        protected void addUpgrade(Table upgradeTable) {
            mUpgradeList.add(upgradeTable);
        }

        @Override
        protected void clearUpgradeList() {
            mUpgradeList.clear();
        }

        @Override
        protected void upgrade() {
            new Migration().migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
        }

        /**
         * 设置需要升级的表名
         *
         * @param tableName 表名
         * @return Controller
         */
        public Controller setUpgradeTable(String tableName) {
            return setUpgradeTable(tableName, "");
        }

        /**
         * 设置需要升级的表名
         *
         * @param tableName      表名
         * @param sqlCreateTable 创建表的 sql
         * @return Controller
         */
        public Controller setUpgradeTable(String tableName, String sqlCreateTable) {
            mUpgradeController = new Controller(this, tableName, sqlCreateTable);
            return mUpgradeController;
        }

        protected SQLiteDatabase getSQLiteDatabase() {
            return mSQLiteDatabase;
        }
    }
}
