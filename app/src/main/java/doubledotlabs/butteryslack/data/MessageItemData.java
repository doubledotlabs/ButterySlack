package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

public abstract class MessageItemData extends ItemData<ItemData.ViewHolder> {

    @Nullable
    private SlackUser sender;
    private String content;
    private String timestamp;

    public MessageItemData(Context context, @Nullable SlackUser sender, String content, String timestamp) {
        super(context, new Identifier(sender != null ? sender.getUserName() : null, content));
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public MessageItemData(Context context, SlackMessagePosted event) {
        super(context, new Identifier(event.getSender() != null ? event.getSender().getUserName() : null, event.getMessageContent()));
        sender = event.getSender();
        content = event.getMessageContent();
        timestamp = event.getTimestamp();
    }

    @Nullable
    public SlackUser getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public abstract ItemData.ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent);

    @Override
    public void onClick(View v) {

    }
}
