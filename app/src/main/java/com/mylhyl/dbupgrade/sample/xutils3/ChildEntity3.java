package com.mylhyl.dbupgrade.sample.xutils3;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by hupei on 2017/6/13.
 */
@Table(name = "child")
public class ChildEntity3 {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "name")
    private String name = "nameB";

    @Column(name = "text")
    private String text = "textB";

    @Column(name = "isAdmin")
    private boolean isAdmin = true;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
