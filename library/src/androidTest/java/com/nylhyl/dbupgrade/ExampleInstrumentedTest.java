package com.nylhyl.dbupgrade;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.nylhyl.dbupgrade.test", appContext.getPackageName());

        DbUpgrade dbUpgrade = new DbUpgrade(null,0);
        dbUpgrade.setTableName("1").addColumn("1a",ColumnType.TEXT).build()
        .setTableName("2").addColumn("2a",ColumnType.TEXT).build()
        .upgrade();

    }
}
