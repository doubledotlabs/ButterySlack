package doubledotlabs.butteryslack.data;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.async.Action;
import com.bumptech.glide.Glide;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import java.util.ArrayList;
import java.util.List;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;
import doubledotlabs.butteryslack.utils.SlackUtils;
import doubledotlabs.butteryslack.utils.ViewUtils;

public class UserMessageItemData extends MessageItemData<UserMessageItemData.ViewHolder> {

    private List<AttachmentData> attachments;

    public UserMessageItemData(@Nullable SlackUser sender, String content, String timestamp) {
        super(sender, content, timestamp);
    }

    public UserMessageItemData(@Nullable SlackUser sender, String content, String timestamp, List<AttachmentData> attachments) {
        super(sender, content, timestamp);
        this.attachments = attachments;
    }

    public UserMessageItemData(SlackMessagePosted event) {
        super(event);
    }

    public UserMessageItemData(SlackMessagePosted event, List<AttachmentData> attachments) {
        super(event);
        this.attachments = attachments;
    }

    public boolean isReply() {
        Integer position = getPosition();
        if (position != null) {
            BaseItemAdapter.BaseItem item = getItem(position + 1);
            if (item != null && item instanceof MessageItemData) {
                SlackUser sender = getSender(), otherSender = ((MessageItemData) item).getSender();
                if (sender != null && otherSender != null)
                    return sender.getUserName().equals(otherSender.getUserName());
            }
        }

        return false;
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_message, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        if (getSender() != null) {
            if (getButterySlack() != null && getSender() != null && getButterySlack().session.sessionPersona().getUserName().equals(getSender().getUserName())) {
                holder.itemView.setBackgroundColor(Color.WHITE);
                ViewCompat.setElevation(holder.itemView, ViewUtils.getPixelsFromDp(2));
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                ViewCompat.setElevation(holder.itemView, 0);
            }
        }

        if (isReply()) {
            holder.title.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
        } else if (getSender() != null) {
            holder.title.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);

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
                        if (holder.imageView != null) {
                            Glide.with(getButterySlack())
                                    .load(result)
                                    .placeholder(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorAccent)))
                                    .thumbnail(0.2f)
                                    .into(holder.imageView);
                        }
                    }
                }
            }.execute();
        } else {
            holder.title.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
        }

        if (getContext() != null && attachments != null && attachments.size() > 0) {
            LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
            manager.setStackFromEnd(true);
            holder.recyclerView.setLayoutManager(manager);
            holder.recyclerView.setAdapter(new BaseItemAdapter<>(getContext(), new ArrayList<BaseItemAdapter.BaseItem<AttachmentData.ViewHolder>>(attachments)));
        } else {
            holder.recyclerView.setAdapter(null);
            holder.recyclerView.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends MessageItemData.ViewHolder {

        ImageView imageView;
        RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }
}
