package doubledotlabs.butteryslack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import doubledotlabs.butteryslack.ButterySlack;

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
            startActivity(new Intent(this, HomeActivity.class));
        }
        finish();
    }
}
