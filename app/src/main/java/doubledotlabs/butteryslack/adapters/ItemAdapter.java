package doubledotlabs.butteryslack.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import doubledotlabs.butteryslack.data.ItemData;

public class ItemAdapter extends RecyclerView.Adapter<ItemData.ViewHolder> {

    private Context context;
    private List<ItemData> items;

    public ItemAdapter(Context context, List<ItemData> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ItemData.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0 || viewType >= items.size()) return null;
        return items.get(viewType).getViewHolder(LayoutInflater.from(context), parent);
    }

    @Override
    public void onBindViewHolder(ItemData.ViewHolder holder, int position) {
        try {
            items.get(position).onBindViewHolder(holder, position);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
