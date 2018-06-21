package com.mylhyl.dbupgrade.sample;

import android.Manifest;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TypesFragment
        .OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, TypesFragment.newInstance())
                    .commitAllowingStateLoss();
        }
        AcpOptions acpOptions = new AcpOptions.Builder()
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE)
                .build();
        Acp.getInstance(this).request(acpOptions, new AcpListener() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(List<String> permissions) {
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Fragment fragment = null;
        int typeId = (int) ContentUris.parseId(uri);
        switch (typeId) {
            case 0:
                fragment = OriginalFragment.newInstance();
                break;
            case 1:
                fragment = XutilsFragment.newInstance();
                break;
            case 2:
                fragment = GreenDaoFragment.newInstance();
                break;
            case 3:
                fragment = OrmLiteFragment.newInstance();
                break;
        }

        if (fragment != null)
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
    }

    public String[] getColumns(SQLiteDatabase db, String tableName) {
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
