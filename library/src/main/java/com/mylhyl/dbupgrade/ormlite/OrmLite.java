package com.mylhyl.dbupgrade.ormlite;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.support.ConnectionSource;
import com.mylhyl.dbupgrade.base.AbsWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/16.
 */

public final class OrmLite {
    private With mWith;
    private SQLiteDatabase mDatabase;
    private ConnectionSource mConnectionSource;

    public OrmLite(int oldVersion, int newVersion, SQLiteDatabase db, ConnectionSource
            connectionSource) {
        this.mWith = new With(oldVersion, newVersion);
        this.mDatabase = db;
        this.mConnectionSource = connectionSource;
    }

    public With setUpgradeVersion(int upgradeVersion) {
        mWith.setUpgradeVersion(upgradeVersion);
        return mWith;
    }

    public final class With extends AbsWith<TableOrmLite> {
        private List<TableOrmLite> mUpgradeList = new ArrayList<>();
        private ControllerOrmLite mUpgradeController;

        private With(int mOldVersion, int mNewVersion) {
            super(mOldVersion, mNewVersion);
        }

        /**
         * 设置升级表
         *
         * @param entityType entityType
         * @return ControllerOrmLite
         */
        public ControllerOrmLite setUpgradeTable(Class<?> entityType) {
            return setUpgradeTable(entityType, "");
        }

        /**
         * 设置升级表
         *
         * @param entityType     entityType
         * @param sqlCreateTable sqlCreateTable
         * @return ControllerOrmLite
         */
        public ControllerOrmLite setUpgradeTable(Class<?> entityType, String sqlCreateTable) {
            mUpgradeController = new ControllerOrmLite(this, entityType, sqlCreateTable);
            return mUpgradeController;
        }

        @Override
        protected void setUpgradeVersion(int upgradeVersion) {
            mUpgradeVersion = upgradeVersion;
        }

        @Override
        protected void addUpgrade(TableOrmLite upgradeTable) {
            mUpgradeList.add(upgradeTable);
        }

        @Override
        protected void clearUpgradeList() {
            mUpgradeList.clear();
        }

        @Override
        protected void upgrade() {
            new MigrationOrmLite().migrate(mDatabase, mConnectionSource, mWith.mOldVersion,
                    mUpgradeList);
        }
    }
}
