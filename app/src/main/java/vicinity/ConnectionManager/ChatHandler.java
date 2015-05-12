package vicinity.ConnectionManager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by macproretina on 5/12/15.
 */
public class ChatHandler implements Handler.Callback {


    private Context context;
    private String TAG = "ChatHandler";

    public ChatHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
