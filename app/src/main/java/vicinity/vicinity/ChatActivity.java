package vicinity.vicinity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;

import vicinity.ConnectionManager.ChatManager;
import vicinity.Controller.MainController;
import vicinity.Controller.VicinityNotifications;
import vicinity.model.Globals;
import vicinity.model.VicinityMessage;


public class ChatActivity extends ActionBarActivity {


    private ListView chatListView;
    private EditText chatText;
    private Button send;
    private Button sendImgButton;
    private Boolean position;
    private VicinityMessage vicinityMessage;
    private static ChatManager chatManager;
    private static ChatAdapter adapter;
    public static Context ctx;
    private static MainController controller;
    private int msgId;
    ArrayList<VicinityMessage> history;
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;
    private VicinityMessage imgMsg;



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
        textviewTitle.setText("Amal");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(true);

        //Initializations
        ctx = this;
        chatListView = (ListView) findViewById(R.id.chatList);
        adapter = new ChatAdapter(ctx, R.layout.chat_box_layout);
        chatText = (EditText) findViewById(R.id.chatText);
        send = (Button) findViewById(R.id.sendButton);
        sendImgButton = (Button) findViewById(R.id.sendImage);
        controller = new MainController(ctx);

        imgMsg = new VicinityMessage();
        try {
            new VicinityMessage(ctx,  controller.retrieveCurrentUsername(),
                    5, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                msgId = 0;
            } else {
                msgId = extras.getInt("MSG_ID");
            }
        } else {
            msgId = (int) savedInstanceState.getSerializable("MSG_ID");
        }

        Log.i(TAG, msgId+"");

        MessagesSectionFragment m = new MessagesSectionFragment();
        history = m.getMsgs();
//        Log.i(TAG, history.get(0).getMessageBody());

        //getHistory();

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

                        Log.i(TAG,"ChatManager= "+chatManager);

                        if (chatManager != null) {

                            //Message

                            try {
                                vicinityMessage = new VicinityMessage(ctx,  controller.retrieveCurrentUsername(),
                                        5, true, chatText.getText().toString());
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }


                            //Display Message to user
                            pushMessage(vicinityMessage);

                            String jsonstring = controller.shiftInsertMessage(vicinityMessage);

                            chatManager.write(jsonstring.getBytes());
                            Log.i(TAG,"Writing vicinityMessage successful");


                            //To add vicinityMessage to db
                            try {
                                boolean added = controller.addMessage(vicinityMessage);
                                if(added)
                                Log.i(TAG, "Message added");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            //Clear EditText
                            chatText.setText(null);
                        }
                    }
                }
        );

        sendImgButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {


                        selectPicture();

                    }
                }
        );


    }


    public static final String TAG = "ChatActivity";
    private static VicinityMessage message;

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

                    message = VicinityMessage.parseMessageRow(readMessage);

                    message.setIsMyMsg(false);

                    if(Globals.Notification)
                    VicinityNotifications.newMessageNotification(message);
                    Log.i(TAG,"message "+message.getMessageBody());
                    pushMessage(message);
                    try {
                        controller.addMessage(message);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    break;

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


    private void getHistory() {
        try {
            Log.i(TAG, msgId + "");


            ArrayList<VicinityMessage> m = new ArrayList<VicinityMessage>();
            m = controller.getChatMessages(msgId);

            for (int i = 0; i < m.size(); i++) {
                pushMessage(m.get(i));
                Log.i(TAG, m.get(i).getMessageBody());

            }
        } catch (IndexOutOfBoundsException e) {
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
        resized.compress(Bitmap.CompressFormat.JPEG, 1, baos);

        Log.i(TAG, resized.getHeight()* resized.getWidth()+"");
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        imgMsg.setImageString(encodedImage);

        pushMessage(imgMsg);

        String jsonstring = controller.shiftInsertMessage(imgMsg);
        chatManager.write(jsonstring.getBytes());
        //chatText.setText("Photo is attached, click send");


    }




    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
    }

}
