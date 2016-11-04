package hero.rxjava.mvp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hero on 2016/7/8 0008.
 */
public class Version {
    @SerializedName("version")
    private VersionInfo version;

    public VersionInfo getVersion() {
        return version;
    }

    public void setVersion(VersionInfo version) {
        this.version = version;
    }

   public class VersionInfo{
       @SerializedName("versions")
       private String versions;
       @SerializedName("serviceAddress")
       private String serviceAddress;
       @SerializedName("size")
       private int size;

       public String getVersions() {
            return versions;
        }

       public void setVersions(String versions) {
            this.versions = versions;
        }

       public String getServiceAddress() {
            return serviceAddress;
        }

       public void setServiceAddress(String serviceAddress) {
           this.serviceAddress = serviceAddress;
       }

       public int getSize() {
            return size;
        }

       public void setSize(int size) {
            this.size = size;
        }
    }

}
