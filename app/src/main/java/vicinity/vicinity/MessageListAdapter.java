package vicinity.vicinity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vicinity.model.Message;
import vicinity.vicinity.R;

/**
 * Created by macproretina on 3/10/15.
 */
public class MessageListAdapter  extends BaseAdapter {
    ArrayList<Message> messages;
    private LayoutInflater mInflater;


    public MessageListAdapter(Context context, ArrayList<Message> messages){
        this.messages = messages;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.messages_row_view, null);


            holder = new ViewHolder();
            holder.textDate = (TextView) convertView.findViewById(R.id.date);
            holder.textName = (TextView) convertView.findViewById(R.id.friendName);
            holder.textMessage = (TextView) convertView.findViewById(R.id.latestMessage);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textDate.setText(messages.get(position).getDate());
        holder.textName.setText(messages.get(position).getFriendID());
        holder.textMessage.setText(messages.get(position).getMessageBody());


        return convertView;
    }

    static class ViewHolder{
        TextView textDate, textMessage,textName;
    }
}
