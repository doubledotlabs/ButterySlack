package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.async.Action;
import com.bumptech.glide.Glide;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.utils.SlackUtils;
import doubledotlabs.butteryslack.utils.ViewUtils;

public class MessageItemData extends ItemData<MessageItemData.ViewHolder> {

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
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (sender != null) {
            if (getButterySlack().session.sessionPersona().getUserName().equals(sender.getUserName())) {
                holder.v.setBackgroundColor(Color.WHITE);
                ViewCompat.setElevation(holder.v, ViewUtils.getPixelsFromDp(2));
            }

            new Action<String>() {
                @NonNull
                @Override
                public String id() {
                    return "image" + sender.getId();
                }

                @Nullable
                @Override
                protected String run() throws InterruptedException {
                    return SlackUtils.getProfilePicture(getButterySlack(), sender.getId());
                }

                @Override
                protected void done(@Nullable String result) {
                    if (result != null) {
                        Glide.with(getContext())
                                .load(result)
                                .placeholder(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorAccent)))
                                .into(holder.imageView);
                    }
                }
            }.execute();

        }
    }

    @Override
    public void onClick(View v) {

    }

    public static class ViewHolder extends ItemData.ViewHolder {

        ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.image);
        }
    }
}
