package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import doubledotlabs.butteryslack.R;

public class MessageItemData extends ItemData {

    private String senderId, senderName;
    private String content;
    private String timestamp;

    public MessageItemData(Context context, String senderId, String senderName, String content, String timestamp) {
        super(context, new Identifier(senderName, content));
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    public MessageItemData(Context context, SlackMessagePosted event) {
        super(context, new Identifier(event.getSender().getUserName(), event.getMessageContent()));
        senderId = event.getSender().getId();
        senderName = event.getSender().getRealName();
        content = event.getMessageContent();
        timestamp = event.getTimestamp();
    }

    public String getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_text, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onClick(View v) {

    }
}
