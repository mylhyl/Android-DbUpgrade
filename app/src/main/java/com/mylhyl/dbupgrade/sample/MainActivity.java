package com.mylhyl.dbupgrade.sample;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

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
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Fragment fragment = null;
        int typeId = (int) ContentUris.parseId(uri);
        switch (typeId) {
            case 0:
                fragment = NativeFragment.newInstance();
                break;
            case 1:
//                fragment = GridViewFragment.newInstance();
                break;
            case 2:
//                fragment = RecyclerViewFragment.newInstance();
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
