package com.mylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;

import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class DbUpgrade {
    private Native mNative;
    private Xutils mXutils;
    private int mOldVersion;

    public DbUpgrade(int oldVersion) {
        this.mOldVersion = oldVersion;

    }

    public Native with(SQLiteDatabase db) {
        mNative = new Native(db);
        return mNative;
    }

    public Xutils withXutils(DbManager db) {
        mXutils = new Xutils(db);
        return mXutils;
    }

    public final class Native {
        private SQLiteDatabase mSQLiteDatabase;
        private List<UpgradeTable> mUpgradeList = new ArrayList<>();
        private UpgradeController mUpgradeController;

        private Native() {
        }

        Native(SQLiteDatabase mSQLiteDatabase) {
            this.mSQLiteDatabase = mSQLiteDatabase;
        }

        /**
         * 设置需要升级的表名
         *
         * @param tableName
         * @param upgradeVersion 当前 tableName 从 upgradeVersion 版本升级
         * @return
         */
        public UpgradeController setTableName(String tableName, int upgradeVersion) {
            mUpgradeController = new UpgradeController(this);
            mUpgradeController.setTableName(tableName, upgradeVersion);
            return mUpgradeController;
        }

        SQLiteDatabase getSQLiteDatabase() {
            return mSQLiteDatabase;
        }

        List<UpgradeTable> getUpgradeList() {
            return mUpgradeList;
        }

        public void upgrade() {
            if (mOldVersion == mUpgradeController.getUpgradeVersion()) {
                UpgradeMigration.migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
                mOldVersion++;
            }
            mUpgradeList.clear();
        }
    }

    public final class Xutils {
        private DbManager mDbManager;
        private List<UpgradeTableXutils> mUpgradeList = new ArrayList<>();
        private UpgradeControllerXutils mUpgradeController;

        private Xutils() {
        }

        Xutils(DbManager db) {
            this.mDbManager = db;
        }

        public UpgradeControllerXutils setEntityType(Class<?> entityType, int upgradeVersion) {
            mUpgradeController = new UpgradeControllerXutils(this, mDbManager);
            mUpgradeController.setEntityType(entityType, upgradeVersion);
            return mUpgradeController;
        }

        public void upgrade() {
            if (mOldVersion == mUpgradeController.getUpgradeVersion()) {
                UpgradeMigrationXutils.migrate(mDbManager, mOldVersion, mUpgradeList);
                mOldVersion++;
            }
            mUpgradeList.clear();
        }

        List<UpgradeTableXutils> getUpgradeList() {
            return mUpgradeList;
        }
    }
}
