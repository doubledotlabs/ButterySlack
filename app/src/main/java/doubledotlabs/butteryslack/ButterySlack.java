package doubledotlabs.butteryslack;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import doubledotlabs.butteryslack.activities.MainActivity;
import doubledotlabs.butteryslack.data.EmojiData;


public class ButterySlack extends Application {

    private static final String PREF_TOKEN = "token";
    private static final String PREF_TOKEN_NAME = "token%1$s";
    private static final String EMOJI_URL = "https://raw.githubusercontent.com/iamcal/emoji-data/master/emoji.json";

    private SharedPreferences prefs;
    private String[] tokens;
    private int tokenIndex;

    public SlackSession session;
    private ConnectionListener connectionListener;

    @Nullable
    private List<EmojiData> emojis;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        tokens = getResources().getStringArray(R.array.tokens);
        tokenIndex = prefs.getInt(PREF_TOKEN, 0);
        connect();
    }

    private void connect() {
        session = SlackSessionFactory.createWebSocketSlackSession(tokens[tokenIndex]);

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
    }

    public void setTokenIndex(Activity activity, int index) {
        if (index != tokenIndex) {
            tokenIndex = index;
            prefs.edit().putInt(PREF_TOKEN, index).apply();
            if (session != null) {
                try {
                    session.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            activity.finish();
            activity.startActivity(new Intent(this, MainActivity.class));
            connect();
        }
    }

    public String getToken() {
        return tokens[tokenIndex];
    }

    public int getTokenIndex() {
        return tokenIndex;
    }

    public String getTokenName(int index) {
        return prefs.getString(String.format(Locale.getDefault(), PREF_TOKEN_NAME, String.valueOf(index)), tokens[index]);
    }

    public List<String> getTokenNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < getTokensLength(); i++) {
            names.add(getTokenName(i));
        }

        return names;
    }

    public int getTokensLength() {
        return tokens.length;
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
