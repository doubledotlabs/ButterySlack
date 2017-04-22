package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.afollestad.async.Async;
import com.afollestad.async.Done;
import com.afollestad.async.Pool;
import com.afollestad.async.Result;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.BaseItemAdapter;
import doubledotlabs.butteryslack.data.LoadingItemData;
import doubledotlabs.butteryslack.data.MessageItemData;


public abstract class BaseMessageFragment extends BaseFragment implements SlackMessagePostedListener {

    public static final String EXTRA_CHANNEL_ID = "doubledotlabs.butteryslack.EXTRA_CHANNEL_ID";
    public static final String EXTRA_REPLY = "doubledotlabs.butteryslack.EXTRA_REPLY";

    private List<BaseItemAdapter.BaseItem> messages;
    private List<BaseItemAdapter.BaseItem> oldMessages;
    private BaseItemAdapter adapter;
    private LoadingItemData loadingItem;

    private RecyclerView recyclerView;
    private EditText editText;
    private View attachFile, send;

    private Handler handler;
    private Pool pagePool, sendingPool;
    private Map<String, Boolean> pages;
    private boolean isRegistered;
    private String timestamp;

    private String reply;
    private boolean hasReplied;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        handler = new Handler(Looper.getMainLooper());

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        editText = (EditText) v.findViewById(R.id.editText);
        attachFile = v.findViewById(R.id.attachFile);
        send = v.findViewById(R.id.send);

        reply = getArguments().getString(EXTRA_REPLY);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        messages = new ArrayList<>();
        pages = new ArrayMap<>();
        loadingItem = new LoadingItemData() {
            @Override
            public void onBindViewHolder(LoadingItemData.ViewHolder holder, int position) {
                String timestamp = "0";
                if (messages.size() > 1) {
                    BaseItemAdapter.BaseItem message = messages.get(messages.indexOf(loadingItem) - 1);
                    if (message instanceof MessageItemData)
                        timestamp = ((MessageItemData) message).getTimestamp();
                }

                if (!pages.containsKey(timestamp)) {
                    pages.put(timestamp, false);

                    Action action = loadPage(timestamp);
                    if (pagePool != null && pagePool.isExecuting())
                        pagePool.push(action);
                    else pagePool = Async.series(action);
                } else Log.e("ChatFragment", "request for " + timestamp + " already sent.");
            }
        };

        messages.add(loadingItem);

        adapter = new BaseItemAdapter(getContext(), messages);
        recyclerView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = editText.getText().toString().replaceAll("\\s+", "").length();
                if (length > 0 && send.getAlpha() == 0)
                    send.animate().scaleX(1).scaleY(1).alpha(1).start();
                else if (length <= 0 && send.getAlpha() == 1)
                    send.animate().scaleX(0).scaleY(0).alpha(0).start();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return v;
    }

    final void sendMessage() {
        String message = editText.getText().toString();
        if (message.replaceAll("\\s+", "").length() > 0) {
            Action action = sendMessage(message.trim());
            if (sendingPool != null && sendingPool.isExecuting())
                sendingPool.push(action);
            else sendingPool = Async.series(action).done(new Done() {
                @Override
                public void result(@NonNull Result result) {
                    recyclerView.scrollToPosition(0);
                }
            });

            editText.setText(null);
            send.animate().scaleX(0).scaleY(0).alpha(0).start();
        }
    }

    final void registerListener() {
        if (!isRegistered) {
            getButterySlack().session.addMessagePostedListener(this);
            isRegistered = true;

            if (reply != null && !hasReplied) {
                Log.d("Reply", reply);
                if (reply != null && reply.replaceAll("\\s+", "").length() > 0) {
                    Action action = sendMessage(reply.trim());
                    if (sendingPool != null && sendingPool.isExecuting())
                        sendingPool.push(action);
                    else sendingPool = Async.series(action).done(new Done() {
                        @Override
                        public void result(@NonNull Result result) {
                            hasReplied = true;
                            recyclerView.scrollToPosition(0);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (isRegistered) {
            getButterySlack().session.removeMessagePostedListener(this);
            isRegistered = false;
        }

        super.onDestroy();
    }

    abstract boolean isMessageInChannel(SlackMessagePosted event);

    abstract Action loadPage(String timestamp);

    abstract Action sendMessage(String message);

    @Override
    public boolean shouldShowBackButton() {
        return true;
    }

    final void onPageLoaded(String timestamp, final List<BaseItemAdapter.BaseItem> messages) {
        int start = this.messages.indexOf(loadingItem);
        oldMessages = new ArrayList<>(this.messages);
        this.messages.addAll(start, messages);

        DiffUtil.calculateDiff(new DiffCallback(oldMessages, new ArrayList<>(this.messages))).dispatchUpdatesTo(adapter);
        adapter.notifyItemChanged(start - 1);

        pages.put(timestamp, true);
        if (timestamp.equals("0"))
            recyclerView.scrollToPosition(0);
    }

    @Override
    public void onEvent(final SlackMessagePosted event, SlackSession session) {
        if (isMessageInChannel(event) && !event.getTimestamp().equals(timestamp)) {
            timestamp = event.getTimestamp();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    messages.add(0, MessageItemData.from(event));
                    adapter.notifyItemInserted(0);
                }
            });
        }
    }

    private static class DiffCallback extends DiffUtil.Callback {

        private List<BaseItemAdapter.BaseItem> oldMessages;
        private List<BaseItemAdapter.BaseItem> messages;

        DiffCallback(List<BaseItemAdapter.BaseItem> oldMessages, List<BaseItemAdapter.BaseItem> messages) {
            this.oldMessages = oldMessages;
            this.messages = messages;
        }

        @Override
        public int getOldListSize() {
            return oldMessages.size();
        }

        @Override
        public int getNewListSize() {
            return messages.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldMessages.get(oldItemPosition).equals(messages.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldMessages.get(oldItemPosition).equals(messages.get(newItemPosition));
        }
    }
}
