package doubledotlabs.butteryslack.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.fragments.ButteryFragment;
import doubledotlabs.butteryslack.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity implements ButteryFragment.FragmentListener {

    private ButterySlack butterySlack;

    private Toolbar toolbar;
    private ButteryFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        butterySlack = (ButterySlack) getApplicationContext();
        if (butterySlack.session == null || !butterySlack.session.isConnected()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(butterySlack.session.getTeam().getName());
        setSupportActionBar(toolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                fragment = (ButteryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                setListeners(fragment);

                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.setDuration(250);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        View v = findViewById(R.id.fragment);
                        if (v != null) v.setAlpha(Math.abs((float) animation.getAnimatedValue()));
                    }
                });
                animator.start();

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null)
                    actionBar.setDisplayHomeAsUpEnabled(fragment.shouldShowBackButton());
            }
        });

        if (savedInstanceState != null) {
            fragment = (ButteryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (fragment != null) {
                setListeners(fragment);
                return;
            }
        }

        fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
        setListeners(fragment);
    }

    private void setListeners(ButteryFragment fragment) {
        fragment.setListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTitleChange(String title) {
        toolbar.setTitle(title);
    }
}
