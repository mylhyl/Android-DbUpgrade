package com.nylhyl.dbupgrade.sample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.nylhyl.dbupgrade.ColumnType;
import com.nylhyl.dbupgrade.DbUpgrade;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    int dbVersion = 1;
    String dbName = "dbupgrade.db";
    File dbDir = null;
    boolean update1to2;
    boolean update2to3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        dbDir = new File(Environment.getExternalStorageDirectory() + "/dbupgrade/" +
                getPackageName() + "/database");
    }

    public void onCreateDb(View view) {
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
            onFindColumn(null);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void onDel(View view) {
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(dbDir, dbName);
        if (file != null && file.exists()) file.delete();
        textView.setText("删除成功");
    }

    public void onFindColumn(View view) {
        String[] schools = getColumns(getXutilsDb().getDatabase(), "School");
        String columnSchool = TextUtils.join(",", schools);
        textView.setText("School");
        textView.append("\n\t");
        textView.append(columnSchool);
        textView.append("\n");
        textView.append("Student");
        textView.append("\n\t");
        String[] students = getColumns(getXutilsDb().getDatabase(), "Student");
        String columnStudent = TextUtils.join(",", students);
        textView.append(columnStudent);
    }

    public void on1Update2(View view) {
        dbVersion = 2;
        update1to2 = true;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn(null);
    }

    public void on2Update3(View view) {
        dbVersion = 3;
        update2to3 = true;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn(null);
    }

    public void on1Update3(View view) {
        dbVersion = 3;
        update1to2 = true;
        update2to3 = true;
        try {
            getXutilsDb().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onFindColumn(null);
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

                DbUpgrade dbUpgrade = new DbUpgrade(db.getDatabase(), oldVersion);
                if (update1to2) {
                    dbUpgrade.setTableName("School", 1)
                            .addColumn("studentId", ColumnType.INTEGER).build()

                            .setTableName("Student", 1)
                            .addColumn("sex", ColumnType.TEXT).build()
                            //每个版本都必须 upgrade()一次
                            .upgrade();
                }
                if (update2to3) {
                    dbUpgrade.setTableName("School", 2)
                            .addColumn("address", ColumnType.TEXT)
                            .addColumn("grade", ColumnType.TEXT)
//                        .setSqlCreateTable(SchoolEntity.sqlCreate3.toString())
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


    String[] getColumns(SQLiteDatabase db, String tableName) {
        String[] columns = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 0", null);
            if (null != cursor && cursor.getColumnCount() > 0) {
                columns = cursor.getColumnNames();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            if (null == columns)
                columns = new String[]{};
        }
        return columns;
    }
}
