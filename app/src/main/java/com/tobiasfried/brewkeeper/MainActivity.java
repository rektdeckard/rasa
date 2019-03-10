package com.tobiasfried.brewkeeper;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements CurrentFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new CurrentFragment()).commit();

        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_current);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int selected = menuItem.getItemId();
                if (!(selected == bottomNavigationView.getSelectedItemId())) {
                    switch (selected) {
                        // TODO add Recipes and Completed Brews fragments
                        case R.id.navigation_completed:
                            //getSupportFragmentManager().beginTransaction().replace(R.id.container, new CompletedFragment());
                            break;
                        case R.id.navigation_current:
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, new CurrentFragment());
                            break;
                        case R.id.navigation_recipes:
                            //getSupportFragmentManager().beginTransaction().replace(R.id.container, new RecipesFragment());
                            break;
                    }
                }
                return true;
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EntryActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selected = item.getItemId();
        switch (selected) {
            case R.id.action_delete_all:
                // TODO delete
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
