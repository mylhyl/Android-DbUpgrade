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
    private long tel;
    private String address = "中国广东珠海";
    @Generated(hash = 1448576916)
    public UserEntity(Long id, String name, int age, String sex, long tel,
            String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.tel = tel;
        this.address = address;
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
    public long getTel() {
        return this.tel;
    }
    public void setTel(long tel) {
        this.tel = tel;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}
