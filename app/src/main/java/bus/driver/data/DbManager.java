package bus.driver.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import javax.inject.Inject;
import javax.inject.Singleton;

import bus.driver.data.entity.User;
import bus.driver.data.gen.DaoMaster;
import bus.driver.data.gen.DaoSession;


/**
 * Created by Liheyu on 2017/8/15.
 * Email:liheyu999@163.com
 */


@Singleton
public class DbManager {

    private static final String DB_NAME = "passenger.db";
    private DaoSession mDaoSession;

    @Inject
    public DbManager(Context context) {
        init(context);
    }


    private void init(Context context) {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
        SQLiteDatabase database = devOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        mDaoSession = daoMaster.newSession();
    }

    public User getUser() {
        return mDaoSession.getUserDao().queryBuilder().unique();
    }

    /**
     * 只保存一个用户的信息
     * @param user 用户信息
     */
    public void saveUser(User user) {
        mDaoSession.getUserDao().deleteAll();
        mDaoSession.clear();
        mDaoSession.getUserDao().insertOrReplace(user);
    }

    /**
     * @param user 须带有ID
     */
    public void updateUser(User user) {
        mDaoSession.getUserDao().update(user);
    }

    /**
     * 清楚账号信息
     */
    public void clearUserInfo() {
        mDaoSession.getUserDao().deleteAll();
    }
}
