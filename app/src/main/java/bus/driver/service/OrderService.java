package bus.driver.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import bus.driver.R;
import bus.driver.module.main.MainActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import lhy.lhylibrary.base.LhyApplication;
import lhy.lhylibrary.utils.ToastUtils;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 * <p>
 * 订单服务接口 用于循环获取订单相关信息
 */

public class OrderService extends Service {

    private AlertDialog mOrderDialog;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO 模拟
        Observable.interval(60, TimeUnit.SECONDS).take(5).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        notifyOrder();
                    }
                });
        return super.onStartCommand(intent, flags, startId);
    }

    private void showOrderDialog() {
        if (mOrderDialog == null) {
            mOrderDialog = new AlertDialog.Builder(this)
                    .setTitle("您有新的订单")
                    .setMessage("天信楼 到 永泰地铁站")
                    .setPositiveButton("接单", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtils.showString("跳到订单页面");
                        }
                    }).setPositiveButton("勇敢的拒绝", null)
                    .setCancelable(false)
                    .create();
            mOrderDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        mOrderDialog.show();
    }


    //初始化通知
    private void initNotification() {
        mNotifyBuilder = new NotificationCompat.Builder(this);
        mNotifyBuilder.setContentTitle("订单通知")
                .setContentText("点击查看详情")
                .setSmallIcon(R.mipmap.loading)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.mis_asv))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        mNotificationManager = (NotificationManager) LhyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void notifyOrder() {
        //TODO 保存订单信息 不能通过INTENT，如果此时mainactiviy在显示 则传不过去
        Notification notification = mNotifyBuilder.build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(1, notification);
    }
}
