package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ullink.slack.simpleslackapi.events.SlackMessageEvent;

import doubledotlabs.butteryslack.R;

public class MessageItemData extends ItemData {

    private SlackMessageEvent event;

    public MessageItemData(Context context, Identifier identifier, SlackMessageEvent event) {
        super(context, identifier);
        this.event = event;
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
