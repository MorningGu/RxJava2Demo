package hero.rxjava.mvp.model.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 创建人：DD
 * 类描述：登录返回数据
 * 创建时间：2015/10/14  16:21
 */
public class UserData implements Serializable {
   @SerializedName("userInfo")
   private UserInfo userInfo;

   public UserInfo getUserInfo() {
      return userInfo;
   }

   public void setUserInfo(UserInfo userInfo) {
      this.userInfo = userInfo;
   }


}
