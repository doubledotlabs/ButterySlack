package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;

public abstract class ItemData implements View.OnClickListener {

    private Context context;
    private ButterySlack butterySlack;
    private Identifier identifier;

    public ItemData(Context context, Identifier identifier) {
        this.context = context;
        this.identifier = identifier;
        butterySlack = (ButterySlack) context.getApplicationContext();
    }

    public final Context getContext() {
        return context;
    }

    public final ButterySlack getButterySlack() {
        return butterySlack;
    }

    public final Identifier getIdentifier() {
        return identifier;
    }

    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_text, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (identifier != null) {
            TextView title = (TextView) holder.v.findViewById(R.id.title);
            TextView subtitle = (TextView) holder.v.findViewById(R.id.subtitle);

            if (title != null)
                title.setText(identifier.getTitle());
            if (subtitle != null) {
                String text = identifier.getSubtitle();
                if (text.length() > 0) {
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setText(text);
                } else subtitle.setVisibility(View.GONE);
            }
        }

        holder.v.setOnClickListener(this);
    }

    public static class Identifier {

        private String title, subtitle;

        public Identifier(String title) {
            this.title = title;
        }

        public Identifier(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        public String getTitle() {
            return title != null ? title : "";
        }

        public String getSubtitle() {
            return subtitle != null ? subtitle : "";
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View v;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
        }
    }
}
