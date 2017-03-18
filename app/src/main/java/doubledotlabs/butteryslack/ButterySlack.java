package doubledotlabs.butteryslack;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.async.Action;
import com.afollestad.async.Async;
import com.afollestad.async.Done;
import com.afollestad.async.Result;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import doubledotlabs.butteryslack.data.EmojiData;


public class ButterySlack extends Application {

    private static final String EMOJI_URL = "https://raw.githubusercontent.com/iamcal/emoji-data/master/emoji.json";

    public SlackSession session;
    private ConnectionListener connectionListener;

    @Nullable
    private List<EmojiData> emojis;

    @Override
    public void onCreate() {
        super.onCreate();

        session = SlackSessionFactory.createWebSocketSlackSession(getString(R.string.token));

        Async.parallel(
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
                },
                new Action<List<EmojiData>>() {
                    @NonNull
                    @Override
                    public String id() {
                        return "emoji";
                    }

                    @Nullable
                    @Override
                    protected List<EmojiData> run() throws InterruptedException {
                        List<EmojiData> emojis = new ArrayList<>();

                        try {
                            String json = "";
                            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(EMOJI_URL).openStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                json += line;
                            }
                            reader.close();

                            JSONArray array = (JSONArray) new JSONParser().parse(json);
                            for (Object object : array) {
                                emojis.add(EmojiData.from((JSONObject) object));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return emojis;
                    }

                    @Override
                    protected void done(@Nullable List<EmojiData> result) {
                        emojis = result;
                    }
                }
        ).done(new Done() {
            @Override
            public void result(@NonNull Result result) {
                if (connectionListener != null) {
                    Action<String> connect = (Action<String>) result.get("connect");
                    if (connect != null && connectionListener != null)
                        connectionListener.onConnect(connect.getResult());
                }
            }
        });
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

    @Nullable
    public List<EmojiData> getEmojis() {
        return emojis;
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
