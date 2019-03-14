package com.tobiasfried.brewkeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.tobiasfried.brewkeeper.constants.IngredientType;
import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.constants.TeaType;
import com.tobiasfried.brewkeeper.data.AppDatabase;
import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.data.Ingredient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;


public class EntryActivity extends AppCompatActivity {

    public static String EXTRA_BREW_ID = "brewID";

    private AppDatabase mDb;
    private LocalDate primaryStartDate;
    private LocalDate secondaryStartDate;
    private LocalDate endDate;
    private DateTimeFormatter formatter;

    private Brew currentBrew;
    private Ingredient currentTea;
    private List<Ingredient> ingredients;
    private List<Ingredient> addedIngredients;
    private List<Ingredient> selectedIngredients;
    private List<Ingredient> deletedIngredients;

    private AutoCompleteTextView mBrewName;
    private TextView mPrimaryDateTextView;
    private AutoCompleteTextView mTeaName;
    private EditText mTeaAmountPicker;
    private TextView mTeaPicker;
    private TextView mSugarPicker;
    private EditText mSugarAmountPicker;
    private TextView mSecondaryDateTextView;
    private TextView mSugarPickerTwo;
    private EditText mSugarAmountPickerTwo;
    private ChipGroup mIngredientChipGroup;
    private TextView mEndDateTextView;
    private MaterialButton mCancelButton;
    private MaterialButton mSubmitButton;

    // ViewModel
    private EntryViewModelFactory factory;
    private EntryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_redesign);

        // Get AppDatabase instance
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Get Date
        primaryStartDate = LocalDate.now();
        formatter = DateTimeFormatter.ofPattern("LLLL d, yyyy");

        // Bind Views
//        mBrewName = findViewById(R.id.brew_name_autocomplete);
//        mPrimaryDateTextView = findViewById(R.id.primary_date_calendar);
//        mTeaName = findViewById(R.id.tea_name_autocomplete);
//        mTeaAmountPicker = findViewById(R.id.tea_amount_picker);
//        mTeaPicker = findViewById(R.id.tea_picker);
//        mSugarPicker = findViewById(R.id.sugar_picker);
//        mSugarAmountPicker = findViewById(R.id.sugar_amount_picker);
//        mSecondaryDateTextView = findViewById(R.id.secondary_date_calendar);
//        mSugarPickerTwo = findViewById(R.id.sugar_picker_2);
//        mSugarAmountPickerTwo = findViewById(R.id.sugar_amount_picker_2);
//        mIngredientChipGroup = findViewById(R.id.ingredient_chip_group);
//        mEndDateTextView = findViewById(R.id.end_date_calendar);
//        mCancelButton = findViewById(R.id.button_cancel);
//        mSubmitButton = findViewById(R.id.button_start);
        mBrewName = findViewById(R.id.create_edit_text);
        mPrimaryDateTextView = findViewById(R.id.primary_date_calendar);
        mTeaName = findViewById(R.id.tea_name_autocomplete);
        mTeaAmountPicker = findViewById(R.id.tea_amount_picker);
        mTeaPicker = findViewById(R.id.tea_picker);
        mSugarPicker = findViewById(R.id.primary_sugar_picker);
        mSugarAmountPicker = findViewById(R.id.sugar_amount_picker);
        mSecondaryDateTextView = findViewById(R.id.secondary_date_calendar);
        mSugarPickerTwo = findViewById(R.id.secondary_sugar_picker);
        mSugarAmountPickerTwo = findViewById(R.id.secondary_sugar_amount_picker);
        mIngredientChipGroup = findViewById(R.id.ingredient_chip_group);
        mEndDateTextView = findViewById(R.id.end_date_calendar);
        mSubmitButton = findViewById(R.id.button_start);

        // Get ViewModel instance


        // Populate fields if in edit mode
        long brewId = -1;
        if (getIntent().getExtras() != null) {
            brewId = getIntent().getExtras().getLong(EXTRA_BREW_ID);
            factory = new EntryViewModelFactory(mDb, brewId);
            viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel.class);
            fetchBrew();
        } else {
            // Create mode
            currentBrew = new Brew();
            currentBrew.setPrimarySweetener(0);
            currentBrew.setSecondarySweetener(0);
            currentBrew.setStage(Stage.PRIMARY);
            currentTea = new Ingredient(null, IngredientType.TEA, TeaType.OTHER);
            factory = new EntryViewModelFactory(mDb, brewId);
            viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel.class);
        }

        if (addedIngredients == null) {
            addedIngredients = new ArrayList<>();
        }
        if (selectedIngredients == null) {
            selectedIngredients = new ArrayList<>();
        }
        if (deletedIngredients == null) {
            deletedIngredients = new ArrayList<>();
        }

        setupButtons();
        setupDialogs();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancel:
                finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get ingredients from database
        fetchIngredients();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Push changes to database
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.ingredientDao().deleteIngredients(deletedIngredients);
                mDb.ingredientDao().insertIngredientList(addedIngredients);
            }
        });
    }

    private void setupButtons() {
//        mCancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBrew();
                finish();
            }
        });
    }

    private void setupDialogs() {
        mTeaPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeaPickerDialog(v);
            }
        });
        mSugarPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSweetenerPickerDialog(v);
            }
        });
        mSugarPickerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSweetenerPickerDialog(v);
            }
        });

        mPrimaryDateTextView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(primaryStartDate));
        mPrimaryDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionDialog fragment = DateSelectionDialog.getInstance();
                fragment.setMinDate(null);
                fragment.setDate(primaryStartDate);
                fragment.setMaxDate(secondaryStartDate);
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        primaryStartDate = LocalDate.of(year, month + 1, dayOfMonth);
                        refreshDates();
                    }
                });
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mSecondaryDateTextView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(primaryStartDate.plusDays(12)));
        mSecondaryDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionDialog fragment = DateSelectionDialog.getInstance();
                fragment.setMinDate(primaryStartDate);
                fragment.setDate(secondaryStartDate);
                fragment.setMaxDate(endDate);
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        secondaryStartDate = LocalDate.of(year, month + 1, dayOfMonth);
                        refreshDates();
                    }
                });
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mEndDateTextView.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(primaryStartDate.plusDays(15)));
        mEndDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionDialog fragment = DateSelectionDialog.getInstance();
                fragment.setMinDate(secondaryStartDate);
                fragment.setDate(endDate);
                fragment.setMaxDate(null);
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate = LocalDate.of(year, month + 1, dayOfMonth);
                        refreshDates();
                    }
                });
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
    }

    private void fetchBrew() {

        viewModel.getBrew().observe(this, new Observer<Brew>() {
            @Override
            public void onChanged(Brew brew) {
                //viewModel.getBrew().removeObserver(this);
                currentBrew = brew;
                setupBrew();
            }
        });
    }

    private void fetchIngredients() {
        viewModel.getFlavors().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(List<Ingredient> allIngredients) {
                ingredients = allIngredients;
                setupChips();
            }
        });
    }

    private void setupBrew() {
        mBrewName.setText(currentBrew.getName());
        primaryStartDate = currentBrew.getPrimaryStartDate();
        secondaryStartDate = currentBrew.getSecondaryStartDate();
        endDate = currentBrew.getEndDate();
        refreshDates();
//        mTeaName.setText(String.valueOf(currentBrew.getTeaId())); // TODO set string properly VIA QUERY
//        mTeaAmountPicker.setText(String.valueOf(currentBrew.getTeaAmount()));
//        mTeaPicker.setText(currentBrew.getTea().getTeaType().getCode()); // TODO set string properly
//        mSugarPicker.setText(currentBrew.getPrimarySweetener()); // TODO set string properly
//        mSugarAmountPicker.setText(currentBrew.getPrimarySweetenerAmount());
//        mSugarPickerTwo.setText(currentBrew.getSecondarySweetener()); // TODO set string properly
//        mSugarAmountPickerTwo.setText(currentBrew.getSecondarySweetenerAmount());
//        selectedIngredients = currentBrew.getIngredients();
        // TODO notes
    }

    private void setupChips() {
        mIngredientChipGroup.removeAllViews();
        for (final Ingredient i : ingredients) {
            final Chip chip = new Chip(this, null, R.style.CustomChipColors);
            chip.setText(i.getName().toLowerCase());
            chip.setChipBackgroundColorResource(R.color.background_color_chip_state_list);
            chip.setCloseIconTint(getColorStateList(R.color.text_color_chip_state_list));
            chip.setTextColor(getColorStateList(R.color.text_color_chip_state_list));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            if (selectedIngredients.contains(i)) {
                chip.setChecked(true);
            }
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedIngredients.add(i);
                    } else {
                        selectedIngredients.remove(i);
                    }
                }
            });
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIngredientChipGroup.removeView(chip);
                    deletedIngredients.add(i);
                    ingredients.remove(i);
                    selectedIngredients.remove(i);
                }
            });
            chip.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (chip.isCloseIconVisible()) {
                        chip.setCheckable(true);
                    } else {
                        chip.setChecked(false);
                        chip.setCheckable(false);
                    }
                    chip.setCloseIconVisible(!chip.isCloseIconVisible());
                    return true;
                }
            });
            mIngredientChipGroup.addView(chip);
        }

        Chip addChip = new Chip(getThemedContext());
        addChip.setText(R.string.new_ingredient);
        addChip.setChipIconResource(R.drawable.ic_add_black_24dp);
        addChip.setChipBackgroundColorResource(R.color.background_color_chip_state_list);
        addChip.setTextColor(getColorStateList(R.color.text_color_chip_state_list));
//        addChip.setChipIconTintResource(R.color.colorAccent);
//        addChip.setChipBackgroundColorResource(R.color.background);
//        addChip.setChipStrokeColorResource(R.color.colorAccent);
//        addChip.setTextColor(getColor(R.color.colorAccent));
        addChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog newFragment = InputDialog.getInstance();
                newFragment.setOnClickListener(new InputDialog.InputSubmitListener() {
                    @Override
                    public void onSubmitInput(DialogFragment dialog, final String input) {
                        if (!input.equals("")) {
                            Ingredient newIngredient = new Ingredient(input, IngredientType.FLAVOR, null);
                            addedIngredients.add(newIngredient);
                            ingredients.add(newIngredient);
                            setupChips();
                        }
                    }
                });

                newFragment.show(getSupportFragmentManager(), "ingredientInput");
            }
        });
        mIngredientChipGroup.addView(addChip, -1);
    }

    private void showSweetenerPickerDialog(final View v) {
        GenericPickerDialog newFragment = GenericPickerDialog.newInstance(R.array.array_sugar_types);
        newFragment.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (v == mSugarPicker) {
                    currentBrew.setPrimarySweetener(which);
                    mSugarPicker.setText(getResources().getStringArray(R.array.array_sugar_types)[which]);
                } else {
                    currentBrew.setSecondarySweetener(which);
                    mSugarPickerTwo.setText(getResources().getStringArray(R.array.array_sugar_types)[which]);
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "sweetenerPicker");
    }

    private void showTeaPickerDialog(View v) {
        GenericPickerDialog newFragment = GenericPickerDialog.newInstance(R.array.array_tea_types);
        newFragment.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentTea.setTeaType(TeaType.get(which));
                mTeaPicker.setText(getResources().getStringArray(R.array.array_tea_types)[which]);
            }
        });
        newFragment.show(getSupportFragmentManager(), "teaPicker");
    }

    private void refreshDates() {
        if (primaryStartDate != null) {
            mPrimaryDateTextView.setText(primaryStartDate.format(formatter));
        }
        if (secondaryStartDate != null) {
            mSecondaryDateTextView.setText(secondaryStartDate.format(formatter));
        }
        if (endDate != null) {
            mEndDateTextView.setText(endDate.format(formatter));
        }
    }

    private void saveBrew() {

        // Read Fields
        currentTea.setName(mTeaName.getText().toString().trim());
        currentBrew.setName(mBrewName.getText().toString().trim());
        currentBrew.setTeaAmount(Integer.parseInt(mTeaAmountPicker.getText().toString()));
        currentBrew.setPrimarySweetenerAmount(Integer.parseInt(mSugarAmountPicker.getText().toString()));
        currentBrew.setSecondarySweetenerAmount(Integer.parseInt(mSugarAmountPickerTwo.getText().toString()));
        currentBrew.setIngredients(selectedIngredients);

        currentBrew.setPrimaryStartDate(primaryStartDate);
        currentBrew.setSecondaryStartDate(secondaryStartDate);
        currentBrew.setEndDate(endDate);
        if(LocalDate.now().isAfter(primaryStartDate) && LocalDate.now().isBefore(secondaryStartDate)) {
            currentBrew.setStage(Stage.PRIMARY);
        } else if (LocalDate.now().isAfter(secondaryStartDate) && LocalDate.now().isBefore(endDate)) {
            currentBrew.setStage(Stage.SECONDARY);
        }
        currentBrew.setRunning(true);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                currentBrew.setTeaId(mDb.ingredientDao().insertIngredient(currentTea));
                mDb.brewDao().insertBrew(currentBrew);
            }
        });

        finish();

    }
}
