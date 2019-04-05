package com.tobiasfried.brewkeeper;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    //private FragmentTabHost mTabHost;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set ViewPager and FragmentAdapter
        ViewPager viewPager = findViewById(R.id.viewpager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return new BrewsFragment();
                } else if (position == 1) {
                    return new HistoryFragment();
                } else if (position == 2) {
                    return new RecipeFragment();
                } else {
                    return new SettingsFragment();
                }
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return getString(R.string.title_brews);
                } else if (position == 1) {
                    return getString(R.string.title_history);
                } else if (position == 2) {
                    return getString(R.string.title_recipes);
                } else {
                    return getString(R.string.title_settings);
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        // Set the Adapter onto the ViewPager
        viewPager.setAdapter(adapter);

        // Set the TabLayout to the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Set FAB
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EntryActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

}
