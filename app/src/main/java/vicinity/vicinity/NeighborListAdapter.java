package vicinity.vicinity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.BaseAdapter;


import java.util.ArrayList;

import vicinity.model.WiFiP2pService;



public class NeighborListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public static ArrayList<WiFiP2pService> services;
    public ProgressBar progressbar;

    public NeighborListAdapter(Context context, ArrayList<WiFiP2pService> services2){
        services = services2;
        mInflater = LayoutInflater.from(context);

    }



    public void setPB(ProgressBar pb){
        progressbar = pb;

    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.neighbor_row_view, null);


            holder = new ViewHolder();
            holder.textName = (TextView) convertView.findViewById(R.id.neighbor_row);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textName.setText(services.get(position).getInstanceName());


        return convertView;
    }

    static class ViewHolder{
        TextView textName;
    }

    public void clear(){
        services.clear();
    }
}
