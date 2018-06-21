package com.mylhyl.dbupgrade.sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mylhyl.dbupgrade.DbUpgrade;
import com.mylhyl.dbupgrade.sample.xutils3.ChildEntity;
import com.mylhyl.dbupgrade.sample.xutils3.ChildEntity2;
import com.mylhyl.dbupgrade.sample.xutils3.ChildEntity3;
import com.mylhyl.dbupgrade.sample.xutils3.ParentEntity;
import com.mylhyl.dbupgrade.sample.xutils3.ParentEntity2;
import com.mylhyl.dbupgrade.sample.xutils3.ParentEntity3;
import com.mylhyl.dbupgrade.xuitls3.Xutils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.IOException;


public class XutilsFragment extends Fragment {
    private MainActivity mainActivity;
    private TextView textView;
    private int dbVersion = 1;
    private String dbName = "dbupgradexutils.db";
    private File dbDir = null;

    public XutilsFragment() {
    }

    public static XutilsFragment newInstance() {
        XutilsFragment fragment = new XutilsFragment();
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
        dbDir = new File(getActivity().getExternalCacheDir() + "/database");
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
        dbVersion = 1;

        ParentEntity parentEntity = new ParentEntity();

        ChildEntity childEntity = new ChildEntity();

        DbManager xdb = getXutilsDb();
        try {
            xdb.replace(parentEntity);
            xdb.replace(childEntity);
            onFindColumn();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void onDel() {
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(dbDir, dbName);
        if (file != null && file.exists()) file.delete();
        textView.setText("删除成功");
    }

    private void onFindColumn() {
        String[] parents = mainActivity.getColumns(getXutilsDb().getDatabase(), "parent");
        String columnParent = TextUtils.join(",", parents);
        textView.setText("parent");
        textView.append("\n\t");
        textView.append(columnParent);
        textView.append("\n");
        textView.append("child");
        textView.append("\n\t");
        String[] childs = mainActivity.getColumns(getXutilsDb().getDatabase(), "child");
        String columnChild = TextUtils.join(",", childs);
        textView.append(columnChild);
    }

    private void on1Update2() {
        dbVersion = 2;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn();
    }

    private void on2Update3() {
        dbVersion = 3;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn();
    }

    private void on1Update3() {
        dbVersion = 3;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn();
    }

    private DbManager getXutilsDb() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        daoConfig.setDbName(dbName);
        daoConfig.setDbDir(dbDir);
        daoConfig.setDbVersion(dbVersion);
        daoConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                if (newVersion <= oldVersion) return;
                DbUpgrade dbUpgrade = new DbUpgrade(oldVersion, newVersion);
                Xutils with = dbUpgrade.withXutils(db);
                with.setUpgradeVersion(1)
                        .setUpgradeTable(ParentEntity2.class)
                        .setUpgradeTable(ChildEntity2.class)
                        //每个版本都必须 upgrade()一次
                        .upgrade();

                with.setUpgradeVersion(2)
                        .setUpgradeTable(ParentEntity3.class)
                        .setUpgradeTable(ChildEntity3.class)
                        //每个版本都必须 upgrade()一次
                        .upgrade();
            }
        });
        return x.getDb(daoConfig);
    }
}
