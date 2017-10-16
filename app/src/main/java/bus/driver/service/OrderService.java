package bus.driver.service;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bus.driver.R;
import bus.driver.base.BaseApplication;
import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import bus.driver.module.order.CaptureOrderActivity;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import lhy.lhylibrary.base.LhyActivity;
import lhy.lhylibrary.base.LhyApplication;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 * <p>
 * 订单服务接口 用于循环获取订单相关信息,需要发送开启事件才能开始获取订单
 */

public class OrderService extends Service {

    public static final String TAG = "orderService";

    /**
     * 获取推送订单的间隔
     */
    public static final int INTERVAL_PULL_ORDER = 10;

    private AlertDialog mOrderDialog;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNotificationManager;
    private HttpManager mHttpManager;
    private Disposable mOrderDisposable;
    private Disposable mCancelOrderDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mHttpManager = HttpManager.instance();
        initNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderEvent event) {
        Log.i(TAG, "onMessageEvent: OrderEvent" + event.getLocationValue());
        switch (event) {
            case ORDER_PULL_ENABLE:
                startPullOrder();
                break;
            case ORDER_PULL_UNABLE:
                stopPullOrder();
                break;
            case ORDER_GET_CANCEL_ENABLE:
                startPullCancelOrder();
                break;
            case ORDER_GET_CANCEL_UNABLE:
                stopPullCancelOrder();
                break;
        }
    }

    private void startPullCancelOrder() {
        Log.i(TAG, "startPullCancelOrdr: " + "开始循环获取乘客是否取消订单的通知");
        if (mCancelOrderDisposable != null && !mCancelOrderDisposable.isDisposed()) return;
        mCancelOrderDisposable = Flowable.interval(INTERVAL_PULL_ORDER, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        pullCancelOrder();
                    }
                });
    }

    private void stopPullCancelOrder() {
        Log.i(TAG, "stopPullCancelOrder: " + "停止循环获取乘客是否取消订单的通知");
        if (mCancelOrderDisposable != null && !mCancelOrderDisposable.isDisposed()) {
            mCancelOrderDisposable.dispose();
        }
    }

    private void pullCancelOrder() {
        wrapHttp(mHttpManager.getOrderApi().pushOrderCancel()).subscribe(new ResultObserver<List<OrderInfo>>() {
            @Override
            public void onSuccess(List<OrderInfo> value) {
                ToastUtils.showString("订单已被取消");
            }
        });
    }

    private void stopPullOrder() {
        Log.i(TAG, "stopPullOrder: " + "取消循环拉取订单");
        if (mOrderDisposable != null && !mOrderDisposable.isDisposed()) {
            mOrderDisposable.dispose();
        }
    }

    private void startPullOrder() {
        Log.i(TAG, "stopPullOrder: " + "开始循环拉取订单");
        if (mOrderDisposable != null && !mOrderDisposable.isDisposed()) return;
        mOrderDisposable = Flowable.interval(INTERVAL_PULL_ORDER, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if(GlobeConstants.DRIVER_STATSU==GlobeConstants.DRIVER_STATSU_WORK){
                            pullOrder();
                        }
                    }
                });
    }

    private void pullOrder() {
        wrapHttp(mHttpManager.getDriverService().pullOrder()).subscribe(new ResultObserver<List<OrderInfo>>() {
            @Override
            public void onSuccess(List<OrderInfo> value) {
                if (value.size() > 0) {
                    LhyActivity currentActivity = BaseApplication.getInstance().getCurrentActivity();
                    if (GlobeConstants.ORDER_STATSU == GlobeConstants.ORDER_STATSU_ONDOING || GlobeConstants.DRIVER_STATSU == GlobeConstants.DRIVER_STATSU_REST) {
                        return;
                    }
                    if (isBackground() || currentActivity == null || !currentActivity.isResume()) {
                        notifyOrder(value.get(0));
                    } else {
                        if (GlobeConstants.ORDER_STATSU == GlobeConstants.ORDER_STATSU_NO) {
                            if (mOrderDialog != null && mOrderDialog.isShowing() || currentActivity instanceof CaptureOrderActivity)
                                return;
                            showOrderDialog(currentActivity, value.get(0));
                        }
                    }
                }
            }
        });
    }

    private void showOrderDialog(final Activity currentActivity, final OrderInfo orderInfo) {
        if (mOrderDialog != null) {
            return;
        }
        mOrderDialog = new AlertDialog.Builder(currentActivity)
                .setTitle("您有新的订单")
                .setMessage("起始位置：" + orderInfo.getOriginAddress() + "\n" + "目的地：" + orderInfo.getDestAddress())
                .setPositiveButton("接单", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, CaptureOrderActivity.class);
                        intent.putExtra(GlobeConstants.ORDER_INFO, orderInfo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("勇敢的拒绝", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mOrderDialog = null;
                    }
                })
                .setCancelable(false)
                .show();
        // mOrderDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
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
                .setOngoing(true);
        mNotificationManager = (NotificationManager) LhyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void notifyOrder(OrderInfo orderInfo) {
        Intent intent = new Intent(this, CaptureOrderActivity.class);
        intent.putExtra(GlobeConstants.ORDER_INFO, orderInfo);
        Notification notification = mNotifyBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mOrderDialog != null) {
            mOrderDialog.dismiss();
            mOrderDialog = null;
        }
        if (mOrderDisposable != null) {
            if (!mOrderDisposable.isDisposed()) {
                mOrderDisposable.dispose();
            }
            mOrderDisposable = null;
        }
    }

    /**
     * 判断应用是否处于后台
     */
    public boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Logger.i(TAG, "处于前台：" + appProcess.processName);
                    return false;
                } else {
                    Logger.i(TAG, "处于后台：" + appProcess.processName);
                    return true;
                }
            }
        }
        return true;
    }
}
