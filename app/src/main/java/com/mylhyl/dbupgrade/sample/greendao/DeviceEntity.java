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

    private String unit = "unit";

    @Generated(hash = 1659403700)
    public DeviceEntity(Long stationNum, String deviceNum, String unit) {
        this.stationNum = stationNum;
        this.deviceNum = deviceNum;
        this.unit = unit;
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

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
