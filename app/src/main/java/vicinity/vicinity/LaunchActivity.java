package vicinity.vicinity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import vicinity.model.DBHandler;

/**
 * Launch activity displays the app's logo
 * for several seconds.
 */
public class LaunchActivity extends Activity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        DBHandler dbH=new DBHandler(context);
        try{
            dbH.createDataBase();}
        catch (Exception e){
        // Log.i(TAG,"Error in database creation");
            }


        int secondsDelay = 3;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {


                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }

        }, secondsDelay * 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
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
