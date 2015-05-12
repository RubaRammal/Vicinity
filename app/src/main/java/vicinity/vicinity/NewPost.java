package vicinity.vicinity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.Random;

import vicinity.ConnectionManager.BroadcastManager;
import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.Post;

/**
 * An Activity to add a new post.
 */
public class NewPost extends ActionBarActivity {

    private static final String TAG = "NewPost";
    private EditText postTextField ;
    private Button sendImgButton;
    private MainController mc ;
    private BroadcastManager broadcastManager;
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;
    Post aPost;


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

        broadcastManager = new BroadcastManager();
        postTextField = (EditText) findViewById(R.id.postTextField);
        sendImgButton = (Button) findViewById(R.id.sendImageButton);
        sendImgButton.setEnabled(true);
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
                //sendImgButton.setEnabled(!TextUtils.isEmpty(postTextField.getText().toString().trim()));
            }
        }); //END addTextChangedListener


        sendImgButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                selectPicture();

            }
        });//END onClickListener

    } //END onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_send_post:
                sendPost();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendPost(){
        Random r = new Random();
        String postText = postTextField.getText().toString();
        try {
            //Can send post only if connected to a network
            if(Globals.isConnectedToANetwork){
                aPost.setPostBody(postText);
                aPost.setPostedBy(mc.retrieveCurrentUsername());
                aPost.setPostDate();
                aPost.setPostID(r.nextInt((1000 - 1) + 1) + 1);
                broadcastManager.setPost(aPost);
                broadcastManager.execute();
                finish();

            }
            else{
                CharSequence text = "You are not connected to a network!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(NewPost.this, text, duration);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                toast.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PICTURE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        sendPhotoObj(bitmap);
                    }
                    cursor.close();
                }
                break;
        }
    }

    public void sendPhotoObj(Bitmap b)  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(b,(int)(b.getWidth()*0.3), (int)(b.getHeight()*0.3), true);
        resized.compress(Bitmap.CompressFormat.JPEG, 5, baos);

        Log.i(TAG, resized.getHeight()* resized.getWidth()+"");
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        aPost.setBitmap(encodedImage);
        Toast.makeText(getApplicationContext(), "Photo attached to Post",
                Toast.LENGTH_LONG).show();
    }

    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
    }


}

