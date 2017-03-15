package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import doubledotlabs.butteryslack.R;

public class AnnouncementItemData extends MessageItemData {

    public AnnouncementItemData(Context context, String content, String timestamp) {
        super(context, null, content, timestamp);
    }

    public AnnouncementItemData(Context context, SlackMessagePosted event) {
        super(context, event);
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_announcement, parent, false));
    }
}
