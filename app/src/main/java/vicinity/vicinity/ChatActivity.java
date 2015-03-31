package vicinity.vicinity;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;

import vicinity.ConnectionManager.ChatManager;
import vicinity.model.Constants;
import vicinity.model.VicinityMessage;


public class ChatActivity extends ActionBarActivity {


    private ListView chatListView;
    private EditText chatText;
    private Button send;
    private Boolean position;
    private VicinityMessage vicinityMessage;
    private static ChatManager chatManager;
    private static ChatAdapter adapter;
    private static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Initializations
        ctx = this;
        chatListView = (ListView) findViewById(R.id.chatList);
        adapter = new ChatAdapter(ctx, R.layout.chat_box_layout);
        chatText = (EditText) findViewById(R.id.chatText);
        send = (Button) findViewById(R.id.sendButton);

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
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d(TAG, readMessage);
                    message = new VicinityMessage(ctx, "2", false, readMessage);
                    Log.i(TAG,"message"+message.getMessageBody());
                    pushMessage(message);
                    break;

                case Constants.MY_HANDLE:
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


}
