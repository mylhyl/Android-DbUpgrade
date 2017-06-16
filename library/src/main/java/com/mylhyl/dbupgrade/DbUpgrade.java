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
    public GreenDao withGreenDao(SQLiteDatabase db) {
        mGreenDao = new GreenDao(db);
        return mGreenDao;
    }

    Xutils getXutils() {
        return mXutils;
    }

    public final class Native {
        private With mWith;
        private SQLiteDatabase mSQLiteDatabase;

        private Native(SQLiteDatabase db) {
            this.mWith = new With(mOldVersion, mNewVersion);
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

            /**
             * 设置需要升级的表名
             *
             * @param tableName
             * @return
             */
            public Controller setUpgradeTable(String tableName) {
                mUpgradeController = new Controller(this, tableName);
                return mUpgradeController;
            }

            @Override
            void upgrade() {
                new Migration().migrate(mSQLiteDatabase, mOldVersion, mUpgradeList);
            }

            SQLiteDatabase getSQLiteDatabase() {
                return mSQLiteDatabase;
            }

            @Override
            void addUpgrade(Table upgradeTable) {
                mUpgradeList.add(upgradeTable);
            }

            @Override
            void clearUpgradeList() {
                mUpgradeList.clear();
            }
        }
    }

    public final class Xutils {
        private With mWith;
        private DbManager mDbManager;

        private Xutils(DbManager db) {
            this.mWith = new With(mOldVersion, mNewVersion);
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
            void addUpgrade(TableXutils upgradeTable) {
                mUpgradeList.add(upgradeTable);
            }

            @Override
            void clearUpgradeList() {
                mUpgradeList.clear();
            }

            @Override
            void upgrade() {
                new MigrationXutils().migrate(mDbManager, mOldVersion, mUpgradeList);
            }
        }
    }

    public final class GreenDao {
        private With mWith;
        private Database mDatabase;
        private SQLiteDatabase mSqLiteDatabase;


        private GreenDao(Database db) {
            this.mWith = new With(mOldVersion, mNewVersion);
            this.mDatabase = db;
        }

        private GreenDao(SQLiteDatabase db) {
            this.mWith = new With(mOldVersion, mNewVersion);
            this.mSqLiteDatabase = db;
        }

        public With setUpgradeVersion(int upgradeVersion) {
            mWith.setUpgradeVersion(upgradeVersion);
            return mWith;
        }

        public final class With extends AbsWith<TableGreenDao> {
            private List<TableGreenDao> mUpgradeList = new ArrayList<>();
            private ControllerGreenDao mUpgradeController;

            private With(int mOldVersion, int mNewVersion) {
                super(mOldVersion, mNewVersion);
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
            void addUpgrade(TableGreenDao upgrade) {
                mUpgradeList.add(upgrade);
            }

            @Override
            void clearUpgradeList() {
                mUpgradeList.clear();
            }

            @Override
            void upgrade() {
                if (mDatabase != null)
                    new MigrationGreenDao().migrate(mDatabase, mOldVersion, mUpgradeList);
                else
                    new MigrationGreenDao().migrate(mSqLiteDatabase, mOldVersion,
                            mUpgradeList);
            }
        }
    }

    abstract class AbsWith<T extends BaseTable> {
        private int mOldVersion, mNewVersion;
        int mUpgradeVersion;

        public AbsWith(int mOldVersion, int mNewVersion) {
            this.mOldVersion = mOldVersion;
            this.mNewVersion = mNewVersion;
        }

        final void setUpgradeVersion(int mUpgradeVersion) {
            this.mUpgradeVersion = mUpgradeVersion;
        }

        final void addOldVersion() {
            mOldVersion++;
        }

        final boolean isUpgrade() {
            return mOldVersion == mUpgradeVersion && mUpgradeVersion < mNewVersion;
        }

        abstract void addUpgrade(T upgradeTable);

        abstract void clearUpgradeList();

        abstract void upgrade();
    }
}
