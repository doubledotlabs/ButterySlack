package doubledotlabs.butteryslack.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;

public abstract class LoadingItemData extends BaseItemAdapter.BaseItem<LoadingItemData.ViewHolder> {

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_loading, parent, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }
}
