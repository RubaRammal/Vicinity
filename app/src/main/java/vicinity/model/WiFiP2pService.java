
package vicinity.model;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * A structure to hold service information.
 */
public class WiFiP2pService implements Parcelable{
    protected WifiP2pDevice device;
    protected String instanceName;
    protected String serviceRegistrationType;
    protected String deviceAddress;
    private String _aliasName;//for friends only
    private String ipAddress;



    /**
     * Public constructor to initiate a full WiFiP2pService
     */
    public WiFiP2pService(WifiP2pDevice device){
        this.device=device;
        instanceName = device.deviceName;
        deviceAddress=device.deviceAddress;
    }

    /*----------------Parcelabel methods------------------*/
    private WiFiP2pService(Parcel in){
        device = in.readParcelable(getClass().getClassLoader());
    }
    public int describeContents(){
        return 0;
    }
    @Override
    public String toString(){
        return "Device name: "+instanceName+" MAC address: "+deviceAddress+" Status: "+device.status;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(device,flags);
    }
    public static final Parcelable.Creator<WiFiP2pService> CREATOR = new Parcelable.Creator<WiFiP2pService>() {
        public WiFiP2pService createFromParcel(Parcel in) {
            return new WiFiP2pService(in);
        }

        public WiFiP2pService[] newArray(int size) {
            return new WiFiP2pService[size];
        }
    };
    /*-----------------------------------------------------*/


    public void setDevice(WifiP2pDevice device){
        this.device=device;
    }
    public WifiP2pDevice getDevice(){return this.device;}
    public void setInstanceName(String instanceName){
        this.instanceName=instanceName;
    }
    public void setDeviceAddress(String deviceAddress){this.deviceAddress = deviceAddress;}
    public String getDeviceAddress(){return this.deviceAddress;}
    public String getInstanceName(){return this.instanceName;}
    public void setServiceRegistrationType(String serviceRegistrationType){
        this.serviceRegistrationType=serviceRegistrationType;
    }
    public String getServiceRegistrationType(){return this.serviceRegistrationType;}
    public String getAliasName(){
        return this._aliasName;
    }
    public void setAliasName(String newName){_aliasName=newName;}


}
