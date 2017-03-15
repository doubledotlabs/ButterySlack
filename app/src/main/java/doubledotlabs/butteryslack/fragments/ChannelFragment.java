package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import doubledotlabs.butteryslack.data.AnnouncementItemData;
import doubledotlabs.butteryslack.data.ItemData;
import doubledotlabs.butteryslack.data.MessageItemData;
import doubledotlabs.butteryslack.data.UserMessageItemData;

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
                    MessageItemData itemData = null;

                    String subtype = (String) message.get("subtype");
                    String content = (String) message.get("text");
                    String timestamp = (String) message.get("ts");
                    if (subtype != null) {
                        switch (subtype) {
                            case "bot_message":
                                itemData = new UserMessageItemData(
                                        getContext(),
                                        getButterySlack().session.findUserById((String) message.get("bot_id")),
                                        content,
                                        timestamp
                                );
                                break;
                            case "channel_archive":
                            case "channel_join":
                            case "channel_leave":
                            case "channel_name":
                            case "channel_purpose":
                            case "channel_topic":
                            case "channel_unarchive":
                            case "group_archive":
                            case "group_join":
                            case "group_leave":
                            case "group_name":
                            case "group_purpose":
                            case "group_topic":
                            case "group_unarchive":
                            case "pinned_item":
                            case "unpinned_item":
                                itemData = new AnnouncementItemData(
                                        getContext(),
                                        content,
                                        timestamp
                                );
                                break;
                            case "me_message":
                            case "file_comment":
                            case "file_mention":
                            case "file_share":
                            case "message_changed":
                            case "message_deleted":
                            case "message_replied":
                            case "reply_broadcast":
                        }
                    }

                    if (itemData != null)
                        messages.add(itemData);
                    else {
                        messages.add(new UserMessageItemData(
                                getContext(),
                                getButterySlack().session.findUserById((String) message.get("user")),
                                content,
                                timestamp
                        ));
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
