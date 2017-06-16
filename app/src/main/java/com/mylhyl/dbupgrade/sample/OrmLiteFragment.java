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

import com.j256.ormlite.table.TableUtils;
import com.mylhyl.dbupgrade.sample.ormlite.DatabaseHelper;
import com.mylhyl.dbupgrade.sample.ormlite.User;
import com.mylhyl.dbupgrade.sample.ormlite.User1;
import com.mylhyl.dbupgrade.sample.ormlite.User2;

import java.io.File;
import java.sql.SQLException;


public class OrmLiteFragment extends Fragment {
    private MainActivity mainActivity;
    private TextView textView;
    private int dbVersion = 1;
    private String dbName = "dbupgradexutils.db";
    private File dbDir = null;

    public OrmLiteFragment() {
    }

    public static OrmLiteFragment newInstance() {
        OrmLiteFragment fragment = new OrmLiteFragment();
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
        DatabaseHelper.DB_VERSION = dbVersion;
        User user = new User();
        DatabaseHelper helper = DatabaseHelper.getHelper(getContext());
        try {
            helper.getUserDao().create(user);
            onFindColumn();

            if (user != null) textView.append("\n" + user.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void onDel() {
        DatabaseHelper helper = DatabaseHelper.getHelper(getContext());
        try {
            TableUtils.dropTable(helper.getUserDao(), true);
            helper.close();
            textView.setText("删除成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onFindColumn() {
        DatabaseHelper helper = DatabaseHelper.getHelper(getContext());
        String[] parents = mainActivity.getColumns(helper.getReadableDatabase(), "tb_user");
        String columnParent = TextUtils.join(",", parents);
        textView.setText("User");
        textView.append("\n\t");
        textView.append(columnParent);
    }

    private void on1Update2() {
        dbVersion = 2;
        DatabaseHelper.DB_VERSION = dbVersion;
        DatabaseHelper helper = DatabaseHelper.getHelper(getContext());
        onFindColumn();
        try {
            User1 user = helper.getUser1Dao().queryForId(1);
            if (user != null) textView.append("\n" + user.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void on2Update3() {
        dbVersion = 3;
        DatabaseHelper.DB_VERSION = dbVersion;
        DatabaseHelper helper = DatabaseHelper.getHelper(getContext());
        onFindColumn();
        try {
            User2 user = helper.getUser2Dao().queryForId(1);
            if (user != null) textView.append("\n" + user.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void on1Update3() {
        dbVersion = 3;
        DatabaseHelper.DB_VERSION = dbVersion;
        DatabaseHelper helper = DatabaseHelper.getHelper(getContext());
        onFindColumn();
        try {
            User2 user = helper.getUser2Dao().queryForId(1);
            if (user != null) textView.append("\n" + user.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
