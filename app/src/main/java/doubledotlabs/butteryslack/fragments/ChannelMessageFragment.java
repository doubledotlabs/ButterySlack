package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;
import doubledotlabs.butteryslack.data.MessageItemData;

public class ChannelMessageFragment extends BaseMessageFragment {

    @Nullable
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
                    channelId = result.getId();
                    setTitle(String.format(Locale.getDefault(), getString(R.string.title_channel), result.getName()));
                    registerListener();
                } else
                    Log.e("ChannelFragment", "Channel Id: " + (channelId != null ? channelId : "null"));
            }
        }.execute();
    }

    @Override
    boolean isMessageInChannel(SlackMessagePosted event) {
        return event.getChannel().getId().equals(channelId);
    }

    @Override
    Action<List<BaseItemAdapter.BaseItem>> loadPage(final String timestamp) {
        return new Action<List<BaseItemAdapter.BaseItem>>() {
            @NonNull
            @Override
            public String id() {
                return "page" + timestamp;
            }

            @Nullable
            @Override
            protected List<BaseItemAdapter.BaseItem> run() throws InterruptedException {
                Map<String, String> params = new HashMap<>();
                params.put("channel", channelId);
                params.put("latest", timestamp);

                List<BaseItemAdapter.BaseItem> messages = new ArrayList<>();

                JSONObject json = getButterySlack().session.postGenericSlackCommand(params, "channels.history").getReply().getPlainAnswer();
                JSONArray array = (JSONArray) json.get("messages");

                if (array != null) {
                    for (int i = 0; i < array.size(); i++) {
                        MessageItemData message = MessageItemData.from(getContext(), (JSONObject) array.get(i));
                        if (message != null) messages.add(message);
                    }
                }

                return messages;
            }

            @Override
            protected void done(@Nullable List<BaseItemAdapter.BaseItem> result) {
                if (result != null) {
                    onPageLoaded(timestamp, result);
                }
            }
        };
    }

    @Override
    Action sendMessage(final String message) {
        return new Action<String>() {
            @NonNull
            @Override
            public String id() {
                return "send";
            }

            @Nullable
            @Override
            protected String run() throws InterruptedException {
                try {
                    getButterySlack().session.sendMessage(channel, message);
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }

                return null;
            }

            @Override
            protected void done(@Nullable String result) {
                if (result != null)
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            }
        };
    }

}
