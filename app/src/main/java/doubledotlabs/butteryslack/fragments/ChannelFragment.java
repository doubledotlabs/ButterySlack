package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

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
                return null;
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
                    setTitle(result.getName());
                    registerListener();
                }
            }
        }.execute();
    }

    @Override
    boolean isMessageInChannel(SlackMessagePosted event) {
        return event.getChannel().getId().equals(channelId);
    }
}
