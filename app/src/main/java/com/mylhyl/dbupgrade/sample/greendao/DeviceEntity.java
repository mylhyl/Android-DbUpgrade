package com.mylhyl.dbupgrade.sample.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hupei on 2017/6/15.
 */
@Entity(nameInDb = "device")
public class DeviceEntity {
    @Id
    private Long stationNum;

    private String deviceNum = "deviceNum";

    //版本2删除
    private String bay = "bay";

    @Generated(hash = 2064758942)
    public DeviceEntity(Long stationNum, String deviceNum, String bay) {
        this.stationNum = stationNum;
        this.deviceNum = deviceNum;
        this.bay = bay;
    }

    @Generated(hash = 1449836520)
    public DeviceEntity() {
    }

    public Long getStationNum() {
        return this.stationNum;
    }

    public void setStationNum(Long stationNum) {
        this.stationNum = stationNum;
    }

    public String getDeviceNum() {
        return this.deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getBay() {
        return this.bay;
    }

    public void setBay(String bay) {
        this.bay = bay;
    }

    //版本3添加
//    private String unit = "unit";

}
