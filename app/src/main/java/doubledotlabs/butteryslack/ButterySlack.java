package doubledotlabs.butteryslack;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;


public class ButterySlack extends Application {

    public SlackSession session;
    private ConnectionListener connectionListener;

    @Override
    public void onCreate() {
        super.onCreate();

        session = SlackSessionFactory.createWebSocketSlackSession(getString(R.string.token));
        new Action<String>() {
            @NonNull
            @Override
            public String id() {
                return "connect";
            }

            @Nullable
            @Override
            protected String run() throws InterruptedException {
                try {
                    session.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
                return null;
            }

            @Override
            protected void done(@Nullable String result) {
                super.done(result);
                if (connectionListener != null) {
                    connectionListener.onConnect(result);
                }
            }
        }.execute();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setConnectionListener(ConnectionListener listener) {
        connectionListener = listener;
        if (connectionListener != null && session.isConnected()) {
            connectionListener.onConnect(null);
        }
    }

    public interface ConnectionListener {
        void onConnect(@Nullable String message);
    }
}
