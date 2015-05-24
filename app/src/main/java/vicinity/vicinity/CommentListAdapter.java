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
 * An adapter that takes a list of Comment objects
 * and binds it to a ListView,  each list item
 * representing a Comment object
 */
public class CommentListAdapter extends BaseAdapter {

    private List<Comment> commentsList;
    private LayoutInflater mInflater;

    /**
     * Public constructor
     * @param context Context
     * @param comments ArrayList of Comment objects
     */
    public CommentListAdapter(Context context, ArrayList<Comment> comments) {
        commentsList = comments;
        mInflater = LayoutInflater.from(context);
    }

                    /*----------Overridden Methods------------*/

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

        holder.name_text.setText(commentsList.get(position).getCommentedBy());
        holder.body_text.setText(commentsList.get(position).getCommentBody());

        return convertView;
    }

    static class ViewHolder{
        TextView name_text, body_text;
         }
}

