package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;

public abstract class ItemData<T extends ItemData.ViewHolder> implements View.OnClickListener {

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

    public abstract T getViewHolder(LayoutInflater inflater, ViewGroup parent);

    public void onBindViewHolder(T holder, int position) {
        if (identifier != null) {
            if (holder.title != null) {
                String text = identifier.getTitle();
                if (text.length() > 0) {
                    holder.title.setVisibility(View.VISIBLE);
                    holder.title.setText(text);
                } else holder.title.setVisibility(View.GONE);
            }
            if (holder.subtitle != null) {
                String text = identifier.getSubtitle();
                if (text.length() > 0) {
                    holder.subtitle.setVisibility(View.VISIBLE);
                    holder.subtitle.setText(text);
                } else holder.subtitle.setVisibility(View.GONE);
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
        TextView title, subtitle;

        public ViewHolder(View v) {
            super(v);
            this.v = v;
            title = (TextView) v.findViewById(R.id.title);
            subtitle = (TextView) v.findViewById(R.id.subtitle);
        }
    }
}
