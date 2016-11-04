package hero.rxjava.mvp.model.enums;

/**
 * Created by hero on 2016/6/17 0017.
 */
public enum NetCodeNormal {
    SUCCESS(0000,"返回成功"),UPDATE_FORCE(2,"强制升级"),LOGIN_TIME_OUT(3,"登录超时");
    private int code;
    private String desc;
    NetCodeNormal(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
