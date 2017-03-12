package doubledotlabs.butteryslack.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class LoadingItemData extends ItemData {

    public LoadingItemData(Context context) {
        super(context, new Identifier(null));
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(new View(inflater.getContext()));
    }

    @Override
    public void onClick(View v) {
    }
}
