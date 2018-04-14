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
            Intent intent = new Intent(this, HomeActivity.class);

            if (getIntent().hasExtra(HomeActivity.EXTRA_CHANNEL_ID))
                intent.putExtra(HomeActivity.EXTRA_CHANNEL_ID, getIntent().getStringExtra(HomeActivity.EXTRA_CHANNEL_ID));

            if (getIntent().hasExtra(HomeActivity.EXTRA_INSTANT_ID))
                intent.putExtra(HomeActivity.EXTRA_INSTANT_ID, getIntent().getStringExtra(HomeActivity.EXTRA_INSTANT_ID));

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
