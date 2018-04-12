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

public class MessagesActivity extends AppCompatActivity implements BaseFragment.FragmentListener {
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
			setContentView(R.layout.activity_messages);

			toolbar = (Toolbar) findViewById(R.id.messages_toolbar);
			setSupportActionBar(toolbar);
			ActionBar actionBar = getSupportActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);

			butterySlack = (ButterySlack) getApplicationContext();
			toolbar.setTitle(butterySlack.session.getTeam().getName());


			Bundle args = new Bundle();
			args.putString(BaseMessageFragment.EXTRA_REPLY, getIntent().getStringExtra(BaseMessageFragment.EXTRA_REPLY));
			if (getIntent().hasExtra(EXTRA_CHANNEL_ID)) {
          args.putString(BaseMessageFragment.EXTRA_CHANNEL_ID, getIntent().getStringExtra(EXTRA_CHANNEL_ID));
					fragment = new ChannelMessageFragment();
			}
			else if (getIntent().hasExtra(EXTRA_INSTANT_ID)) {
          args.putString(BaseMessageFragment.EXTRA_CHANNEL_ID, getIntent().getStringExtra(EXTRA_INSTANT_ID));
					fragment = new InstantMessageFragment();
			}
			fragment.setArguments(args);

			getSupportFragmentManager().beginTransaction().add(R.id.messages_fragment, fragment).commit();
			setListeners(fragment);
	}

	private void setListeners(BaseFragment fragment) {
			fragment.setListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
					case android.R.id.home:
							finish();
							break;
			}
			return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTitleChange(String title) {
			toolbar.setTitle(title);
	}

}
