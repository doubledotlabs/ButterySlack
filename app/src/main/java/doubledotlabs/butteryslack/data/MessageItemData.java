package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.utils.SlackMovementMethod;
import doubledotlabs.butteryslack.utils.SlackUtils;

public abstract class MessageItemData<T extends ItemData.ViewHolder> extends ItemData<T> {

    @Nullable
    private SlackUser sender;
    private String timestamp;

    public MessageItemData(Context context, @Nullable SlackUser sender, String content, String timestamp) {
        super(context, new Identifier(sender != null ? sender.getUserName() : null, content));
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public MessageItemData(Context context, SlackMessagePosted event) {
        super(context, new Identifier(event.getSender() != null ? event.getSender().getUserName() : null, event.getMessageContent()));
        sender = event.getSender();
        timestamp = event.getTimestamp();
    }

    @Nullable
    public SlackUser getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public abstract T getViewHolder(LayoutInflater inflater, ViewGroup parent);

    @Override
    public void onBindViewHolder(final T holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder.subtitle != null) {
            if (!(holder.subtitle.getMovementMethod() instanceof SlackMovementMethod) && getContext() instanceof AppCompatActivity)
                holder.subtitle.setMovementMethod(new SlackMovementMethod((AppCompatActivity) getContext()));

            new Action<String>() {
                @NonNull
                @Override
                public String id() {
                    return "html";
                }

                @Nullable
                @Override
                protected String run() throws InterruptedException {
                    return SlackUtils.getHtmlFromMessage(getButterySlack(), getIdentifier().getSubtitle());
                }

                @Override
                protected void done(@Nullable String result) {
                    if (result != null && holder.subtitle != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            holder.subtitle.setText(Html.fromHtml(result, 0));
                        else holder.subtitle.setText(Html.fromHtml(result));
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onClick(View v) {

    }

    public static MessageItemData from(Context context, JSONObject object) {
        ButterySlack butterySlack = (ButterySlack) context.getApplicationContext();
        MessageItemData itemData = null;

        String subtype = (String) object.get("subtype");
        String senderId = (String) object.get("user");
        String content = (String) object.get("text");
        String timestamp = (String) object.get("ts");

        List<ItemData> attachments = new ArrayList<>();
        JSONArray array = (JSONArray) object.get("attachments");
        if (array != null) {
            for (Object attachment : array) {
                attachments.add(AttachmentData.from(context, (JSONObject) attachment));
            }
        }

        if (subtype != null) {
            switch (subtype) {
                case "bot_message":
                    itemData = new UserMessageItemData(
                            context,
                            butterySlack.session.findUserById((String) object.get("bot_id")),
                            content,
                            timestamp,
                            attachments
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
                            context,
                            content,
                            timestamp
                    );
                    break;
                case "file_share":
                    content = null;

                    JSONObject file = (JSONObject) object.get("file");
                    if (file != null)
                        attachments.add(AttachmentData.fromFile(context, file));
                    break;
                case "me_message":
                case "file_comment":
                case "file_mention":
                case "message_changed":
                case "message_deleted":
                case "message_replied":
                case "reply_broadcast":
            }
        }

        if (itemData != null)
            return itemData;
        else {
            return new UserMessageItemData(
                    context,
                    butterySlack.session.findUserById(senderId),
                    content,
                    timestamp,
                    attachments
            );
        }
    }

    public static MessageItemData from(Context context, SlackMessagePosted event) {
        MessageItemData itemData = null;
        SlackMessagePosted.MessageSubType subType = event.getMessageSubType();
        String type = subType != null ? subType.name() : null;

        List<ItemData> attachments = new ArrayList<>();
        if (event.getAttachments() != null) {
            for (SlackAttachment attachment : event.getAttachments()) {
                attachments.add(AttachmentData.from(context, attachment));
            }
        }

        if (type != null) {
            switch (type) {
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
                            context,
                            event
                    );
                    break;
                case "file_share":
                    //TODO: do some magic
                    break;
                case "me_message":
                case "file_comment":
                case "file_mention":
                case "message_changed":
                case "message_deleted":
                case "message_replied":
                case "reply_broadcast":
                case "bot_message":
            }
        }

        if (itemData != null)
            return itemData;
        else return new UserMessageItemData(context, event, attachments);
    }
}
