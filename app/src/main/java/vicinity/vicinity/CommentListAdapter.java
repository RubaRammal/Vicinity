package vicinity.vicinity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vicinity.model.Comment;


/**
 * Created by macproretina on 4/2/15.
 */
public class CommentListAdapter extends BaseAdapter {

    private List<Comment> commentsList;
    private LayoutInflater mInflater;
    private Context ctx;

    //Constructor
    public CommentListAdapter(Context context, ArrayList<Comment> comments) {
        commentsList = comments;
        mInflater = LayoutInflater.from(context);
        ctx = context;


    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public void add(Comment object) {
        commentsList.add(object);
    }

    @Override
    public int getCount() {
        return commentsList.size();
    }

    @Override
    public Comment getItem(int position) {
        return commentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return commentsList.get(position).getCommentID();
    }

    /**
     * Google this method if you wanna understand it. - Ruba
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.comment_layout, null);

            holder = new ViewHolder();
            holder.name_text = (TextView) convertView.findViewById(R.id.commetName);
            holder.body_text = (TextView) convertView.findViewById(R.id.commentBody);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       /* View row = convertView.findViewById(R.id.commentRow);

        //The post row
        if(position==0){
            row.setPadding(30,30,30,30);
            row.setBackgroundColor(Color.parseColor("#E1E1E1"));
            holder.name_text.setTextSize(20);
            holder.body_text.setTextSize(18);
        }

        //The label Comments row
        if(position==1){
            row.setPadding(10,10,10,10);
            row.setBackgroundColor(Color.parseColor("#BFBFBF"));
            holder.name_text.setTextColor(Color.parseColor("#000000"));
            holder.name_text.setTextSize(18);
            holder.body_text.setTextSize(0);
            holder.name_text.setPadding(5,5,5,5);

        }*/



        holder.name_text.setText(commentsList.get(position).getCommentedBy());

        holder.body_text.setText(commentsList.get(position).getCommentBody());


        return convertView;
    }

    static class ViewHolder{
        TextView name_text, body_text;
         }
}

