package vicinity.vicinity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import vicinity.ConnectionManager.PostManager;
import vicinity.Controller.MainController;
import vicinity.model.Post;

public class NewPost extends ActionBarActivity {

    private static final String TAG = "NewPost";
    private EditText postTextField ;
    private Button sendPostButton;
    private Button sendPhotoButton;
    private MainController mc ;
    private PostManager postManager;
    private Post aPost;
    private int SELECT_PICTURE =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);



        final ActionBar abar = getSupportActionBar();
        abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01aef0")));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("New Post");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        // I disabled the back button in the action bar cuz it causes an error
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(true);

        mc = new MainController(this);

        postManager = new PostManager(this);
        postTextField = (EditText) findViewById(R.id.postTextField);
        sendPostButton = (Button) findViewById(R.id.sendPostButton);
        sendPhotoButton = (Button) findViewById(R.id.AddButton);
        sendPostButton.setEnabled(false);
        aPost = new Post();
        postTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendPostButton.setEnabled(!TextUtils.isEmpty(postTextField.getText().toString().trim()));
            }
        }); //END addTextChangedListener

        // When the send button is clicked
        sendPostButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View view) {
                        String postText = postTextField.getText().toString();

                        aPost.setPostBody(postText);

                        postManager.setPost(aPost);

                        postManager.execute();

                           /* if (mc.addPost(aPost))
                                Log.i(TAG, "post is saved to DB");
                            else
                                Log.i(TAG, "post is not saved to DB")*/

                        // postToTimeline(aPost);
                        finish();
                    }
                }
        ); //END onClickListener

        sendPhotoButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, SELECT_PICTURE);

            }
        });//END onClickListener
    } //END onCreate


    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.i(TAG, picturePath);
            cursor.close();
            sendPhotoObj(picturePath);


        }

    }
    public void sendPhotoObj( String photoPath)  {
            aPost.setPhotoPath(photoPath);
             sendPostButton.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Photo attached to Post",
                Toast.LENGTH_LONG).show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

