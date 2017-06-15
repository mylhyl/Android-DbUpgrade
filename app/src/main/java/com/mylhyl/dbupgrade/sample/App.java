package com.mylhyl.dbupgrade.sample;

import android.app.Application;

import org.xutils.x;

/**
 * Created by hupei on 2017/6/13.
 */

public class App extends Application {
    private static App instance = null;

    public static App getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //Application全局只有一个，它本身就已经是单例了，无需再用单例模式去为它做多重实例保护了
        instance = this;
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
