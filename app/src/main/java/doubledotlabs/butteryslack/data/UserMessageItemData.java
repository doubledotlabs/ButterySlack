package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.async.Action;
import com.bumptech.glide.Glide;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.utils.SlackUtils;
import doubledotlabs.butteryslack.utils.ViewUtils;

public class UserMessageItemData extends MessageItemData {

    public UserMessageItemData(Context context, @Nullable SlackUser sender, String content, String timestamp) {
        super(context, sender, content, timestamp);
    }

    public UserMessageItemData(Context context, SlackMessagePosted event) {
        super(context, event);
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (getSender() != null) {
            if (getButterySlack().session.sessionPersona().getUserName().equals(getSender().getUserName())) {
                holder.v.setBackgroundColor(Color.WHITE);
                ViewCompat.setElevation(holder.v, ViewUtils.getPixelsFromDp(2));
            } else {
                holder.v.setBackgroundColor(Color.TRANSPARENT);
                ViewCompat.setElevation(holder.v, 0);
            }

            new Action<String>() {
                @NonNull
                @Override
                public String id() {
                    return "image" + getSender().getId();
                }

                @Nullable
                @Override
                protected String run() throws InterruptedException {
                    return SlackUtils.getProfilePicture(getButterySlack(), getSender().getId());
                }

                @Override
                protected void done(@Nullable String result) {
                    if (result != null) {
                        ImageView imageView = (ImageView) holder.v.findViewById(R.id.image);
                        if (imageView != null) {
                            Glide.with(getButterySlack())
                                    .load(result)
                                    .placeholder(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorAccent)))
                                    .thumbnail(0.2f)
                                    .into(imageView);
                        }
                    }
                }
            }.execute();

        }
    }
}
