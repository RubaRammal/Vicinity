package vicinity.model;
import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import vicinity.ConnectionManager.WiFiP2pService;

/**
 * A structure class for holding friends information
 * this class extends WiFiP2pService
 */
public class Friend extends WiFiP2pService{

    private ArrayList<VicinityMessage> _privateMessages;
    private String _aliasName;

    public Friend(String instanceName, String deviceAddress){
        this.instanceName = instanceName;
        this.deviceAddress=deviceAddress;
    }
    public Friend(WifiP2pDevice device){
        super(device);

    }


    /**
     *
     * Setters-getters
     */
    public String getAliasName(){
        return this._aliasName;
    }
    public void setAliasName(String newName){_aliasName=newName;}
    public ArrayList<VicinityMessage> getPrivateMessages(){
        return this._privateMessages;
    }






}
