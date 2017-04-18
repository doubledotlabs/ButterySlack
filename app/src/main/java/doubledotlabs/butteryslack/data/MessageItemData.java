package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;
import doubledotlabs.butteryslack.utils.SlackMovementMethod;
import doubledotlabs.butteryslack.utils.SlackUtils;

public abstract class MessageItemData<T extends MessageItemData.ViewHolder> extends BaseItemAdapter.BaseItem<T> {

    @Nullable
    private SlackUser sender;
    private String content;
    private String timestamp;

    public MessageItemData(@Nullable SlackUser sender, String content, String timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public MessageItemData(SlackMessagePosted event) {
        sender = event.getSender();
        content = event.getMessageContent();
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
        if (holder.title != null) {
            if (sender != null)
                holder.title.setText(sender.getUserName());
            else holder.title.setVisibility(View.GONE);
        }

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
                    return SlackUtils.getHtmlMessage(getButterySlack(), content);
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
        } else holder.subtitle.setVisibility(View.GONE);
    }

    public static MessageItemData from(Context context, JSONObject object) {
        ButterySlack butterySlack = (ButterySlack) context.getApplicationContext();
        MessageItemData itemData = null;

        String subtype = (String) object.get("subtype");
        String senderId = (String) object.get("user");
        String content = (String) object.get("text");
        String timestamp = (String) object.get("ts");

        List<AttachmentData> attachments = new ArrayList<>();
        JSONArray array = (JSONArray) object.get("attachments");
        if (array != null) {
            for (Object attachment : array) {
                attachments.add(new AttachmentData(AttachmentData.TYPE_ATTACHMENT, (JSONObject) attachment));
            }
        }

        if (subtype != null) {
            switch (subtype) {
                case "bot_message":
                    itemData = new UserMessageItemData(
                            butterySlack.session.findUserById(senderId != null ? senderId : (String) object.get("bot_id")),
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
                            content,
                            timestamp
                    );
                    break;
                case "file_share":
                    content = null;

                    JSONObject file = (JSONObject) object.get("file");
                    if (file != null)
                        attachments.add(new AttachmentData(AttachmentData.TYPE_FILE, file));
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
                    butterySlack.session.findUserById(senderId),
                    content,
                    timestamp,
                    attachments
            );
        }
    }

    public static MessageItemData from(SlackMessagePosted event) {
        MessageItemData itemData = null;
        SlackMessagePosted.MessageSubType subType = event.getMessageSubType();
        String type = subType != null ? subType.name() : null;

        SlackUser sender = event.getSender();
        String content = event.getMessageContent();
        String timestamp = event.getTimestamp();

        List<AttachmentData> attachments = new ArrayList<>();
        if (event.getAttachments() != null) {
            for (SlackAttachment attachment : event.getAttachments()) {
                attachments.add(new AttachmentData(attachment));
            }
        }

        if (type != null) {
            switch (type) {
                case "bot_message":
                    itemData = new UserMessageItemData(
                            sender,
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
                    itemData = new AnnouncementItemData(event);
                    break;
                case "file_share":
                    content = null;

                    if (event.getSlackFile() != null)
                        attachments.add(new AttachmentData(event.getSlackFile()));
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
                    sender,
                    content,
                    timestamp,
                    attachments
            );
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}
