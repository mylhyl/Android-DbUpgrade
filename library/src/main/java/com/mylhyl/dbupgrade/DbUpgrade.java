package com.mylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;

import com.mylhyl.dbupgrade.xuitls.UpgradeXutils;

import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class DbUpgrade {
    private Native mNative;

    private List<UpgradeXutils> mUpgradeXutilsList;
    private DbManager mDbManager;

    private int mOldVersion;

    public DbUpgrade(int oldVersion) {
        this.mOldVersion = oldVersion;

    }

    public Native with(SQLiteDatabase db) {
        mNative = new Native(db);
        return mNative;
    }

    public DbUpgrade withXutils(DbManager db) {
        this.mDbManager = db;
        mUpgradeXutilsList = new ArrayList<>();
        return this;
    }

    public final class Native {
        private SQLiteDatabase mSQLiteDatabase;
        private List<Upgrade> mUpgradeList = new ArrayList<>();
        private UpgradeController mUpgradeController;

        public Native(SQLiteDatabase mSQLiteDatabase) {
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

        List<Upgrade> getUpgradeList() {
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
}
