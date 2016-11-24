package hero.rxjava.mvp.model.user;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 创建人：DD
 * 类描述：BaseUser
 * 创建时间：2015/10/15  10:55
 */
public class UserBase implements Serializable {
   @SerializedName("id")
   private String id;
   //该记录是否无效：
   @SerializedName("invalid")
   private Boolean invalid;
   @SerializedName("status")
   private String status;////画家审核状态
   @SerializedName("createdDatetime")
   private String createdDatetime;

   // 最近修改时间
   @SerializedName("updatedDatetime")
   private String updatedDatetime;

   //排序标签
   @SerializedName("orderTag")
   private String orderTag;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Boolean getInvalid() {
      return invalid;
   }

   public void setInvalid(Boolean invalid) {
      this.invalid = invalid;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getCreatedDatetime() {
      return createdDatetime;
   }

   public void setCreatedDatetime(String createdDatetime) {
      this.createdDatetime = createdDatetime;
   }

   public String getUpdatedDatetime() {
      return updatedDatetime;
   }

   public void setUpdatedDatetime(String updatedDatetime) {
      this.updatedDatetime = updatedDatetime;
   }

   public String getOrderTag() {
      return orderTag;
   }

   public void setOrderTag(String orderTag) {
      this.orderTag = orderTag;
   }
}
