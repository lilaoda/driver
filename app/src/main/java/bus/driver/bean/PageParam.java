package bus.driver.bean;

/**
 * Created by Lilaoda on 2017/11/7.
 * Email:749948218@qq.com
 * 获取订单列表，需要的分页信息
 */

public class PageParam {

    private int pageNo;
    private int pageSize;

    public PageParam() {
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
