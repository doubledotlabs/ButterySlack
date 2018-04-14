package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.afollestad.async.Action;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackUser;

import java.util.ArrayList;
import java.util.List;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;
import doubledotlabs.butteryslack.fragments.BaseMessageFragment;
import doubledotlabs.butteryslack.fragments.ChannelMessageFragment;
import doubledotlabs.butteryslack.fragments.InstantMessageFragment;
import doubledotlabs.butteryslack.activities.MessagesActivity;
import doubledotlabs.butteryslack.utils.SlackUtils;
import doubledotlabs.butteryslack.utils.ViewUtils;

public class ChannelItemData extends BaseItemAdapter.BaseItem<ChannelItemData.ViewHolder> implements View.OnClickListener {

    private SlackChannel channel;
    private String subtitle;
    private String senderImage;
		private SlackUser owner;

    public ChannelItemData(SlackChannel channel) {
        this.channel = channel;
        subtitle = SlackUtils.getChannelTopic(channel);
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_channel, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (channel.getType() == SlackChannel.SlackChannelType.INSTANT_MESSAGING) {
            holder.prefix.setText("");

            List<SlackUser> members = new ArrayList<>(channel.getMembers());
            if (members.size() > 0) {
								owner = members.get(0);
                holder.title.setText(owner.getUserName());
                holder.subtitle.setText(owner.getRealName());
								if (senderImage != null) {
										Glide.with(getButterySlack())
														.load(senderImage)
														.apply(new RequestOptions()
																.placeholder(new ColorDrawable(ContextCompat.getColor(holder.imageView.getContext(), R.color.colorAccent))))
														.thumbnail(0.2f)
														.into(holder.imageView);
								} else {
										new Action<String>() {
												@NonNull
												@Override
												public String id() {
														return "image" + owner.getId();
												}

												@Nullable
												@Override
												protected String run() throws InterruptedException {
														return SlackUtils.getProfilePicture(getButterySlack(), owner.getId());
												}

												@Override
												protected void done(@Nullable String result) {
														if (result != null) {
																Context context = getContext();
																senderImage = result;
																if (context != null && holder.imageView != null) {
																		Glide.with(getButterySlack())
																						.load(senderImage)
																						.apply(new RequestOptions()
																								.placeholder(new ColorDrawable(ContextCompat.getColor(context, R.color.colorAccent))))
																						.thumbnail(0.2f)
																						.into(holder.imageView);
																}
														}
												}
										}.execute();
								}
            }
        } else {
            holder.prefix.setText("#");
            holder.title.setText(channel.getName());
            holder.subtitle.setText(subtitle);
        }

        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), MessagesActivity.class);

				if (channel.getType() == SlackChannel.SlackChannelType.INSTANT_MESSAGING) {
          intent.putExtra(MessagesActivity.EXTRA_INSTANT_ID, channel.getId());
				} else {
	        intent.putExtra(MessagesActivity.EXTRA_CHANNEL_ID, channel.getId());
				}
      v.getContext().startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        TextView title, subtitle, prefix;

        public ViewHolder(View itemView) {
            super(itemView);
						imageView = (ImageView) itemView.findViewById(R.id.channelImage);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            prefix = (TextView) itemView.findViewById(R.id.prefix);
        }
    }
}
