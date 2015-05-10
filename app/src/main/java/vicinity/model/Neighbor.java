
package vicinity.model;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.net.InetAddress;



/**
 * A class that defines the structure of a peer
 * (neighbor/friend/currentUser device info)
 */
public class Neighbor implements Serializable{



    protected String instanceName;//Device name
    protected String deviceAddress;//Device P2P MAC address
    private String _aliasName;//for friends only
    private InetAddress ipAddress;//Device local IP address
    private String status;//device status

    /**
     * Public constructor
     * @param deviceName username of the device
     * @param macAddress MAC address of the device
     * @param status status of the device (i.e AVAILABLE)
     */
    public Neighbor(String deviceName, String macAddress, String status){
       instanceName = deviceName;
       deviceAddress=macAddress;
       this.status=status;

    }

    /*----------------Parcelabel methods------------------
    private Neighbor(Parcel in){
        device = in.readParcelable(getClass().getClassLoader());
    }
    public int describeContents(){
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(device,flags);
    }
    public static final Parcelable.Creator<Neighbor> CREATOR = new Parcelable.Creator<Neighbor>() {
        public Neighbor createFromParcel(Parcel in) {
            return new Neighbor(in);
        }

        public Neighbor[] newArray(int size) {
            return new Neighbor[size];
        }
    };*/
    /*-----------------------------------------------------*/


    /*----------Setters and getters---------*/
    public void setInstanceName(String username){instanceName = username;}
    public void setDeviceAddress(String deviceAddress){this.deviceAddress = deviceAddress;}
    public void setIpAddress(InetAddress ip){this.ipAddress=ip;}
    public void setAliasName(String newName){_aliasName=newName;}
    public void setStatus(String status){this.status = status;}
    public String getDeviceAddress(){return this.deviceAddress;}
    public String getInstanceName(){return this.instanceName;}
    public String getAliasName(){
        return this._aliasName;
    }
    public InetAddress getIpAddress(){return this.ipAddress;}
    public String getStatus(){return status;}



    @Override
    public String toString(){
        return "Device name: "+instanceName+" MAC address: "+deviceAddress+" Status: "+status;
    }
}
