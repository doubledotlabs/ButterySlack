package doubledotlabs.butteryslack.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;

import doubledotlabs.butteryslack.R;

public class AnnouncementItemData extends MessageItemData<AnnouncementItemData.ViewHolder> {

    public AnnouncementItemData(String content, String timestamp) {
        super(null, content, timestamp);
    }

    public AnnouncementItemData(SlackMessagePosted event) {
        super(event);
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_announcement, parent, false));
    }

    public static class ViewHolder extends MessageItemData.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }
    }
}
