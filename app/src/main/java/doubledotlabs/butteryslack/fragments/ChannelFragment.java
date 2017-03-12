package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.data.ItemData;
import doubledotlabs.butteryslack.data.MessageItemData;

public class ChannelFragment extends ChatFragment {

    public static final String EXTRA_CHANNEL_ID = "doubledotlabs.butteryslack.EXTRA_CHANNEL_ID";

    private String channelId;
    private SlackChannel channel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        channelId = getArguments().getString(EXTRA_CHANNEL_ID);

        new Action<SlackChannel>() {
            @NonNull
            @Override
            public String id() {
                return "channel";
            }

            @Nullable
            @Override
            protected SlackChannel run() throws InterruptedException {
                return getButterySlack().session.findChannelById(channelId);
            }

            @Override
            protected void done(@Nullable SlackChannel result) {
                if (result != null) {
                    channel = result;
                    setTitle(String.format(Locale.getDefault(), getString(R.string.title_channel), result.getName()));
                    registerListener();
                }
            }
        }.execute();
    }

    @Override
    boolean isMessageInChannel(SlackMessagePosted event) {
        return event.getChannel().getId().equals(channelId);
    }

    @Override
    Action<List<ItemData>> loadPage(final String timestamp) {
        return new Action<List<ItemData>>() {
            @NonNull
            @Override
            public String id() {
                return "page" + timestamp;
            }

            @Nullable
            @Override
            protected List<ItemData> run() throws InterruptedException {
                Map<String, String> params = new HashMap<>();
                params.put("channel", channelId);
                params.put("latest", timestamp);

                List<ItemData> messages = new ArrayList<>();

                JSONObject json = getButterySlack().session.postGenericSlackCommand(params, "channels.history").getReply().getPlainAnswer();
                JSONArray array = (JSONArray) json.get("messages");

                for (Object object : array) {
                    JSONObject message = (JSONObject) object;
                    switch ((String) message.get("type")) {
                        case "message":
                            messages.add(new MessageItemData(
                                    getContext(),
                                    getButterySlack().session.findUserById((String) message.get("user")),
                                    (String) message.get("text"),
                                    (String) message.get("ts")
                            ));
                            break;
                    }
                }

                return messages;
            }

            @Override
            protected void done(@Nullable List<ItemData> result) {
                if (result != null) {
                    onPageLoaded(timestamp, result);
                }
            }
        };
    }

}
