package vicinity.vicinity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import vicinity.Controller.MainController;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "rubasMessage";
    EditText username_input;
    Button submit_button;
    TextView launchScreen;
    final Context context = this;
    public String username;
    public final MainController controller = new MainController(MainActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {//Lama

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        username_input = (EditText) findViewById(R.id.username);
        submit_button = (Button) findViewById(R.id.button);
        submit_button.setEnabled(false);



        username_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                username=username_input.getText().toString();
                submit_button.setEnabled(controller.nameValidation(username));//Username validation
            }
        });

        //Submit button event handler
        //when clicked, user must be navigated to timeline
        submit_button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {

                        //Controller will add the user to the database
                        if(controller.createNewUser(username_input.getText().toString()))
                        {

                            CharSequence text = "Welcome to Vicinity!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);//we can customize this Toast later -Afnan
                            toast.show();
                            Intent intent = new Intent(context, Tabs.class);
                            startActivity(intent);
                        }
                    }
                }
        );

    }//End onCreate






    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.i(TAG, "onCreateOptionsMenu");
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
            Log.i(TAG, "onOptionsItemSelected");
            return true;
        }
        Log.i(TAG, "onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    /*public void getWifiActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), WiFiActivity.class);
        startActivity(intent);
    }*/
}
