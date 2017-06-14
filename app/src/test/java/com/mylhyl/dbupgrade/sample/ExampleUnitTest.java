package com.mylhyl.dbupgrade.sample;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        String sql = "CREATE TABLE \"School\" ( \"id\" INTEGER PRIMARY KEY" +
//                " AUTOINCREMENT, \"name\" TEXT      ,\"sex\" TEXT  )";
//        String sql = "CREATE TABLE \"School\" ( \"id\" INTEGER PRIMARY KEY" +
//                " AUTOINCREMENT, \"name\" TEXT,\"sex\" TEXT  )";

        String sql = "CREATE TABLE IF NOT EXISTS \"School\" (\"id\" INTEGER, \"name\" TEXT," +
                "\"studentId\" INTEGER,\"address\" TEXT, \"grade\" TEXT, PRIMARY KEY(\"id\", " +
                "\"    name\"))";
        LinkedList<String> removeColumns = new LinkedList<>();
        removeColumns.add("name");
        removeColumns.add("grade");
        boolean isAddskh = sql.replaceAll(" ", "").contains("))");

        System.out.println(sql);
        System.out.println("===========================");
        String[] split = sql.split(",");
        for (String str : split) {
            System.out.println(str);
            for (String column : removeColumns) {
                int indexOf = str.indexOf(column);
                if (indexOf > 0) {
                    if (str.replaceAll(" ", "").contains("))")) {
                        sql = sql.replace(","+str, "))");
                    } else {
                        sql = sql.replace(str + ",", "");
                    }
                }
//                if (indexOf < 0) {
//                    sb.append(str).append(",");
//                }
            }
        }
        System.out.println("===========================");
        System.out.println(sql);
//        sb.deleteCharAt(sb.length() - 1);
//        if (sb.lastIndexOf(")") < 0) {
//            sb.append(")");
//        }
//        if (isAddskh) {
//            sb.append(")");
//        }
//        System.out.println(sb.toString());

//        StringBuffer sqlCreate3 = new StringBuffer()
//                .append("CREATE TABLE IF NOT EXISTS \"School\" (\"id\" INTEGER, ")
//                .append("\"name\" TEXT,\"studentId\" INTEGER,\"address\" TEXT, ")
//                .append("\"grade\" TEXT, PRIMARY KEY(\"id\", \"name\"))");
//        System.out.println(sqlCreate3.toString());
    }
}