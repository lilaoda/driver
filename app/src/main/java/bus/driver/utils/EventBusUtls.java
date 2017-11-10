package bus.driver.utils;

import org.greenrobot.eventbus.EventBus;

import bus.driver.bean.OrderInfo;
import bus.driver.data.local.entity.User;
import bus.driver.module.setting.MyInfoActivity;

/**
 * Created by Lilaoda on 2017/11/10.
 * Email:749948218@qq.com
 */

public class EventBusUtls {

    /**
     * 通知订单信息有改变 在订单列表接受通知{@link bus.driver.module.main.OrderFragment}
     * @param orderInfo 改变的订单信息
     */
    public static void notifyOrderChanged(OrderInfo orderInfo){
        EventBus.getDefault().post(orderInfo);
    }

    /**
     * 通知个人信息有改变 在个人信息页面，侧边栏接受通知{@link MyInfoActivity}  {@link bus.driver.module.main.LeftNavFragment}
     * @param user 个人信息有改变，头像 姓名等
     */
    public static void notifyUserInfoChanged(User user){
        EventBus.getDefault().post(user);
    }
}
