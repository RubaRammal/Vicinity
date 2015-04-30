package vicinity.vicinity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLException;

import vicinity.Controller.MainController;
import vicinity.model.DBHandler;

/**
 * Launch activity displays the app's logo
 * for several seconds.
 */
public class LaunchActivity extends Activity {

    Context context = this;
    MainController controller = new MainController(context);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        DBHandler dbH=new DBHandler(context);
        try{
            dbH.createDataBase();}
        catch (Exception e){
            e.printStackTrace();
            }


        int secondsDelay = 3;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                try {
                    if (controller.retrieveCurrentUsername()==null) {
                        Intent intent = new Intent(context, NameActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(context, TabsActivity.class);
                        startActivity(intent);
                        finish();
                    }


                }
                catch(SQLException e){
                    e.printStackTrace();
                }
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
