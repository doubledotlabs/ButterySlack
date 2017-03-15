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
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

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
}
