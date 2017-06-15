package com.mylhyl.dbupgrade.sample.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hupei on 2017/6/15.
 */
@Entity(nameInDb = "user")
public class UserEntity {
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "name")
    private String name = "name";

    @Property(nameInDb = "age")
    private int age = 18;

    private String sex = "男";

    @Generated(hash = 2080618721)
    public UserEntity(Long id, String name, int age, String sex) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    @Generated(hash = 1433178141)
    public UserEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


    //版本2
//    private String tel = "123456789";
    //版本3
//    private String address = "中国广东珠海";

}
