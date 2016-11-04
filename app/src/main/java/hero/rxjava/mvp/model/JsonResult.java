package hero.rxjava.mvp.model;

import com.google.gson.annotations.SerializedName;

/**
 * 服务器返回的最外层的json格式
 * Created by hero on 2016/6/17 0017.
 */
public class JsonResult<T> {
    //@SerializedName是指定Json格式中的Key名
    //可以不写，则默认采用与变量名一样的Key名,混淆的话要写
    @SerializedName("msg")
    private String msg;
    @SerializedName("returnCode")
    private int returnCode;
    @SerializedName("data")
    private T data;
    @SerializedName("success")
    private boolean success;
    @SerializedName("result")
    private String result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
