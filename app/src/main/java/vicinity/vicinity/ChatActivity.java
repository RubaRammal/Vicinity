package vicinity.vicinity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import vicinity.ConnectionManager.ChatManager;
import vicinity.model.CapturePhotoUtils;
import vicinity.model.Globals;
import vicinity.model.Photo;
import vicinity.model.VicinityMessage;


public class ChatActivity extends ActionBarActivity {


    private ListView chatListView;
    private EditText chatText;
    private Button send;
    private Button sendPhoto;

    private Boolean position;
    private VicinityMessage vicinityMessage;
    private Photo VicinityPhoto;
    private static File file;
    private static ChatManager chatManager;
    private static ChatAdapter adapter;
    private static Context ctx;
    ContentResolver contentResolver;
    private int SELECT_PICTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ActionBar abar = getSupportActionBar();
        abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01aef0")));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Friend's name");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);

        //Initializations
        ctx = this;
        chatListView = (ListView) findViewById(R.id.chatList);
        adapter = new ChatAdapter(ctx, R.layout.chat_box_layout);
        chatText = (EditText) findViewById(R.id.chatText);
        send = (Button) findViewById(R.id.sendButton);
        sendPhoto = (Button) findViewById(R.id.addphoto);
        contentResolver = ctx.getContentResolver();


        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatListView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatListView.setSelection(adapter.getCount() - 1);
            }
        });

        //When button send message is clicked
        send.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Globals.msgFlag = true;

                        Log.i(TAG,"ChatManager= "+chatManager);

                        if (chatManager != null) {

                            //Message
                            vicinityMessage = new VicinityMessage(ctx, "1", true, chatText.getText().toString());

                            //Display Message to user
                            pushMessage(vicinityMessage);

                            //Send vicinityMessage to ChatManager
                            chatManager.write(chatText.getText().toString().getBytes());
                            Log.i(TAG,"Writing vicinityMessage successful");


                            //To add vicinityMessage to db
                            try {
                                vicinityMessage.addMessage(vicinityMessage);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            //Clear EditText
                            chatText.setText(null);
                        }
                    }
                }
        );


        //When button (+)  is clicked

        sendPhoto.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Globals.msgFlag = false;

                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, SELECT_PICTURE);


                    }
                }


        );

    }



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


    public void sendPhotoObj( String photoPath){

        Log.i(TAG,"ChatManager= "+chatManager);

        if(chatManager != null) {
            VicinityPhoto = new Photo(ctx, "1", true, "", file);
            VicinityPhoto.setPhotoPath(photoPath);

            pushPhoto(VicinityPhoto);

            //File code (at the receiver side)
            file = new File(Environment.getExternalStorageDirectory() + "/"
                    + ctx.getPackageName() + "/Vicinity-" + System.currentTimeMillis()
                    + ".jpg");

            chatManager.write(photoPath.getBytes());
            Log.i(TAG, "Writing photo stream successful");

        }

    }


public static final String TAG = "ChatActivity";
    private static VicinityMessage message;
    private static Photo photo;

    public static Handler handler = new Handler(){
        /**
         *
         * @param msg
         * @return
         */
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG,"handleMessage");
            switch (msg.what) {
                case Globals.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, readMessage);
                    if(Globals.msgFlag ) {
                        message = new VicinityMessage(ctx, "2", false, readMessage);
                        Log.i(TAG, "message" + message.getMessageBody());
                        pushMessage(message);
                        break;
                    }
                    else       {

                        photo = new Photo(ctx, "1", true, readMessage, file);
                        Log.i(TAG, "photo" + photo.getphotoFile());
                        file = new File(Environment.getExternalStorageDirectory() + "/"
                                + ctx.getPackageName() + "/Vicinity-" + System.currentTimeMillis()
                                + ".jpg");
                        File dirs = new File(file.getParent());
                        if (!dirs.exists())
                            dirs.mkdirs();
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile(photo.getPhotoPath());
                        ContentResolver contentResolver = ctx.getContentResolver();
                        CapturePhotoUtils capturePhotoUtils = new CapturePhotoUtils();
                        capturePhotoUtils.insertImage(contentResolver , bitmap ,"Received Photo" ," Photo saved to receiver media gallery" );
                        Log.i(TAG,"Save photo to gallery");
                    }

                case Globals.MY_HANDLE:
                    Object obj = msg.obj;
                    chatManager = ((ChatManager) obj);
                    Log.i(TAG," "+obj.toString());

            }
        }
    };



    /**
     * Sends an object VicinityMessage to the Message adapter
     * to be displayed in the ChatActivity
     **/
    public static void pushMessage(VicinityMessage readVicinityMessage) {
        adapter.add(readVicinityMessage);
        adapter.notifyDataSetChanged();
    }


    public  static void pushPhoto(Photo photo){
        adapter.addPhoto(photo);
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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
