package doubledotlabs.butteryslack.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

import java.util.Locale;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.activities.HomeActivity;
import doubledotlabs.butteryslack.activities.MainActivity;
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
        String senderName = event.getSender().getUserName();
        if ((event.getChannel().isMember() || event.getChannel().getType() == SlackChannel.SlackChannelType.INSTANT_MESSAGING) && !senderName.equals(butterySlack.session.sessionPersona().getUserName())) {
            String channelName = SlackUtils.getChannelName(event.getChannel());
            Log.d("Notification", channelName);

            NotificationCompat.MessagingStyle style;
            if (messages.containsKey(channelName))
                style = messages.get(channelName);
            else {
                style = new NotificationCompat.MessagingStyle(butterySlack.getTokenName());
                style.setConversationTitle(channelName);
                messages.put(channelName, style);
            }

            style.addMessage(event.getMessageContent(), System.currentTimeMillis(), senderName);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(event.getChannel().isMember() ? HomeActivity.EXTRA_CHANNEL_ID : HomeActivity.EXTRA_INSTANT_ID, event.getChannel().getId());

            notificationManager.notify(channelName, messages.indexOfKey(channelName), new NotificationCompat.Builder(this)
                    .setContentTitle(String.format(Locale.getDefault(), getString(R.string.title_message_notification), senderName, channelName))
                    .setContentText(event.getMessageContent())
                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                    .setSmallIcon(R.drawable.ic_message_notification)
                    .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{1})
                    .setAutoCancel(true)
                    .setStyle(style)
                    .build());
        }
    }
}
