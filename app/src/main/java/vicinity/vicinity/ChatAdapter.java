package vicinity.vicinity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vicinity.model.VicinityMessage;

/**
 * This class is responsible for displaying
 * message objects in activity_chat
 */
public class ChatAdapter extends ArrayAdapter<VicinityMessage>  {

    private List<VicinityMessage > vicinityMessages;
    private LayoutInflater mInflater;
    private Context ctx;
    private String TAG = "ChatAdapter";


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



    @Override //since vicinityMessage list array is displayed these overridden methods are needed
    public int getCount() {
        return vicinityMessages.size();
    }

    @Override
    public VicinityMessage getItem(int position) {
        return vicinityMessages.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {


            convertView = mInflater.inflate(R.layout.chat_box_layout, null);

            holder = new ViewHolder();
            holder.chat_text = (TextView) convertView.findViewById(R.id.chatBox);
            holder.name_text = (TextView) convertView.findViewById(R.id.nameOfFriend);
            holder.image_view = (ImageView) convertView.findViewById(R.id.imageBox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Bitmap bitmap = BitmapFactory.decodeFile(vicinityMessages.get(position).getPhotoPath());
        holder.image_view.setVisibility(View.GONE);
        holder.chat_text.setVisibility(View.GONE);

        String m = vicinityMessages.get(position).getPhotoPath().substring(0,9);
        Log.i(TAG,m);


        if(vicinityMessages.get(position).getMessageBody() == null){
            holder.image_view.setVisibility(View.VISIBLE);
            holder.image_view.setImageBitmap(bitmap);
            holder.image_view.setBackgroundDrawable(vicinityMessages.get(position).isMyMsg() ?
                    ctx.getResources().getDrawable(R.drawable.chatboxright) : ctx.getResources().getDrawable(R.drawable.chatboxleft));

        }
        else {

            holder.chat_text.setVisibility(View.VISIBLE);
            holder.chat_text.setText(vicinityMessages.get(position).getMessageBody());
            holder.chat_text.setBackgroundDrawable(vicinityMessages.get(position).isMyMsg() ?
                    ctx.getResources().getDrawable(R.drawable.chatboxright) : ctx.getResources().getDrawable(R.drawable.chatboxleft));
        }



        holder.name_text.setText(vicinityMessages.get(position).isMyMsg() ? "" : "Sarah");

        //getting the photo path from Vicinity Message instead of photo
            /*Here i started thinking we might need to add one more attribute in VicinityMessage to
             hold the photo bitmap instead instead on converting to bitmap here and at the receiver
             side (in chat activity)*/


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (!vicinityMessages.get(position).isMyMsg()) {
            params.gravity = Gravity.LEFT;
        } else {
            params.gravity = Gravity.RIGHT;
        }

        holder.chat_text.setLayoutParams(params);

        return convertView;

    }


    static class ViewHolder{
        TextView chat_text;
        TextView name_text;
        ImageView image_view;    }
}

