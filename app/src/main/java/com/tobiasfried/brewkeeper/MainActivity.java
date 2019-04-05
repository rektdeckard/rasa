package com.tobiasfried.brewkeeper;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tobiasfried.brewkeeper.model.Brew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTabHost;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private FragmentTabHost mTabHost;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set TabHose
        mTabHost = findViewById(R.id.fragment_tab_host);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("brews")
                .setIndicator("Brews"), CurrentFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("history")
                .setIndicator("History"), HistoryFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("recipes")
                .setIndicator("Recipes"), RecipeFragment.class, null);
        //mTabHost.addTab(mTabHost.newTabSpec("settings").setIndicator("Settings"), null, null);
        mTabHost.setCurrentTab(0);

//        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, EntryActivity.class);
//                startActivity(intent);
//            }
//        });

        ExtendedFloatingActionButton fab = findViewById(R.id.extended_fab);
        fab.setOnClickListener(v -> {
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
