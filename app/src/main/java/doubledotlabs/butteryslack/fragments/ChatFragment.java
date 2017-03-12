package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import java.util.ArrayList;
import java.util.List;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.ItemAdapter;
import doubledotlabs.butteryslack.data.ItemData;
import doubledotlabs.butteryslack.data.MessageItemData;


public abstract class ChatFragment extends ButteryFragment implements SlackMessagePostedListener {

    private List<ItemData> messages;
    private Handler handler;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        messages = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ItemAdapter(getContext(), messages);
        recyclerView.setAdapter(adapter);

        return v;
    }

    final void registerListener() {
        getButterySlack().session.addMessagePostedListener(this);
    }

    @Override
    public void onDestroy() {
        getButterySlack().session.removeMessagePostedListener(this);
        super.onDestroy();
    }

    abstract boolean isMessageInChannel(SlackMessagePosted event);

    @Override
    public boolean shouldShowBackButton() {
        return true;
    }

    final void addMessages(List<ItemData> messages) {
        this.messages.addAll(messages);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEvent(final SlackMessagePosted event, SlackSession session) {
        if (isMessageInChannel(event)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    messages.add(0, new MessageItemData(getContext(), new ItemData.Identifier(event.getMessageContent(), event.getTimestamp()), event));
                    adapter.notifyItemInserted(0);
                }
            });
        }
    }
}
