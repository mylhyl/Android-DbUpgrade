package com.mylhyl.dbupgrade.sample.xutils3;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by hupei on 2017/6/13.
 */
@Table(name = "child")
public class ChildEntity {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "name")
    private String name = "nameB";

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
