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

public class HomeFragment extends BaseFragment {

    private ViewPager viewPager;
    private BottomNavigationView navigation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        setTitle(getButterySlack().getTokenName());

        viewPager = (ViewPager) v.findViewById(R.id.viewPager);

        viewPager.setAdapter(new BasePagerAdapter(getChildFragmentManager(), new ChannelsFragment()));

        return v;
    }
}
