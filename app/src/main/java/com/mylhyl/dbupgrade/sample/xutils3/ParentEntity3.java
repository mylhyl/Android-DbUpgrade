package com.mylhyl.dbupgrade.sample.xutils3;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by hupei on 2017/6/13.
 */
@Table(name = "parent")
public class ParentEntity3 {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "name")
    private String name = "nameA";

    @Column(name = "sex")
    private String sex = "sexA";


    @Column(name = "age")
    private int age = 18;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
