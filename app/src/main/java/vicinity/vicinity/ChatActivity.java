package vicinity.vicinity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import vicinity.ConnectionManager.ChatClient;
import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.Neighbor;
import vicinity.model.VicinityMessage;


public class ChatActivity extends ActionBarActivity {


    private ListView chatListView;
    private EditText chatText;
    private Button send;
    private Button sendImgButton;
    private VicinityMessage vicinityMessage;
    private static ChatAdapter adapter;
    public static Context ctx;
    private MainController controller;
    private int chatID;
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;
    private VicinityMessage imgMsg;
    private static String TAG = "ChatActivity";

    private static VicinityMessage message;
    private BroadcastReceiver newMessage;
    private ChatClient chatClient;
    private String friendsIp;
    private Neighbor friendChat;
    private Thread chatThread;




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


        ArrayList<VicinityMessage> vicinityMessages = controller.viewAllMessages();


        imgMsg = new VicinityMessage();

        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        try{
        savedInstanceState = getIntent().getExtras();
            if(savedInstanceState.getSerializable("FRIEND") instanceof Neighbor)
            {
                friendChat = (Neighbor) savedInstanceState.getSerializable("FRIEND");
                chatClient = new ChatClient(ctx,friendChat.getIpAddress().getHostAddress());
                friendsIp = friendChat.getIpAddress().getHostAddress();
                textviewTitle.setText(friendChat.getInstanceName());
            }
            else if(savedInstanceState.getSerializable("MSG") instanceof VicinityMessage){
                message = (VicinityMessage) savedInstanceState.getSerializable("MSG");
                chatClient = new ChatClient(ctx,message.getFrom());
                textviewTitle.setText(message.getFriendID());
                friendsIp = message.getFrom();

            }


        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        getHistory();


        chatThread = new Thread(chatClient);
        chatThread.start();
        controller.addClientThread(chatClient);


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
                        try {
                        Log.i(TAG, "onClick ");

                        //Message
                        vicinityMessage = new VicinityMessage(ctx, controller.retrieveCurrentUsername(),
                                    5, true, chatText.getText().toString());

                        vicinityMessage.setFrom(friendsIp);
                        //Display Message to user
                        pushMessage(vicinityMessage);

                        chatClient.write(vicinityMessage);
                        Log.i(TAG, "Writing vicinityMessage successful");

                        //To add vicinityMessage to db
                            boolean added = controller.addMessage(vicinityMessage);
                            if (added)
                                Log.i(TAG, "Message added");

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        //Clear EditText
                        chatText.setText(null);
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

        newMessage = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    final Bundle bundle = intent.getExtras();

                        Log.i(TAG,"onReceive");
                        VicinityMessage vMessage = (VicinityMessage) bundle.getSerializable("NEW_MESSAGE");
                        Log.i(TAG,"Received new message: "+vMessage.getMessageBody());
                        friendsIp = vMessage.getFrom();
                        pushMessage(vMessage);

                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("MESSAGE");
        LocalBroadcastManager.getInstance(ctx).registerReceiver((newMessage), filter);

    }

    /**
     * Sends an object VicinityMessage to the Message adapter
     * to be displayed in the ChatActivity
     **/
    public static void pushMessage(VicinityMessage readVicinityMessage) {
        adapter.add(readVicinityMessage);
        adapter.notifyDataSetChanged();
    }


    private void getHistory() {
        try {
            Log.i(TAG, "History: "+ friendsIp);

            ArrayList<VicinityMessage> m = controller.getChatMessages(friendsIp);

            for (int i = 0; i < m.size(); i++) {
                pushMessage(m.get(i));
                Log.i(TAG, "History: "+ m.get(i).getMessageBody());
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
                        try {
                            sendPhotoObj(bitmap);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    cursor.close();
                }
                break;
        }
    }

    public void sendPhotoObj(Bitmap b) throws SQLException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(b,(int)(b.getWidth()*0.3), (int)(b.getHeight()*0.3), true);
        resized.compress(Bitmap.CompressFormat.JPEG, 2, baos);

        Log.i(TAG, resized.getHeight()* resized.getWidth()+"");
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        imgMsg.setImageString(encodedImage);
        friendsIp = imgMsg.getFrom();
        pushMessage(imgMsg);

        chatText.setText("Photo is attached, click send");
    }


    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
    }


    @Override
    public void onStart() {
        super.onStart();
        Globals.chatActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Globals.chatActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //chatThread.stop();

    }
}
