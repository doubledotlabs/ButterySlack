package doubledotlabs.butteryslack.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AlphabetItemAdapter<T extends RecyclerView.ViewHolder> extends BaseItemAdapter<T> {

    private Map<Integer, Character> firstLetters;

    public AlphabetItemAdapter(Context context, List<BaseItem<T>> baseItems) {
        super(context, baseItems);
    }

    @Override
    public void setItems(List<BaseItem<T>> items) {
        Collections.sort(items, new Comparator<BaseItem<T>>() {
            @Override
            public int compare(BaseItem<T> o1, BaseItem<T> o2) {
                return ((AlphabetItem) o1).getLetter().compareTo(((AlphabetItem) o2).getLetter());
            }
        });

        firstLetters = new ArrayMap<>();
        Character previousLetter = null;
        for (int i = 0; i < items.size(); i++) {
            AlphabetItem item = (AlphabetItem) items.get(i);
            Character letter = item.getLetter();

            if (previousLetter == null || !previousLetter.equals(letter)) {
                firstLetters.put(i, letter);
                previousLetter = letter;
            }
        }

        super.setItems(items);
    }

    public boolean isFirst(AlphabetItem item) {
        return firstLetters.containsKey(getItems().indexOf(item));
    }

    public abstract static class AlphabetItem<T extends RecyclerView.ViewHolder> extends BaseItemAdapter.BaseItem<T> {

        @Nullable
        public final boolean isFirst() {
            return getAdapter() != null && ((AlphabetItemAdapter) getAdapter()).isFirst(this);
        }

        @NonNull
        public abstract Character getLetter();
    }

}
