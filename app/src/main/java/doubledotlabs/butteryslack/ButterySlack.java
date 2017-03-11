package doubledotlabs.butteryslack;

import android.app.Application;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;


public class ButterySlack extends Application {

    public SlackSession session;

    @Override
    public void onCreate() {
        super.onCreate();
        session = SlackSessionFactory.createWebSocketSlackSession(getString(R.string.token));
    }
}
