package doubledotlabs.butteryslack.activities;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.design.widget.NavigationView;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.fragments.BaseFragment;
import doubledotlabs.butteryslack.fragments.BaseMessageFragment;
import doubledotlabs.butteryslack.fragments.ChannelMessageFragment;
import doubledotlabs.butteryslack.fragments.HomeFragment;
import doubledotlabs.butteryslack.fragments.InstantMessageFragment;
import doubledotlabs.butteryslack.activities.SettingsActivity;

public class HomeActivity extends AppCompatActivity implements BaseFragment.FragmentListener {

    public static String EXTRA_CHANNEL_ID = "doubledotlabs.butteryslack.EXTRA_CHANNEL_ID";
    public static String EXTRA_INSTANT_ID = "doubledotlabs.butteryslack.EXTRA_INSTANT_ID";

    private ButterySlack butterySlack;
    private Integer selectedTokenIndex;

    private Toolbar toolbar;
    private BaseFragment fragment;
    private DrawerLayout drawer;
    private NavigationView drawerNavView;

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
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
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
            fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            if (fragment != null) {
                setListeners(fragment);
                return;
            }
        }

        if (getIntent().hasExtra(EXTRA_CHANNEL_ID)) {
            Bundle args = new Bundle();
            args.putString(BaseMessageFragment.EXTRA_CHANNEL_ID, getIntent().getStringExtra(EXTRA_CHANNEL_ID));
            args.putString(BaseMessageFragment.EXTRA_REPLY, getIntent().getStringExtra(BaseMessageFragment.EXTRA_REPLY));

            fragment = new ChannelMessageFragment();
            fragment.setArguments(args);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        } else if (getIntent().hasExtra(EXTRA_INSTANT_ID)) {
            Bundle args = new Bundle();
            args.putString(BaseMessageFragment.EXTRA_CHANNEL_ID, getIntent().getStringExtra(EXTRA_INSTANT_ID));
            args.putString(BaseMessageFragment.EXTRA_REPLY, getIntent().getStringExtra(BaseMessageFragment.EXTRA_REPLY));

            fragment = new InstantMessageFragment();
            fragment.setArguments(args);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        } else fragment = new HomeFragment();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerNavView = (NavigationView) findViewById(R.id.left_drawer);
        drawerNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_settings:
                        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
        setListeners(fragment);
    }

    private void setListeners(BaseFragment fragment) {
        fragment.setListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTitleChange(String title) {
        toolbar.setTitle(title);
    }
    
}
