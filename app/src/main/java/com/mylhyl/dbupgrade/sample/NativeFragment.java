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


import com.mylhyl.dbupgrade.ColumnType;
import com.mylhyl.dbupgrade.DbUpgrade;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.IOException;


public class NativeFragment extends Fragment {
    private MainActivity mainActivity;
    private TextView textView;
    private int dbVersion = 1;
    private String dbName = "dbupgrade.db";
    private File dbDir = null;
    private boolean update1to2;
    private boolean update2to3;

    public NativeFragment() {
    }

    public static NativeFragment newInstance() {
        NativeFragment fragment = new NativeFragment();
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
        dbDir = new File(Environment.getExternalStorageDirectory() + "/dbupgrade/" +
                getActivity().getPackageName() + "/database");
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
        update1to2 = false;
        update2to3 = false;

        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName("studentA");

        SchoolEntity schoolEntity = new SchoolEntity();
        schoolEntity.setName("schoolA");

        DbManager xdb = getXutilsDb();
        try {
            xdb.replace(studentEntity);
            xdb.replace(schoolEntity);
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
        String[] schools = mainActivity.getColumns(getXutilsDb().getDatabase(), "School");
        String columnSchool = TextUtils.join(",", schools);
        textView.setText("School");
        textView.append("\n\t");
        textView.append(columnSchool);
        textView.append("\n");
        textView.append("Student");
        textView.append("\n\t");
        String[] students = mainActivity.getColumns(getXutilsDb().getDatabase(), "Student");
        String columnStudent = TextUtils.join(",", students);
        textView.append(columnStudent);
    }

    private void on1Update2() {
        dbVersion = 2;
        update1to2 = true;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn();
    }

    private void on2Update3() {
        dbVersion = 3;
        update2to3 = true;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn();
    }

    private void on1Update3() {
        dbVersion = 3;
        update1to2 = true;
        update2to3 = true;
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

                DbUpgrade dbUpgrade = new DbUpgrade(oldVersion);
                DbUpgrade.Native with = dbUpgrade.with(db.getDatabase());
                if (update1to2) {
                    with.setTableName("School", 1)
                            .addColumn("studentId", ColumnType.INTEGER)
                            .setSqlCreateTable(SchoolEntity.sqlCreate3.toString())
                            .build()

                            .setTableName("Student", 1)
                            .addColumn("sex", ColumnType.TEXT).build()
                            //每个版本都必须 upgrade()一次
                            .upgrade();
                }
                if (update2to3) {
                    with.setTableName("School", 2)
                            .addColumn("address", ColumnType.TEXT)
                            .addColumn("grade", ColumnType.TEXT)
                            .removeColumn("studentId")
                            .build()

                            .setTableName("Student", 2)
                            .addColumn("age", ColumnType.INTEGER).build()
                            //每个版本都必须 upgrade()一次
                            .upgrade();
                }
            }
        });
        return x.getDb(daoConfig);
    }
}
