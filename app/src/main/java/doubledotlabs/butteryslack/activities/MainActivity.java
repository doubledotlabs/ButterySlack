package doubledotlabs.butteryslack.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.async.Action;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;

public class MainActivity extends AppCompatActivity {

    private ButterySlack slack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        slack = (ButterySlack) getApplicationContext();

        new Action<String>() {
            @NonNull
            @Override
            public String id() {
                return "connect";
            }

            @Nullable
            @Override
            protected String run() throws InterruptedException {
                try {
                    slack.session.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
                return null;
            }

            @Override
            protected void done(@Nullable String result) {
                super.done(result);
                if (result != null) {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //TODO: start HomeActivity
                }
            }
        }.execute();
    }
}
