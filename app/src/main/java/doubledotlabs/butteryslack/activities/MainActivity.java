package doubledotlabs.butteryslack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.fragments.BaseMessageFragment;
import doubledotlabs.butteryslack.activities.MessagesActivity;

public class MainActivity extends AppCompatActivity implements ButterySlack.ConnectionListener {

    private ButterySlack slack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slack = (ButterySlack) getApplicationContext();
        slack.addListener(this);
    }

    @Override
    protected void onDestroy() {
        slack.removeListener(this);
        super.onDestroy();
    }

    @Override
    public void onConnect(@Nullable String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Intent intent;

            //Was the application started to open a particular chat?
            if (getIntent().hasExtra(MessagesActivity.EXTRA_CHANNEL_ID)) {
						    intent = new Intent(this, MessagesActivity.class);
                intent.putExtra(MessagesActivity.EXTRA_CHANNEL_ID, getIntent().getStringExtra(MessagesActivity.EXTRA_CHANNEL_ID));
						}
            else if (getIntent().hasExtra(MessagesActivity.EXTRA_INSTANT_ID)) {
						    intent = new Intent(this, MessagesActivity.class);
                intent.putExtra(MessagesActivity.EXTRA_INSTANT_ID, getIntent().getStringExtra(MessagesActivity.EXTRA_INSTANT_ID));
						}	else {
							intent = new Intent(this, HomeActivity.class);
						}

            //For replying via a notification
            Bundle remoteInput = RemoteInput.getResultsFromIntent(getIntent());
            if (remoteInput != null) {
                intent.putExtra(BaseMessageFragment.EXTRA_REPLY, remoteInput.getCharSequence(BaseMessageFragment.EXTRA_REPLY, "").toString());
                Log.d("RemoteInput", remoteInput.getCharSequence(BaseMessageFragment.EXTRA_REPLY, "").toString());
            }

            startActivity(intent);
        }

        finish();
    }
}
