package vicinity.vicinity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vicinity.model.Post;
import vicinity.vicinity.R;

/**
 * An adapter that takes a list of posts and displays it in a ListView
 * inside the TimelineSectionFragment
 */
public class PostListAdapter  extends BaseAdapter {
    ArrayList<Post> posts;
    private LayoutInflater mInflater;


    public PostListAdapter(Context context, ArrayList<Post> posts){
        this.posts = posts;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.timeline_row_view, null);


            holder = new ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.name);
            holder.txtPost = (TextView) convertView.findViewById(R.id.post);
            holder.txtComments  = (TextView) convertView.findViewById(R.id.comments);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(posts.get(position).getPostedBy().getUsername());
        holder.txtPost.setText(posts.get(position).getPostBody());
        holder.txtComments.setText("0 comments");

        return convertView;
    }

    static class ViewHolder{
        TextView txtName, txtPost, txtComments;
    }
}