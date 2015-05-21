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
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
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
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import vicinity.ConnectionManager.ChatClient;
import vicinity.ConnectionManager.ServiceRequest;
import vicinity.Controller.MainController;
import vicinity.Controller.VicinityNotifications;
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
    public static String friendsIp;
    private Neighbor friendChat;
    private Thread chatThread;
    private boolean gettingImage = false;




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
        imgMsg.setChatId(5);
        imgMsg.setIsMyMsg(true);
        imgMsg.setMessageBody("");

        try {
            imgMsg.setFriendID(controller.retrieveCurrentUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        try{
            savedInstanceState = getIntent().getExtras();
            if(savedInstanceState.getSerializable("FRIEND") instanceof Neighbor)
            {
                friendChat = (Neighbor) savedInstanceState.getSerializable("FRIEND");
                chatClient = new ChatClient(friendChat.getIpAddress().getHostAddress());
                friendsIp = friendChat.getIpAddress().getHostAddress();
                textviewTitle.setText(friendChat.getInstanceName());
            }
            else if(savedInstanceState.getSerializable("MSG") instanceof VicinityMessage){
                message = (VicinityMessage) savedInstanceState.getSerializable("MSG");
                chatClient = new ChatClient(message.getFrom());
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


/*
        if(!(NeighborSectionFragment.chatClient == null)){
            Log.i(TAG, "Chat client from neighbor fragment");
        }
        else if(!chatThread.isAlive()){
            Log.i(TAG, "Chat client from chat activity");
        }
*/

        //chatThread = new Thread(chatClient);
            //chatThread.start();

        //controller.addClientThread(chatClient);


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

                            /*if(!(NeighborSectionFragment.chatClient == null)){
                                NeighborSectionFragment.chatClient.write(vicinityMessage);
                                Log.i(TAG, "Writing vicinityMessage successful");
                            }
                            else {
                                Log.i(TAG, "Writing vicinityMessage successful");
                            }*/

                            chatClient.write(vicinityMessage);


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
                        gettingImage = true;
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
                    intent.removeExtra("NEW_MESSAGE");

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
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(filePath, bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        Bitmap rotatedBitmap = decodeFile(new File(filePath),
                                photoW, photoH, getImageOrientation(filePath));

                        try {
                            sendPhotoObj(rotatedBitmap);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    cursor.close();
                }
                break;
        }
    }

    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap decodeFile(File f, double REQUIRED_WIDTH,
                                    double REQUIRED_HEIGHT, int rotation) {
        try {
            if (REQUIRED_WIDTH == 0 || REQUIRED_HEIGHT == 0) {
                return BitmapFactory.decodeFile(f.getAbsolutePath());
            } else {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(f.getAbsolutePath(), o);

                o.inSampleSize = calculateInSampleSize(o, REQUIRED_WIDTH,
                        REQUIRED_HEIGHT);

                o.inJustDecodeBounds = false;
                o.inPurgeable = true;
                Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath(), o);
                if (rotation != 0)
                    b = rotate(b, rotation);
                if (b.getWidth() > REQUIRED_WIDTH
                        || b.getHeight() > REQUIRED_HEIGHT) {
                    double ratio = Math.max((double) b.getWidth(),
                            (double) b.getHeight())
                            / (double) Math
                            .min(REQUIRED_WIDTH, REQUIRED_HEIGHT);

                    return Bitmap.createScaledBitmap(b,
                            (int) (b.getWidth() / ratio),
                            (int) (b.getHeight() / ratio), true);
                } else
                    return b;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            double reqWidth, double reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((height / inSampleSize) > reqHeight
                    || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        inSampleSize = Math.max(1, inSampleSize / 2);
        return inSampleSize;
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2,
                    (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    public void sendPhotoObj(Bitmap b) throws SQLException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(b,(int)(b.getWidth()*0.3), (int)(b.getHeight()*0.3), true);
        resized.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        Log.i(TAG, resized.getHeight()* resized.getWidth()+"");
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        imgMsg.setImageString(encodedImage);
        imgMsg.setFrom(friendsIp);
        Log.i(TAG, "Chat img ip: " +friendsIp);

        pushMessage(imgMsg);
        chatClient.write(imgMsg);
        gettingImage = false;


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
        if(!gettingImage){
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //chatThread.stop();

    }
}