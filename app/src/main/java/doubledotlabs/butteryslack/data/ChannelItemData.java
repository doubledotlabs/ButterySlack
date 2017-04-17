package doubledotlabs.butteryslack.data;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ullink.slack.simpleslackapi.SlackChannel;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;
import doubledotlabs.butteryslack.fragments.ChannelFragment;
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
        holder.title.setText(channel.getName());
        holder.subtitle.setText(subtitle);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        args.putString(ChannelFragment.EXTRA_CHANNEL_ID, channel.getId());

        ChannelFragment fragment = new ChannelFragment();
        fragment.setArguments(args);

        if (v.getContext() != null && v.getContext() instanceof AppCompatActivity)
            ((AppCompatActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();
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
