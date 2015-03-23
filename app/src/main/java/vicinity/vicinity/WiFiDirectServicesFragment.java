
package vicinity.vicinity;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * ListFragment that shows the available services as published by the
 * peers
 * Ruba: I changed this from FragmentList to a Fragment so that I could return it as a tab
 * in Tabs class. I added a ListView in the fragment_neighbor and linked it to this fragment class
 * If use Fragment instead of v4.app.Fragment, an error appears in Tabs class in the getItem method
 * v4.app.Fragment caused errors in WiFiServiceDiscoveryActivity...
 */
public class WiFiDirectServicesFragment extends Fragment {

    WiFiDevicesAdapter listAdapter = null;
    ListView lv;

    interface DeviceClickListener {
       public void connectP2p(WiFiP2pService wifiP2pService);
   }

    public WiFiDirectServicesFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neighbor, container, false);

        lv = (ListView) rootView.findViewById(android.R.id.list);

        listAdapter = new WiFiDevicesAdapter(this.getActivity(),
                android.R.layout.simple_list_item_2, android.R.id.text1,
                new ArrayList<WiFiP2pService>());

        lv.setAdapter(listAdapter);

       lv.setOnItemClickListener(
              new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ((DeviceClickListener) getActivity()).connectP2p((WiFiP2pService) parent
                               .getItemAtPosition(position));
                        ((TextView) view.findViewById(android.R.id.text2)).setText("Connecting");
                    }
                }
        );

        return rootView;
    }

    public ListView getListView(){
        return lv;
    }

    public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {

        private List<WiFiP2pService> items;
        private Context context;

        public WiFiDevicesAdapter(Context context, int resource,
                                  int textViewResourceId, List<WiFiP2pService> items) {
            super(context, resource, textViewResourceId, items);
            this.items = items;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_2, null);
            }
            WiFiP2pService service = items.get(position);
            if (service != null) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);

                if (nameText != null) {
                    nameText.setText(service.device.deviceName + " - " + service.instanceName);
                }
                TextView statusText = (TextView) v
                        .findViewById(android.R.id.text2);
                statusText.setText(getDeviceStatus(service.device.status));
            }
            return v;
        }


    }

    public static String getDeviceStatus(int statusCode) {
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

}
