package vicinity.vicinity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;

import vicinity.Controller.MainController;
import vicinity.model.Post;

/**
 * An adapter that takes a list of posts and displays it in a ListView
 * inside the TimelineSectionFragment
 */
public class PostListAdapter  extends BaseAdapter {
    public static ArrayList<Post> posts;
    private LayoutInflater mInflater;
    MainController controller;


    public PostListAdapter(Context context, ArrayList<Post> postsList){
        posts = postsList;
        mInflater = LayoutInflater.from(context);
        controller = new MainController(context);
    }
    public PostListAdapter(Context context){
        posts = new ArrayList<>();
        controller = new MainController(context);
        posts.addAll(controller.viewAllPosts());
        mInflater = LayoutInflater.from(context);


    }


    public void addPost(Post p){
        posts.add(p);
        notifyDataSetChanged();
        try {
            controller.addPost(p);
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageBox);
            holder.txtComments  = (TextView) convertView.findViewById(R.id.comments);
            holder.txtDate  = (TextView) convertView.findViewById(R.id.date);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtPost.setVisibility(View.GONE);
        holder.imageView.setVisibility(View.GONE);

        holder.txtName.setText(posts.get(position).getPostedBy());
        holder.txtComments.setText("0 comments");
        holder.txtDate.setText(posts.get(position).getPostedAt());

        if(!posts.get(position).getPostBody().equals("")){
            holder.txtPost.setVisibility(View.VISIBLE);
            holder.txtPost.setText(posts.get(position).getPostBody());
        }

        try {
            if(!posts.get(position).getBitmap().equals("")){
                holder.imageView.setVisibility(View.VISIBLE);
                String imageBitmap = posts.get(position).getBitmap();
                byte[] decodedString = Base64.decode(imageBitmap, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageView.setImageBitmap(decodedByte);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }

    static class ViewHolder{
        TextView txtName, txtPost, txtComments, txtDate;
        ImageView imageView;
    }

    public  static void clearPosts(){
        posts.clear();
        TimelineSectionFragment.clearPosts();
    }



}