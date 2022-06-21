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
import com.kwendaapp.rideo.Fragments.PastTrips;
import com.kwendaapp.rideo.R;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
public class HistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);
        ViewPager viewPager = findViewById(R.id.viewpager);
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> onBackPressed());

        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setVisibility(View.GONE);
        String strTag = Objects.requireNonNull(getIntent().getExtras()).getString("tag");
        String[] tabTitles = new String[]{getResources().getString(R.string.past_trips),
                getResources().getString(R.string.upcoming_trips)};

        viewPager.setAdapter(new SampleFragmentPagerAdapter(tabTitles, getSupportFragmentManager()
        ));

        if (strTag != null) {
            if (strTag.equalsIgnoreCase("past")) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(1);
            }
        }

        setupTabIcons();
    }

    private void setupTabIcons() {
        @SuppressLint("InflateParams") TextView tabOne = (TextView) LayoutInflater.from(HistoryActivity.this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getResources().getString(R.string.past_trips));
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
            return new PastTrips();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

}
