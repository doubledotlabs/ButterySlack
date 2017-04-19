package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import doubledotlabs.butteryslack.R;
import doubledotlabs.butteryslack.adapters.BasePagerAdapter;

public class HomeFragment extends BaseFragment implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private BottomNavigationView navigation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setTitle(getString(R.string.app_name));

        viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        navigation = (BottomNavigationView) v.findViewById(R.id.navigation);

        viewPager.setAdapter(new BasePagerAdapter(getChildFragmentManager(), new ChannelsFragment(), new InstantsFragment()));

        navigation.setOnNavigationItemSelectedListener(this);
        viewPager.addOnPageChangeListener(this);

        return v;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_channels:
                if (viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(0);
                break;
            case R.id.action_instant_messages:
                if (viewPager.getCurrentItem() != 1)
                    viewPager.setCurrentItem(1);
                break;
        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int itemId = position == 0 ? R.id.action_channels : R.id.action_instant_messages;
        if (navigation.getSelectedItemId() != itemId)
            navigation.setSelectedItemId(itemId);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
