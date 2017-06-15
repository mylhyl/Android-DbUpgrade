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
    private int mOldVersion;

    public DbUpgrade(int oldVersion) {
        this.mOldVersion = oldVersion;

    }

    /**
     * 原生
     *
     * @param db
     * @return
     */
    public Native with(SQLiteDatabase db) {
        mNative = new Native(db);
        return mNative;
    }

    /**
     * xutils3 框架
     *
     * @param db
     * @return
     */
    public Xutils withXutils(DbManager db) {
        mXutils = new Xutils(db);
        return mXutils;
    }

    /**
     * greenDao 框架
     *
     * @param db
     * @return
     */
    public GreenDao withGreenDao(Database db) {
        mGreenDao = new GreenDao(db);
        return mGreenDao;
    }

    /**
     * greenDao 框架
     *
     * @param db
     * @return
     */
    public Native withGreenDao(SQLiteDatabase db) {
        mNative = new Native(db);
        return mNative;
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

        public void upgrade() {
            if (mOldVersion == mUpgradeController.getUpgradeVersion()) {
                new UpgradeMigration().migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
                mOldVersion++;
            }
            mUpgradeList.clear();
        }

        SQLiteDatabase getSQLiteDatabase() {
            return mSQLiteDatabase;
        }

        List<UpgradeTable> getUpgradeList() {
            return mUpgradeList;
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

        /**
         * 设置需要升级的实体类
         *
         * @param entityType
         * @param upgradeVersion entityType 实体类 从 upgradeVersion 版本升级
         * @return
         */
        public UpgradeControllerXutils setEntityType(Class<?> entityType, int upgradeVersion) {
            mUpgradeController = new UpgradeControllerXutils(this);
            mUpgradeController.setEntityType(entityType, upgradeVersion);
            return mUpgradeController;
        }

        public void upgrade() {
            if (mOldVersion == mUpgradeController.getUpgradeVersion()) {
                new UpgradeMigrationXutils().migrate(mDbManager, mOldVersion, mUpgradeList);
                mOldVersion++;
            }
            mUpgradeList.clear();
        }

        List<UpgradeTableXutils> getUpgradeList() {
            return mUpgradeList;
        }
    }

    public final class GreenDao {
        private Database mDatabase;
        private SQLiteDatabase mSqLiteDatabase;
        private List<UpgradeTableGreenDao> mUpgradeList = new ArrayList<>();
        private UpgradeControllerGreenDao mUpgradeController;

        private GreenDao() {
        }

        GreenDao(Database db) {
            this.mDatabase = db;
        }

        GreenDao(SQLiteDatabase sqLiteDatabase) {
            this.mSqLiteDatabase = sqLiteDatabase;
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
            mUpgradeController.setAbstractDao(abstractDao, upgradeVersion);
            return mUpgradeController;
        }

        public void upgrade() {
            if (mOldVersion == mUpgradeController.getUpgradeVersion()) {
                if (mDatabase != null)
                    new UpgradeMigrationGreenDao().migrate(mDatabase, mOldVersion, mUpgradeList);
                else
                    new UpgradeMigrationGreenDao().migrate(mSqLiteDatabase, mOldVersion,
                            mUpgradeList);
                mOldVersion++;
            }
            mUpgradeList.clear();
        }

        List<UpgradeTableGreenDao> getUpgradeList() {
            return mUpgradeList;
        }
    }
}
