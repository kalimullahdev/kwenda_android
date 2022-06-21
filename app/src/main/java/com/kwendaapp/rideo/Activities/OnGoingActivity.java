package com.kwendaapp.rideo.Activities;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.kwendaapp.rideo.Fragments.OnGoingTrips;
import com.kwendaapp.rideo.R;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class OnGoingActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ongoing);
        ViewPager viewPager = findViewById(R.id.viewpager);
        ImageView backArrow = findViewById(R.id.backArrow);


        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);

        String[] tabTitles = new String[]{getResources().getString(R.string.past_trips),
                getResources().getString(R.string.upcoming_trips)};

        viewPager.setAdapter(new SampleFragmentPagerAdapter(tabTitles, getSupportFragmentManager()
        ));

        viewPager.setCurrentItem(1);

        setupTabIcons();


        backArrow.setOnClickListener(view -> onBackPressed());
    }

    /**
     * Adding custom view to tab
     */
    private void setupTabIcons() {

        @SuppressLint("InflateParams") TextView tabOne = (TextView) LayoutInflater.from(OnGoingActivity.this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getResources().getString(R.string.upcoming_trips));
        Objects.requireNonNull(tabLayout.getTabAt(0)).setCustomView(tabOne);


    }


    public static class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 1;
        private final String[] tabTitles;

        public SampleFragmentPagerAdapter(String[] tabTitles, FragmentManager fm) {
            super(fm);
            this.tabTitles = tabTitles;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return new OnGoingTrips();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
