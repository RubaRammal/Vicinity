
package vicinity.model;
import com.google.common.net.InetAddresses;

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
     * Default constructor
     */
    public Neighbor(){}

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

    /**
     * Setters And Getters
     */

    public void setInstanceName(String username)
    {
        instanceName = username;
    }

    public void setDeviceAddress(String deviceAddress)
    {
        this.deviceAddress = deviceAddress;
    }

    public void setIpAddress(String ip)
    {
        this.ipAddress = InetAddresses.forString(ip);
    }

    public void setAliasName(String newName)
    {
        _aliasName = newName;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDeviceAddress()
    {
        return this.deviceAddress;
    }

    public String getInstanceName()
    {
        return this.instanceName;
    }

    public String getAliasName()
    {
        return this._aliasName;
    }

    public InetAddress getIpAddress()
    {
        return this.ipAddress;
    }

    public String getStatus()
    {
        return status;
    }

    /*---------Overridden Methods------------*/

    @Override
    public String toString()
    {
        return "Device name: "+instanceName+" MAC address: "+deviceAddress+" Status: "+status;
    }
}
