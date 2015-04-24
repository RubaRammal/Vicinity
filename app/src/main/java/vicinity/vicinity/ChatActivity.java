package vicinity.vicinity;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    private Boolean position;
    private VicinityMessage vicinityMessage;
    private static ChatManager chatManager;
    private static ChatAdapter adapter;
    public static Context ctx;
    private static MainController controller;
    private int msgId;

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
        controller = new MainController(ctx);


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
                            vicinityMessage = new VicinityMessage(ctx,  "40-123943",5, true, chatText.getText().toString());

                            //Display Message to user
                            pushMessage(vicinityMessage);

                            //Send vicinityMessage to ChatManager
                            chatManager.write(chatText.getText().toString().getBytes());
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
                    message = new VicinityMessage(ctx, "30-123943", 5, false, readMessage);
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


}
