package com.tobiasfried.brewkeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tobiasfried.brewkeeper.data.AppDatabase;
import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.data.Ingredient;
import com.tobiasfried.brewkeeper.constants.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class EntryActivity extends AppCompatActivity {

    private AppDatabase mDb;

    private AutoCompleteTextView mBrewName;
    private AutoCompleteTextView mTeaName;
    private NumberPicker mTeaAmountPicker;
    private Spinner mTeaSpinner;
    private Spinner mSugarSpinner;
    private NumberPicker mSugarAmountPicker;
    private CalendarView mPrimaryCalendarView;
    private Spinner mSugarSpinnerTwo;
    private NumberPicker mSugarAmountPickerTwo;
    private CalendarView mSecondaryCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // Get AppDatabase instance
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Bind Views
        mBrewName = findViewById(R.id.brew_name_autocomplete);
        mTeaName = findViewById(R.id.tea_name_autocomplete);
        mTeaAmountPicker = findViewById(R.id.tea_amount_picker);
        mTeaSpinner = findViewById(R.id.tea_spinner);
        mSugarSpinner = findViewById(R.id.sugar_spinner);
        mSugarAmountPicker = findViewById(R.id.sugar_amount_picker);
        mPrimaryCalendarView = findViewById(R.id.start_date_calendar);
        mSugarSpinnerTwo = findViewById(R.id.sugar_spinner_2);
        mSugarAmountPickerTwo = findViewById(R.id.sugar_amount_picker_2);
        mSecondaryCalendarView = findViewById(R.id.secondary_date_calendar);

        setupSpinners();
        setupPickers();

        // TODO chipgroup

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

        // Read Fields
        String name = mBrewName.getText().toString().trim();
        Ingredient tea = new Ingredient(mTeaName.getText().toString().trim(), IngredientType.TEA,
                TeaType.get(mTeaSpinner.getSelectedItemPosition()));
        int teaAmount = mTeaAmountPicker.getValue();
        int primarySweetener = mSugarSpinner.getSelectedItemPosition();
        int primarySweetenerAmount = (mSugarAmountPicker.getValue() + 1) * 5;
        int secondarySweetener = mSugarSpinnerTwo.getSelectedItemPosition();
        int secondarySweetenerAmount = (mSugarAmountPickerTwo.getValue() + 1) * 5;

        List<Ingredient> ingredients = new ArrayList<>();
        Ingredient peach = new Ingredient("Peach", IngredientType.FLAVOR, null);
        ingredients.add(peach);
        Ingredient ginger = new Ingredient("Ginger", IngredientType.FLAVOR, null);
        ingredients.add(ginger);

        ZonedDateTime primaryStartDate = ZonedDateTime.now();
        ZonedDateTime secondaryStartDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(mPrimaryCalendarView.getDate()), ZoneId.systemDefault());
        ZonedDateTime endDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(mSecondaryCalendarView.getDate()), ZoneId.systemDefault());

        Brew brew = new Brew(name, tea, teaAmount, primarySweetener, primarySweetenerAmount,
                secondarySweetener, secondarySweetenerAmount, ingredients, primaryStartDate, secondaryStartDate, endDate, Stage.PRIMARY, true);
        brew.setTeaId(mDb.ingredientDao().insertIngredient(tea));
        brew.setIngredientOneId(mDb.ingredientDao().insertIngredient(peach));
        brew.setIngredientTwoId(mDb.ingredientDao().insertIngredient(ginger));

        mDb.brewDao().insertBrew(brew);
        finish();
    }

    private void setupPickers() {
        String[] displayedValues = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50"};

        mSugarAmountPicker.setMinValue(0);
        mSugarAmountPicker.setMaxValue(9);
        mSugarAmountPicker.setDisplayedValues(displayedValues);

        mSugarAmountPickerTwo.setMaxValue(0);
        mSugarAmountPickerTwo.setMaxValue(9);
        mSugarAmountPickerTwo.setDisplayedValues(displayedValues);
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

        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter sugarSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sugar_types, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        sugarSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSugarSpinner.setAdapter(sugarSpinnerAdapter);

        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter sugarSpinnerAdapterTwo = ArrayAdapter.createFromResource(this,
                R.array.array_sugar_types, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        sugarSpinnerAdapterTwo.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSugarSpinnerTwo.setAdapter(sugarSpinnerAdapter);

    }
}
