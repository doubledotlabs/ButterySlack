package doubledotlabs.butteryslack.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import doubledotlabs.butteryslack.ButterySlack;

public class ButteryFragment extends Fragment {

    private ButterySlack butterySlack;
    private FragmentListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        butterySlack = (ButterySlack) getContext().getApplicationContext();
    }

    final ButterySlack getButterySlack() {
        return butterySlack;
    }

    public final void setListener(FragmentListener listener) {
        this.listener = listener;
    }

    final void setTitle(String title) {
        listener.onTitleChange(title);
    }

    public boolean shouldShowBackButton() {
        return false;
    }

    public interface FragmentListener {
        void onTitleChange(String title);
    }
}
