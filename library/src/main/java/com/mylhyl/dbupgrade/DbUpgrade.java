package com.mylhyl.dbupgrade;

import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.xutils.DbManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupei on 2017/6/9.
 */

public final class DbUpgrade {
    private Native mNative;
    private Xutils mXutils;
    private GreenDao mGreenDao;
    private int mOldVersion, mNewVersion;

    public DbUpgrade(int oldVersion, int newVersion) {
        this.mOldVersion = oldVersion;
        this.mNewVersion = newVersion;
    }

    /**
     * 原生
     *
     * @param db
     * @return
     */
    public Native with(SQLiteDatabase db) {
        mNative = new Native(mOldVersion, mNewVersion, db);
        return mNative;
    }

    /**
     * xutils3 框架
     *
     * @param db
     * @return
     */
    public Xutils withXutils(DbManager db) {
        mXutils = new Xutils(mOldVersion, mNewVersion, db);
        return mXutils;
    }

    /**
     * greenDao 框架
     *
     * @param db
     * @return
     */
    public GreenDao withGreenDao(Database db) {
        mGreenDao = new GreenDao(mOldVersion, mNewVersion, db);
        return mGreenDao;
    }

    /**
     * greenDao 框架
     *
     * @param db
     * @return
     */
    public GreenDao withGreenDao(SQLiteDatabase db) {
        mGreenDao = new GreenDao(mOldVersion, mNewVersion, db);
        return mGreenDao;
    }

    Xutils getXutils() {
        return mXutils;
    }

    abstract class With<T extends BaseUpgradeTable> {
        private int mOldVersion, mNewVersion;

        public With(int mOldVersion, int mNewVersion) {
            this.mOldVersion = mOldVersion;
            this.mNewVersion = mNewVersion;
        }

        final int getOldVersion() {
            return mOldVersion;
        }

        final void addOldVersion() {
            mOldVersion++;
        }

        final int getNewVersion() {
            return mNewVersion;
        }

        abstract void addUpgrade(T upgradeTable);

        abstract void clearUpgradeList();

        abstract void upgrade();
    }

    public final class Native extends With<UpgradeTable> {
        private SQLiteDatabase mSQLiteDatabase;
        private List<UpgradeTable> mUpgradeList = new ArrayList<>();
        private UpgradeController mUpgradeController;

        private Native(int mOldVersion, int mNewVersion, SQLiteDatabase db) {
            super(mOldVersion, mNewVersion);
            this.mSQLiteDatabase = db;
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
            mUpgradeController.newTableName(tableName, upgradeVersion);
            return mUpgradeController;
        }

        @Override
        void upgrade() {
            new UpgradeMigration().migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
        }

        SQLiteDatabase getSQLiteDatabase() {
            return mSQLiteDatabase;
        }

        @Override
        void addUpgrade(UpgradeTable upgradeTable) {
            mUpgradeList.add(upgradeTable);
        }

        @Override
        void clearUpgradeList() {
            mUpgradeList.clear();
        }
    }

    public final class Xutils extends With<UpgradeTableXutils> {

        private DbManager mDbManager;
        private List<UpgradeTableXutils> mUpgradeList = new ArrayList<>();
        private UpgradeControllerXutils mUpgradeController;

        private Xutils(int mOldVersion, int mNewVersion, DbManager db) {
            super(mOldVersion, mNewVersion);
            this.mDbManager = db;
        }

        /**
         * 设置需要升级的实体类
         *
         * @param entityType
         * @param upgradeVersion entityType 实体类 从 upgradeVersion 版本升级
         * @return
         */
        public UpgradeControllerXutils setUpgradeTable(Class<?> entityType, int upgradeVersion) {
            mUpgradeController = new UpgradeControllerXutils(this);
            mUpgradeController.newUpgradeTable(entityType, upgradeVersion);
            return mUpgradeController;
        }

        @Override
        void addUpgrade(UpgradeTableXutils upgradeTable) {
            mUpgradeList.add(upgradeTable);
        }

        @Override
        void clearUpgradeList() {
            mUpgradeList.clear();
        }

        @Override
        void upgrade() {
            new UpgradeMigrationXutils().migrate(mDbManager, mOldVersion, mUpgradeList);
        }

    }

    public final class GreenDao extends With<UpgradeTableGreenDao> {
        private Database mDatabase;
        private SQLiteDatabase mSqLiteDatabase;
        private List<UpgradeTableGreenDao> mUpgradeList = new ArrayList<>();
        private UpgradeControllerGreenDao mUpgradeController;

        private GreenDao(int mOldVersion, int mNewVersion, Database db) {
            super(mOldVersion, mNewVersion);
            this.mDatabase = db;
        }

        private GreenDao(int mOldVersion, int mNewVersion, SQLiteDatabase db) {
            super(mOldVersion, mNewVersion);
            this.mSqLiteDatabase = db;
        }


        /**
         * 设置需要升级的 AbstractDao 类
         *
         * @param abstractDao
         * @param upgradeVersion abstractDao 从 upgradeVersion 版本升级
         * @return
         */
        public UpgradeControllerGreenDao setAbstractDao(Class<? extends AbstractDao<?, ?>>
                                                                abstractDao, int upgradeVersion) {
            mUpgradeController = new UpgradeControllerGreenDao(this);
            mUpgradeController.newAbstractDao(abstractDao, upgradeVersion);
            return mUpgradeController;
        }

        @Override
        void addUpgrade(UpgradeTableGreenDao upgrade) {
            mUpgradeList.add(upgrade);
        }

        @Override
        void clearUpgradeList() {
            mUpgradeList.clear();
        }

        @Override
        void upgrade() {
            if (mDatabase != null)
                new UpgradeMigrationGreenDao().migrate(mDatabase, mOldVersion, mUpgradeList);
            else
                new UpgradeMigrationGreenDao().migrate(mSqLiteDatabase, mOldVersion,
                        mUpgradeList);
        }
    }
}
