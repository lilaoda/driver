package bus.driver.base;


public class Constants {

    public static final int STATUSBAR_ALPHA = 30;

    //    缓存目录名称及大小
    public static final String CACHE_FILE_NAME = "file_cache";
    public static final long cacheFileSize = 1024 * 1024 * 100;

    public static final String ORDER_INFO = "order_info";

    //Driver Config
    //司机状态，工作，作息
    public static final int DRIVER_STATSU_WORK = 9;
    public static final int DRIVER_STATSU_REST = 10;
    public static int DRIVER_STATSU = DRIVER_STATSU_REST;
    //订单状态，有正在进行中的订单，没有正在进行中的订单
    public static final int ORDER_STATSU_ONDOING = 11;
    public static final int ORDER_STATSU_NO = 12;
    public static int ORDER_STATSU = ORDER_STATSU_NO;

}

