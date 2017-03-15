package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import doubledotlabs.butteryslack.R;

public abstract class LoadingItemData extends ItemData<LoadingItemData.ViewHolder> {

    public LoadingItemData(Context context) {
        super(context, new Identifier(null));
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_loading, parent, false));
    }

    @Override
    public void onClick(View v) {
    }

    public class ViewHolder extends ItemData.ViewHolder {

        ProgressBar progressBar;

        public ViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}
