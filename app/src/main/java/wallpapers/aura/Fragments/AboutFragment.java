package wallpapers.aura.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wallpapers.aura.R;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class AboutFragment extends Fragment {

    public static AboutFragment newInstance(String json, Boolean category) {
        AboutFragment oneFragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString("JsonLink", json);
        args.putBoolean("isCategory", category);
        oneFragment.setArguments(args);
        return oneFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    class AboutViewPager extends FragmentPagerAdapter{
        public AboutViewPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

}
