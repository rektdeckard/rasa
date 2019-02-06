package com.tobiasfried.brewkeeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Brew> brewList = new ArrayList<>();
        brewList.add(new Brew("Moonlight White", Stage.PRIMARY, new Date(119, 1, 10)));
        brewList.add(new Brew("Bai Liu Dan", Stage.PRIMARY, new Date(119, 1, 12)));
        brewList.add(new Brew("Ginger Lime Yerba Mate", Stage.SECONDARY, new Date(119, 1, 8)));

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(new BrewAdapter(this, brewList));

//        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_completed);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.navigation_completed:
//                        Intent completed = new Intent(MainActivity.this, CompletedActivity.class);
//                        startActivity(completed);
//                        break;
//                    case R.id.navigation_current:
//                        Intent current = new Intent(MainActivity.this, MainActivity.class);
//                        break;
//                    case R.id.navigation_recipes:
//                        Intent recipes = new Intent(MainActivity.this, RecipesActivity.class);
//                        break;
//                }
//                return true;
//            }
//        });
    }
}
