package vicinity.vicinity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import vicinity.Controller.MainController;


/**
 * Registration activity
 * prompts the user to enter a username for registration
 */
public class NameActivity extends Activity {

    private static final String TAG = "Username Activity";
    EditText username_input;
    Button submit_button;
    final Context context = this;
    public String username;
    MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        controller = new MainController(NameActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

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
                username = username_input.getText().toString();
                submit_button.setEnabled(controller.nameValidation(username));//Username validation;
            }
        });

        //Submit button event handler
        //when clicked, user must be navigated to timeline and username shall be saved
        submit_button.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v) {
                            //Controller will add the user to the database
                            if(controller.createNewUser(username_input.getText().toString()))
                            {
                                CharSequence text = "Welcome to Vicinity "+username_input.getText().toString();
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                                Intent intent = new Intent(context, TabsActivity.class);
                                startActivity(intent);
                                finish();
                            }


                    }
                }
        );



    }//End onCreate

}
