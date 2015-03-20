package vicinity.vicinity;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.ActionBar;
import android.support.v7.widget.Toolbar;
import java.sql.SQLException;

import vicinity.model.Message;


public class ChatActivity extends ActionBarActivity {

    ListView chatListView;
    EditText chatText;
    Button send;
    Boolean position = true;
    Context ctx = this;
    Message message;
    Boolean added;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

/*        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);

        setSupportActionBar(actionBar);*/


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);



        chatListView = (ListView) findViewById(R.id.chatList);
        final ChatAdapter adapter = new ChatAdapter(this, R.layout.chat_box_layout);
        chatText = (EditText) findViewById(R.id.chatText);
        send = (Button) findViewById(R.id.sendButton);

        chatListView.setAdapter(adapter);
        chatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatListView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                chatListView.setSelection(adapter.getCount()-1);
            }
        });

        send.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                        message = new Message(ctx, "1", position, chatText.getText().toString());
                        adapter.add(message);
                        try {
                             added = message.addMessage(message);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        position = !position;
                        chatText.setText(null);
                    }
                }
        );

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
