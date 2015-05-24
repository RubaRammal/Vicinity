package vicinity.vicinity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import vicinity.Controller.MainController;
import vicinity.model.DBHandler;
import vicinity.model.Globals;

/**
 * Launch activity displays the app's logo
 * for several seconds then directs the user to the Timeline
 * if she/he was already registered, if not then displays the username activity.
 */
public class LaunchActivity extends Activity {

    Context context = this;

            /*----------Overridden Methods------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        DBHandler dbH=new DBHandler(context);
        try{
            dbH.createDataBase();
            dbH.openDataBase();
        }


        catch (Exception e){
            e.printStackTrace();
        }


        int secondsDelay = 3;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {

                try {
                    /**
                     Checks if user is already registered
                     then navigate to the timeline directly,
                     if not then display NameActivity to register
                     */
                    MainController controller = new MainController(context);
                    if (controller.retrieveCurrentUsername()==null) {
                        Globals.isNewUser=true;
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

}
