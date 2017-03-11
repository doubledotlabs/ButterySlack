package doubledotlabs.butteryslack.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;

public class HomeActivity extends AppCompatActivity {

    private ButterySlack butterySlack;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        butterySlack = (ButterySlack) getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(butterySlack.session.getTeam().getName());
        setSupportActionBar(toolbar);
    }
}
