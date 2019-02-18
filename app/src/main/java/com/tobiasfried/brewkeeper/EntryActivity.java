package com.tobiasfried.brewkeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import com.tobiasfried.brewkeeper.data.BrewContract.BasicEntry;
import com.tobiasfried.brewkeeper.data.BrewContract.IngredientEntry;


public class EntryActivity extends AppCompatActivity {

    AutoCompleteTextView mBrewName;
    AutoCompleteTextView mTeaName;
    Spinner mTeaSpinner;
    Spinner mSugarSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        mBrewName = findViewById(R.id.brew_name_autocomplete);
        mTeaName = findViewById(R.id.tea_name_autocomplete);

        mTeaSpinner = findViewById(R.id.tea_spinner);
        mSugarSpinner = findViewById(R.id.sugar_spinner);

        setupSpinners();

        // TODO chip clicks
        ChipGroup chipGroup = findViewById(R.id.ingredient_chip_group);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Chip chip = findViewById(chipGroup.getCheckedChipId());
                chip.setChecked(true);
            }

        });

    }

    private void setupSpinners() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter teaSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_tea_types, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        teaSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTeaSpinner.setAdapter(teaSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTeaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {

                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter sugarSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sugar_types, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        sugarSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSugarSpinner.setAdapter(sugarSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSugarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {

                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBrew();
                finish();
                return true;
            case R.id.action_delete:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveBrew() {
        // Read values from fields
        String brewName = mBrewName.getText().toString().trim();
        String teaName = mTeaName.getText().toString().trim();

        // Create ContentValues
        ContentValues values = new ContentValues();
        values.put(BasicEntry.COLUMN_NAME, brewName);
        values.put(BasicEntry.COLUMN_TEA_NAME, 11);
        values.put(BasicEntry.COLUMN_TEA_TYPE, 2);
        values.put(BasicEntry.COLUMN_TEA_AMOUNT, 100);
        values.put(BasicEntry.COLUMN_PRIMARY_SUGAR_TYPE, 12); // sugar
        values.put(BasicEntry.COLUMN_PRIMARY_SUGAR_AMOUNT, 50);
        values.put(BasicEntry.COLUMN_PRIMARY_TIME, 1209600); // 14 days
        values.put(BasicEntry.COLUMN_SECONDARY_TIME, 259200); // 3 days
        values.put(BasicEntry.COLUMN_INGREDIENT_1, 12); // ginger
        values.put(BasicEntry.COLUMN_INGREDIENT_2, 13); // lemon

        // Make sure ingredients and teas exist
        ContentValues tea = new ContentValues();
        tea.put(BasicEntry.COLUMN_NAME, "Drunken Concubine");
        tea.put(IngredientEntry.COLUMN_INGREDIENT_ID, 11);
        tea.put(IngredientEntry.COLUMN_TYPE, 1);
        tea.put(BasicEntry.COLUMN_TEA_TYPE, 2);
        getContentResolver().insert(IngredientEntry.CONTENT_URI, tea);

        ContentValues sugar = new ContentValues();
        sugar.put(BasicEntry.COLUMN_NAME, "Sugar");
        sugar.put(IngredientEntry.COLUMN_INGREDIENT_ID, 12);
        sugar.put(IngredientEntry.COLUMN_TYPE, 2);
        getContentResolver().insert(IngredientEntry.CONTENT_URI, sugar);

        ContentValues ginger = new ContentValues();
        ginger.put(BasicEntry.COLUMN_NAME, "Ginger");
        sugar.put(IngredientEntry.COLUMN_INGREDIENT_ID, 13);
        ginger.put(IngredientEntry.COLUMN_TYPE, 3);
        getContentResolver().insert(IngredientEntry.CONTENT_URI, ginger);

        getContentResolver().insert(BasicEntry.CONTENT_URI, values);
    }
}
