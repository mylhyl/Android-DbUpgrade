package com.mylhyl.dbupgrade.sample.ormlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by hupei on 2017/6/16.
 */
@DatabaseTable(tableName = "tb_user")
public class User {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name")
    private String name="hp";

    @DatabaseField(columnName = "desc")
    private String desc="骚年";

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
