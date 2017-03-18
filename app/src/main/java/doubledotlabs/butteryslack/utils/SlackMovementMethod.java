package doubledotlabs.butteryslack.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.afollestad.async.Action;
import com.ullink.slack.simpleslackapi.SlackChannel;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.fragments.ChannelFragment;

public class SlackMovementMethod extends LinkMovementMethod {

    private AppCompatActivity activity;
    private ButterySlack butterySlack;

    private final GestureDetector gestureDetector;
    private TextView textView;
    private Spannable buffer;

    public SlackMovementMethod(AppCompatActivity activity) {
        this.activity = activity;
        butterySlack = (ButterySlack) activity.getApplicationContext();
        gestureDetector = new GestureDetector(activity, new SimpleOnGestureListener());
    }

    @Override
    public boolean onTouchEvent(TextView textView, Spannable buffer, MotionEvent event) {
        this.textView = textView;
        this.buffer = buffer;
        gestureDetector.onTouchEvent(event);
        return false;
    }

    private class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            String text = buffer.toString();
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            String linkText = getLinkText(textView, buffer, event);

            if (Patterns.PHONE.matcher(linkText).matches()) {
                //TODO: phone links
            } else if (Patterns.WEB_URL.matcher(linkText).matches()) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkText)));
            } else if (Patterns.EMAIL_ADDRESS.matcher(linkText).matches()) {
                //TODO: email links
            } else if (linkText.startsWith("@")) {
                //TODO: mention links
            } else if (linkText.startsWith("#")) {
                final String name = linkText.substring(1, linkText.length());
                new Action<String>() {
                    @NonNull
                    @Override
                    public String id() {
                        return "channel" + name;
                    }

                    @Nullable
                    @Override
                    protected String run() throws InterruptedException {
                        SlackChannel channel = butterySlack.session.findChannelByName(name);
                        if (channel != null)
                            return channel.getId();
                        else return null;
                    }

                    @Override
                    protected void done(@Nullable String result) {
                        if (result != null) {
                            Bundle args = new Bundle();
                            args.putString(ChannelFragment.EXTRA_CHANNEL_ID, result);

                            ChannelFragment fragment = new ChannelFragment();
                            fragment.setArguments(args);
                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();
                        }
                    }
                }.execute();
            }

            return false;
        }

        private String getLinkText(TextView widget, Spannable buffer, MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();
            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0)
                return buffer.subSequence(buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0])).toString();
            else return "";
        }
    }
}
