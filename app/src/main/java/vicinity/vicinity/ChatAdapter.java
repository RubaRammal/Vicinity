package vicinity.vicinity;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.List;

import vicinity.model.Message;

/**
 * Created by macproretina on 3/10/15.
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private List<Message> messages;
    private LayoutInflater mInflater;
    private Context ctx;

    public ChatAdapter(Context context, int resource) {
        super(context, resource);
        messages = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        ctx = context;

    }

    @Override
    public void add(Message object) {
        messages.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.chat_box_layout, null);

            holder = new ViewHolder();
            holder.chat_text = (TextView) convertView.findViewById(R.id.chatBox);
            holder.name_text = (TextView) convertView.findViewById(R.id.nameOfFriend);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.chat_text.setText(messages.get(position).getMessageBody());

            holder.chat_text.setBackgroundDrawable(messages.get(position).isMyMsg() ?
                    ctx.getResources().getDrawable(R.drawable.chatboxright) : ctx.getResources().getDrawable(R.drawable.chatboxleft));

        holder.name_text.setText(messages.get(position).isMyMsg() ? "" : "Sarah");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if(!messages.get(position).isMyMsg()){
            params.gravity = Gravity.LEFT;
        }
        else{
            params.gravity = Gravity.RIGHT;
        }

        holder.chat_text.setLayoutParams(params);

        return convertView;
    }

    static class ViewHolder{
         TextView chat_text, name_text;    }
}

