package com.mylhyl.dbupgrade.xuitls3;

import com.mylhyl.dbupgrade.base.AbsWith;

import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/16.
 */

public final class Xutils {
    private With mWith;
    private DbManager mDbManager;

    public Xutils(int oldVersion, int newVersion, DbManager db) {
        this.mWith = new With(oldVersion, newVersion);
        this.mDbManager = db;
    }

    public With setUpgradeVersion(int upgradeVersion) {
        mWith.setUpgradeVersion(upgradeVersion);
        return mWith;
    }

    public final class With extends AbsWith<TableXutils> {
        private List<TableXutils> mUpgradeList = new ArrayList<>();
        private ControllerXutils mUpgradeController;

        private With(int mOldVersion, int mNewVersion) {
            super(mOldVersion, mNewVersion);
        }

        /**
         * 设置升级表
         *
         * @param entityType
         * @return
         */
        public ControllerXutils setUpgradeTable(Class<?> entityType) {
            mUpgradeController = new ControllerXutils(this, entityType);
            return mUpgradeController;
        }

        @Override
        protected void setUpgradeVersion(int upgradeVersion) {
            mUpgradeVersion = upgradeVersion;
        }

        @Override
        protected void addUpgrade(TableXutils upgradeTable) {
            mUpgradeList.add(upgradeTable);
        }

        @Override
        protected void clearUpgradeList() {
            mUpgradeList.clear();
        }

        @Override
        protected void upgrade() {
            new MigrationXutils().migrate(mDbManager, mWith.mOldVersion, mUpgradeList);
        }
    }
}
