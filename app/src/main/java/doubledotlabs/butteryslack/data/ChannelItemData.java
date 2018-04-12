package doubledotlabs.butteryslack.data;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class ChannelItemData extends BaseItemAdapter.BaseItem<ChannelItemData.ViewHolder> implements View.OnClickListener {

    private SlackChannel channel;
    private String subtitle;

    public ChannelItemData(SlackChannel channel) {
        this.channel = channel;
        subtitle = SlackUtils.getChannelTopic(channel);
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_channel, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (channel.getType() == SlackChannel.SlackChannelType.INSTANT_MESSAGING) {
            holder.prefix.setText("@");

            List<SlackUser> members = new ArrayList<>(channel.getMembers());
            if (members.size() > 0) {
                SlackUser member = members.get(0);
                holder.title.setText(member.getUserName());
                holder.subtitle.setText(member.getRealName());
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

        TextView title, subtitle, prefix;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            prefix = (TextView) itemView.findViewById(R.id.prefix);
        }
    }
}
