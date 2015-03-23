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

import vicinity.model.VicinityMessage;

/**
 * This class is responsible for displaying
 * message objects in activity_chat
 */
public class ChatAdapter extends ArrayAdapter<VicinityMessage> {

    private List<VicinityMessage> vicinityMessages;
    private LayoutInflater mInflater;
    private Context ctx;

    //Constructor
    public ChatAdapter(Context context, int resource) {
        super(context, resource);
        vicinityMessages = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        ctx = context;

    }

    @Override
    public void add(VicinityMessage object) {
        vicinityMessages.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return vicinityMessages.size();
    }

    @Override
    public VicinityMessage getItem(int position) {
        return vicinityMessages.get(position);
    }

    /**
     * Google this method if you wanna understand it. - Ruba
     */
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

        holder.chat_text.setText(vicinityMessages.get(position).getMessageBody());

            holder.chat_text.setBackgroundDrawable(vicinityMessages.get(position).isMyMsg() ?
                    ctx.getResources().getDrawable(R.drawable.chatboxright) : ctx.getResources().getDrawable(R.drawable.chatboxleft));

        holder.name_text.setText(vicinityMessages.get(position).isMyMsg() ? "" : "Sarah");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if(!vicinityMessages.get(position).isMyMsg()){
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

