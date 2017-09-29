package bus.driver.data.remote;

/**
 * Created by Liheyu on 2017/3/6.
 * Email:liheyu999@163.com
 */

public class HttpResult<T> {
    private T data;
    private boolean result;
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
