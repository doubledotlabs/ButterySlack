package doubledotlabs.butteryslack.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import doubledotlabs.butteryslack.ButterySlack;

public class BaseItemAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private List<BaseItem<T>> items;
    private List<Integer> selectedItems;

    private Context context;
    private ButterySlack butterySlack;

    public BaseItemAdapter(Context context, List<BaseItem<T>> items) {
        this.context = context;
        butterySlack = (ButterySlack) context.getApplicationContext();

        selectedItems = new ArrayList<>();
        setItems(items);
    }

    public void setItems(List<BaseItem<T>> items) {
        this.items = items;
        for (BaseItem item : items) {
            item.setAdapter(this);
        }

        notifyDataSetChanged();
    }

    public List<BaseItem<T>> getItems() {
        return items;
    }

    public void setSelectedItems(List<Integer> selectedItems) {
        this.selectedItems = selectedItems;
        for (Integer item : selectedItems) {
            notifyItemChanged(item);
        }
    }

    public void setSelected(BaseItem item, boolean isSelected) {
        if (isSelected)
            selectedItems.add(items.indexOf(item));
        else selectedItems.remove(selectedItems.indexOf(items.indexOf(item)));

        notifyItemChanged(items.indexOf(item));
    }

    public boolean isSelected(BaseItem item) {
        return selectedItems.contains(items.indexOf(item));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        return items.get(viewType).getViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        BaseItem<T> item = items.get(position);
        if (item.getAdapter() == null)
            item.setAdapter(this);

        try {
            item.onBindViewHolder(holder, position);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public abstract static class BaseItem<T extends RecyclerView.ViewHolder> {

        private BaseItemAdapter<T> adapter;

        final BaseItemAdapter getAdapter() {
            return adapter;
        }

        private final void setAdapter(BaseItemAdapter<T> adapter) {
            this.adapter = adapter;
        }

        public final void setSelected(boolean isSelected) {
            if (adapter != null)
                adapter.setSelected(this, isSelected);
        }

        public final boolean isSelected() {
            return adapter != null && adapter.isSelected(this);
        }

        @Nullable
        public final Context getContext() {
            if (adapter != null)
                return adapter.context;
            else return null;
        }

        @Nullable
        public final ButterySlack getButterySlack() {
            if (adapter != null)
                return adapter.butterySlack;
            else return null;
        }

        @Nullable
        public final Integer getPosition() {
            if (adapter != null)
                return adapter.items.indexOf(this);
            else return null;
        }

        @Nullable
        public final BaseItem<T> getItem(int position) {
            if (adapter != null)
                return adapter.items.get(position);
            else return null;
        }

        public abstract T getViewHolder(LayoutInflater inflater, ViewGroup parent);

        public abstract void onBindViewHolder(T holder, int position);

    }

}
