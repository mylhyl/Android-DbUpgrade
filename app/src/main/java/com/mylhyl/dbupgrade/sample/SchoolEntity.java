package com.mylhyl.dbupgrade.sample;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by hupei on 2017/6/13.
 */
@Table(name = "School")
public class SchoolEntity {

   public static StringBuffer sqlCreate3 = new StringBuffer()
            .append("CREATE TABLE IF NOT EXISTS \"School\" (\"id\" INTEGER, ")
            .append("\"name\" TEXT,\"studentId\" INTEGER, PRIMARY KEY(\"id\", \"name\"))");

    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
