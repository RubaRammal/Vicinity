package vicinity.vicinity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import vicinity.model.Globals;
import vicinity.model.Photo;
import vicinity.model.VicinityMessage;

/**
 * This class is responsible for displaying
 * message objects in activity_chat
 */
public class ChatAdapter extends ArrayAdapter<VicinityMessage>  {

    private List<VicinityMessage > vicinityMessages;
    //private List<Photo> photos;//I need it in order to call a method in Photo class
    private LayoutInflater mInflater;
    private Context ctx;


    //Constructor
    public ChatAdapter(Context context, int resource) {
        super(context, resource);
        vicinityMessages = new ArrayList<>();
        //photos = new ArrayList<>();
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

 public void addPhoto(Photo photo){

        vicinityMessages.add(photo);
        super.add(photo);

    }

    /**
     * Google this method if you wanna understand it. - Ruba
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (Globals.msgFlag) {
            if (convertView == null) {


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

            if (!vicinityMessages.get(position).isMyMsg()) {
                params.gravity = Gravity.LEFT;
            } else {
                params.gravity = Gravity.RIGHT;
            }

            holder.chat_text.setLayoutParams(params);
        }
        else{
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.chat_box_layout, null);
                holder = new ViewHolder();
                holder.image_view = (ImageView) convertView.findViewById(R.id.imageBox);
                convertView.setTag(holder);

            }
            else{
                holder = (ViewHolder) convertView.getTag();

            }
            //getting the photo path from Vicinity Message instead of photo
            Bitmap bitmap = BitmapFactory.decodeFile(vicinityMessages.get(position).getPhotoPath());

            holder.image_view.setImageBitmap(bitmap);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            if (!vicinityMessages.get(position).isMyMsg()) {
                params.gravity = Gravity.LEFT;
            } else {
                params.gravity = Gravity.RIGHT;
            }

            holder.image_view.setLayoutParams(params);
        }

        return convertView;

    }


    static class ViewHolder{
        TextView chat_text;
        TextView name_text;
        ImageView image_view;    }
}

