package vicinity.vicinity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;


import java.util.ArrayList;


/**
 * Created by macproretina on 3/23/15.
 */
public class NeighborListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    ArrayList<String> services;

    public NeighborListAdapter(Context context, ArrayList<String> services){
        this.services = services;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position);
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

        holder.textName.setText(services.get(position));


        return convertView;
    }

    static class ViewHolder{
        TextView textName;
    }
}
