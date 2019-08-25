package com.hash.greendao;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.hash.greendao.db.DaoMaster;
import com.hash.greendao.db.DaoSession;

/**
 * Created by HashWaney on 2019/8/23.
 */

public class GreenDaoApp extends Application {

    private DaoSession daoSession;
    private static GreenDaoApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance =this;
        initGreenDao();
    }

    public static GreenDaoApp getInstance() {
       return sInstance;

    }


    /**
     * 初始化GreenDao
     */
    private void initGreenDao() {
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "person.db");
        SQLiteDatabase db = openHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    /**
     * 获取DaoSession
     * 在这个DaoSession中维护着多张表
     */
    public DaoSession getDaoSession() {
        return daoSession;
    }
}
