package com.nylhyl.dbupgrade.sample;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by hupei on 2017/6/13.
 */
@Table(name = "School")
public class SchoolEntity {
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
