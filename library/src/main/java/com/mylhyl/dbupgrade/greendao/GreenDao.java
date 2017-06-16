package com.mylhyl.dbupgrade.greendao;

import android.database.sqlite.SQLiteDatabase;

import com.mylhyl.dbupgrade.base.AbsWith;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/16.
 */

public final class GreenDao {
    private With mWith;
    private Database mDatabase;
    private SQLiteDatabase mSqLiteDatabase;


    public GreenDao(int oldVersion, int newVersion, Database db) {
        this.mWith = new With(oldVersion, newVersion);
        this.mDatabase = db;
    }

    public GreenDao(int oldVersion, int newVersion, SQLiteDatabase db) {
        this.mWith = new With(oldVersion, newVersion);
        this.mSqLiteDatabase = db;
    }

    public GreenDao.With setUpgradeVersion(int upgradeVersion) {
        mWith.setUpgradeVersion(upgradeVersion);
        return mWith;
    }

    public final class With extends AbsWith<TableGreenDao> {
        private List<TableGreenDao> mUpgradeList = new ArrayList<>();
        private ControllerGreenDao mUpgradeController;

        private With(int mOldVersion, int mNewVersion) {
            super(mOldVersion, mNewVersion);
        }

        @Override
        protected void setUpgradeVersion(int upgradeVersion) {
            mUpgradeVersion = upgradeVersion;
        }

        /**
         * 设置需要升级的 AbstractDao 类
         *
         * @param abstractDao
         * @return
         */
        public ControllerGreenDao setUpgradeTable(Class<? extends AbstractDao<?, ?>>
                                                          abstractDao) {
            mUpgradeController = new ControllerGreenDao(this, abstractDao);
            return mUpgradeController;
        }

        @Override
        protected void addUpgrade(TableGreenDao upgrade) {
            mUpgradeList.add(upgrade);
        }

        @Override
        protected void clearUpgradeList() {
            mUpgradeList.clear();
        }

        @Override
        protected void upgrade() {
            if (mDatabase != null)
                new MigrationGreenDao().migrate(mDatabase, mOldVersion, mUpgradeList);
            else
                new MigrationGreenDao().migrate(mSqLiteDatabase, mOldVersion,
                        mUpgradeList);
        }
    }
}
