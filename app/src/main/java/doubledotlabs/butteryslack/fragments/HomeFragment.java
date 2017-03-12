package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.ItemAdapter;
import doubledotlabs.butteryslack.data.ChannelItemData;
import doubledotlabs.butteryslack.data.ItemData;

public class HomeFragment extends ButteryFragment {

    private List<ItemData> channels;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        setTitle(getString(R.string.app_name));

        channels = new ArrayList<>();

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ItemAdapter(getContext(), channels);
        recyclerView.setAdapter(adapter);

        new Action<Collection<SlackChannel>>() {
            @NonNull
            @Override
            public String id() {
                return "channels";
            }

            @Nullable
            @Override
            protected Collection<SlackChannel> run() throws InterruptedException {
                return getButterySlack().session.getChannels();
            }

            @Override
            protected void done(@Nullable Collection<SlackChannel> result) {
                if (result != null) {
                    for (SlackChannel channel : result) {
                        channels.add(new ChannelItemData(getContext(), channel));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }.execute();

        return v;
    }
}
