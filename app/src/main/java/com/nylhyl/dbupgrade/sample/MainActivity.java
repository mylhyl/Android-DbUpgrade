package com.nylhyl.dbupgrade.sample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.nylhyl.dbupgrade.ColumnType;
import com.nylhyl.dbupgrade.DbUpgrade;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xutilsInit();
    }

    private void xutilsInit() {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName("studentA");

        SchoolEntity schoolEntity = new SchoolEntity();
        schoolEntity.setName("schoolA");

        DbManager xdb = getXutilsDb();
        try {
            xdb.replace(studentEntity);
            xdb.replace(schoolEntity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private DbManager getXutilsDb() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();
        daoConfig.setDbName("dbupgrade.db");
        daoConfig.setDbDir(new File(Environment.getExternalStorageDirectory() + "/dbupgrade/" +
                getPackageName() + "/database"));
//        daoConfig.setDbVersion(3);
        daoConfig.setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                if (newVersion <= oldVersion) return;

                int upgradeVersion = oldVersion;

                DbUpgrade dbUpgrade = new DbUpgrade(db.getDatabase(), oldVersion);
                if (upgradeVersion == 1) {
                    dbUpgrade.setTableName("School")
                            .addColumn("studentId", ColumnType.INTEGER).build()

                            .setTableName("Student")
                            .addColumn("sex", ColumnType.TEXT).build()
                            .upgrade();
                    upgradeVersion = 2;
                }
                if (upgradeVersion == 2) {
                    dbUpgrade.setTableName("School")
                            .addColumn("address", ColumnType.TEXT).build()

                            .setTableName("Student")
                            .addColumn("age", ColumnType.INTEGER).build()
                            .upgrade();
                }
            }
        });
        return x.getDb(daoConfig);
    }
}
