package com.mylhyl.dbupgrade.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.DbUpgrade;
import com.mylhyl.dbupgrade.greendao.GreenDao;
import com.mylhyl.dbupgrade.sample.greendao.DaoMaster;
import com.mylhyl.dbupgrade.sample.greendao.DaoSession;
import com.mylhyl.dbupgrade.sample.greendao.DeviceEntity;
import com.mylhyl.dbupgrade.sample.greendao.DeviceEntityDao;
import com.mylhyl.dbupgrade.sample.greendao.GreenDaoConfig;
import com.mylhyl.dbupgrade.sample.greendao.UserEntity;
import com.mylhyl.dbupgrade.sample.greendao.UserEntityDao;

import org.greenrobot.greendao.database.Database;

import java.io.File;


public class GreenDaoFragment extends Fragment {
    private MainActivity mainActivity;
    private TextView textView;
    private String dbName = "dbupgradeGreenDao.db";
    private File dbDir = null;

    public GreenDaoFragment() {
    }

    public static GreenDaoFragment newInstance() {
        GreenDaoFragment fragment = new GreenDaoFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbDir = new File(getActivity().getExternalCacheDir().getPath() + "/database");
        textView = (TextView) getView().findViewById(R.id.text);

        getView().findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDb();
            }
        });
        getView().findViewById(R.id.button6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDel();
            }
        });
        getView().findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFindColumn();
            }
        });
        getView().findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on1Update2();
            }
        });
        getView().findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on2Update3();
            }
        });
        getView().findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on1Update3();
            }
        });
    }

    private void onCreateDb() {

        DeviceEntity deviceEntity = new DeviceEntity();

        UserEntity userEntity = new UserEntity();
        DaoMaster greenDao = getGreenDao();
        if (greenDao.SCHEMA_VERSION == 1) {
            DaoSession daoSession = greenDao.newSession();
            daoSession.insert(deviceEntity);
            daoSession.insert(userEntity);
            onFindColumn();
        } else {
            Toast.makeText(mainActivity, "app.gradle schemaVersion 必须 1", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void onDel() {
        File file = new File(dbDir, dbName);
        if (file != null && file.exists()) file.delete();
        textView.setText("删除成功");
    }

    private void onFindColumn() {
        SQLiteDatabase database = (SQLiteDatabase) getGreenDao().getDatabase().getRawDatabase();
        String[] parents = mainActivity.getColumns(database, "device");
        String columnParent = TextUtils.join(",", parents);
        textView.setText("device");
        textView.append("\n\t");
        textView.append(columnParent);
        textView.append("\n");
        textView.append("user");
        textView.append("\n\t");
        String[] childs = mainActivity.getColumns(database, "user");
        String columnChild = TextUtils.join(",", childs);
        textView.append(columnChild);
    }

    private void on1Update2() {
        DaoMaster greenDao = getGreenDao();
        if (greenDao.SCHEMA_VERSION == 2) {
            greenDao.getDatabase().getRawDatabase();
            onFindColumn();
        } else {
            Toast.makeText(mainActivity, "app.gradle schemaVersion 必须 2", Toast
                    .LENGTH_SHORT).show();
        }
    }

    private void on2Update3() {
        DaoMaster greenDao = getGreenDao();
        if (greenDao.SCHEMA_VERSION == 3) {
            greenDao.getDatabase().getRawDatabase();
            onFindColumn();
        } else
            Toast.makeText(mainActivity, "app.gradle schemaVersion 必须 3", Toast
                    .LENGTH_SHORT).show();
    }

    private void on1Update3() {
        DaoMaster greenDao = getGreenDao();
        if (greenDao.SCHEMA_VERSION == 3) {
            greenDao.getDatabase().getRawDatabase();
            onFindColumn();
        } else
            Toast.makeText(mainActivity, "app.gradle schemaVersion 必须 3", Toast
                    .LENGTH_SHORT).show();
    }

    private DaoMaster getGreenDao() {

        GreenDaoConfig greenDaoConfig = new GreenDaoConfig();
        greenDaoConfig.setDbDir(dbDir);
        greenDaoConfig.setDbName(dbName);
        ProdOpenHelper prodOpenHelper = new ProdOpenHelper(greenDaoConfig);
        Database db = prodOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster;
    }

    public class ProdOpenHelper extends DaoMaster.OpenHelper {
        public ProdOpenHelper(GreenDaoConfig daoConfig) {
            super(daoConfig, daoConfig.getDbName());
        }

        public ProdOpenHelper(GreenDaoConfig daoConfig, SQLiteDatabase.CursorFactory factory) {
            super(daoConfig, daoConfig.getDbName(), factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            if (newVersion <= oldVersion) return;

            DbUpgrade dbUpgrade = new DbUpgrade(oldVersion, newVersion);
            GreenDao greenDao = dbUpgrade.withGreenDao(db);
            greenDao.setUpgradeVersion(1)
                    .setUpgradeTable(DeviceEntityDao.class)
//                    .addColumn(DeviceEntityDao.Properties.Content.columnName, ColumnType.TEXT)
//                    .addColumn(DeviceEntityDao.Properties.Info.columnName, ColumnType.INTEGER)
                    .setUpgradeTable(UserEntityDao.class)
                    //每个版本都必须 upgrade()一次
                    .upgrade();

            greenDao.setUpgradeVersion(2)
                    .setUpgradeTable(DeviceEntityDao.class)
//                    .addColumn(DeviceEntityDao.Properties.ComOrl.columnName, ColumnType.INTEGER)
                    .setUpgradeTable(UserEntityDao.class)
                    //每个版本都必须 upgrade()一次
                    .upgrade();
        }
    }
}
