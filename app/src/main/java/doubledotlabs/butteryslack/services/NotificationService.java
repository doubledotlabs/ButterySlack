package doubledotlabs.butteryslack.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import java.util.Locale;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.activities.HomeActivity;
import doubledotlabs.butteryslack.activities.MainActivity;
import doubledotlabs.butteryslack.fragments.BaseMessageFragment;
import doubledotlabs.butteryslack.utils.SlackUtils;

public class NotificationService extends Service implements ButterySlack.ConnectionListener, SlackMessagePostedListener {

    public static final String EXTRA_TOKEN_INDEX = "doubledotlabs.butteryslack.EXTRA_TOKEN_INDEX";

    private NotificationManagerCompat notificationManager;
    private ArrayMap<String, NotificationCompat.MessagingStyle> messages;

    private ButterySlack butterySlack;

    @Override
    public void onCreate() {
        super.onCreate();
        butterySlack = (ButterySlack) getApplicationContext();
        butterySlack.addListener(this);

        notificationManager = NotificationManagerCompat.from(this);
        messages = new ArrayMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            butterySlack.setTokenIndex(null, intent.getIntExtra(EXTRA_TOKEN_INDEX, butterySlack.getTokenIndex()));

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnect(@Nullable String message) {
        if (message == null) {
            butterySlack.session.addMessagePostedListener(this);
            messages.clear();
        }
    }

    @Override
    public void onEvent(SlackMessagePosted event, SlackSession session) {
        try {
            String senderName = event.getSender().getUserName();
            String channelName = SlackUtils.getChannelName(event.getChannel());
            boolean isInstant = event.getChannel().getType() == SlackChannel.SlackChannelType.INSTANT_MESSAGING;
            boolean isMe = senderName.equals(butterySlack.session.sessionPersona().getUserName());
            if ((event.getChannel().isMember() || isInstant) && (!isMe || messages.containsKey(channelName))) {
                String title = String.format(Locale.getDefault(), getString(isInstant ? R.string.title_instant_name : R.string.title_channel_name), isInstant ? senderName : channelName);

                NotificationCompat.MessagingStyle style;
                if (messages.containsKey(channelName))
                    style = messages.get(channelName);
                else {
                    style = new NotificationCompat.MessagingStyle(butterySlack.getTokenName());
                    style.setConversationTitle(title);
                    messages.put(channelName, style);
                }

                style.addMessage(event.getMessageContent(), System.currentTimeMillis(), senderName);

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(event.getChannel().isMember() ? HomeActivity.EXTRA_CHANNEL_ID : HomeActivity.EXTRA_INSTANT_ID, event.getChannel().getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent);

                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

                String replyTitle = getString(R.string.title_reply);
                NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_message_notification, replyTitle, pendingIntent)
                        .addRemoteInput(new RemoteInput.Builder(BaseMessageFragment.EXTRA_REPLY)
                                .setLabel(replyTitle)
                                .build())
                        .setAllowGeneratedReplies(true)
                        .build();

                notificationManager.notify(messages.indexOfKey(channelName), new NotificationCompat.Builder(this)
                        .setContentTitle(isInstant ? title : String.format(Locale.getDefault(), getString(R.string.title_message_notification), senderName, channelName))
                        .setContentText(event.getMessageContent())
                        .setContentIntent(pendingIntent)
                        .addAction(action)
                        .setSmallIcon(R.drawable.ic_message_notification)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(isMe ? NotificationCompat.PRIORITY_MIN : NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(isMe ? null : new long[]{1})
                        .setAutoCancel(true)
                        .setStyle(style)
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
