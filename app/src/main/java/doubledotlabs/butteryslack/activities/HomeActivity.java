package doubledotlabs.butteryslack.activities;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import doubledotlabs.butteryslack.ButterySlack;
import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.fragments.BaseFragment;
import doubledotlabs.butteryslack.fragments.BaseMessageFragment;
import doubledotlabs.butteryslack.fragments.ChannelMessageFragment;
import doubledotlabs.butteryslack.fragments.HomeFragment;
import doubledotlabs.butteryslack.fragments.InstantMessageFragment;

public class HomeActivity extends AppCompatActivity implements BaseFragment.FragmentListener {

    public static String EXTRA_CHANNEL_ID = "doubledotlabs.butteryslack.EXTRA_CHANNEL_ID";
    public static String EXTRA_INSTANT_ID = "doubledotlabs.butteryslack.EXTRA_INSTANT_ID";

    private ButterySlack butterySlack;
    private Integer selectedTokenIndex;

    private Toolbar toolbar;
    private BaseFragment fragment;

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

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_CHANNEL_ID)) {
            Bundle args = new Bundle();
            args.putString(BaseMessageFragment.EXTRA_CHANNEL_ID, extras.getString(EXTRA_CHANNEL_ID));

            fragment = new ChannelMessageFragment();
            fragment.setArguments(args);
        } else if (extras != null && extras.containsKey(EXTRA_INSTANT_ID)) {
            Bundle args = new Bundle();
            args.putString(BaseMessageFragment.EXTRA_CHANNEL_ID, extras.getString(EXTRA_INSTANT_ID));

            fragment = new InstantMessageFragment();
            fragment.setArguments(args);
        } else fragment = new HomeFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
        setListeners(fragment);
    }

    private void setListeners(BaseFragment fragment) {
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
            case R.id.action_switch_user:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_switch_user)
                        .setSingleChoiceItems(butterySlack.getTokenNames().toArray(new String[butterySlack.getTokensLength()]), butterySlack.getTokenIndex(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedTokenIndex = which;
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedTokenIndex = null;
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                butterySlack.setTokenIndex(HomeActivity.this, selectedTokenIndex);
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTitleChange(String title) {
        toolbar.setTitle(title);
    }
}
