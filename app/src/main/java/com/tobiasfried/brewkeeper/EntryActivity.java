package com.tobiasfried.brewkeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tobiasfried.brewkeeper.constants.IngredientType;
import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.constants.TeaType;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.model.Ingredient;
import com.tobiasfried.brewkeeper.viewmodel.EntryViewModel;
import com.tobiasfried.brewkeeper.viewmodel.EntryViewModelFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class EntryActivity extends AppCompatActivity {

    public static String EXTRA_BREW_ID = "brewID";

    private DateTimeFormatter formatter;

    private Brew currentBrew;
    private Ingredient currentTea;
    private List<Ingredient> ingredients;
    private List<Ingredient> addedIngredients;
    private List<Ingredient> selectedIngredients;
    private List<Ingredient> deletedIngredients;

    // Views
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
    private MaterialButton mSubmitButton;

    // ViewModel
    private EntryViewModel viewModel;

    // Database
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_redesign);

        // Get Date
        formatter = DateTimeFormatter.ofPattern("LLL d");

        // Bind Views
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

        // Get Database instance
        db = FirebaseFirestore.getInstance();

        String brewId = getIntent().getExtras() == null ? null : getIntent().getExtras().getString(EXTRA_BREW_ID);
        if (brewId != null) {
            // Edit mode
            EntryViewModelFactory factory = new EntryViewModelFactory(FirebaseFirestore.getInstance(), brewId);
            viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel.class);
            fetchBrew();
        } else {
            // Create mode
            currentBrew = new Brew();
            currentBrew.getRecipe().setPrimarySweetener(0);
            currentBrew.getRecipe().setPrimarySweetenerAmount(100);
            currentBrew.getRecipe().setSecondarySweetener(0);
            currentBrew.getRecipe().setPrimarySweetenerAmount(100);
            currentBrew.setStage(Stage.PRIMARY);
            currentBrew.setPrimaryStartDate(Instant.now().toEpochMilli());
            currentTea = new Ingredient(null, IngredientType.TEA, TeaType.OTHER);
            EntryViewModelFactory factory = new EntryViewModelFactory(db, null);
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

    private void setupButtons() {
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

        mPrimaryDateTextView.setText(formatter.format(LocalDateTime.now()));
        mPrimaryDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionDialog fragment = DateSelectionDialog.getInstance();
                fragment.setDate(currentBrew.getPrimaryStartDate());
                fragment.setMaxDate(currentBrew.getSecondaryStartDate());
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentBrew.setPrimaryStartDate(new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis());
//                        currentBrew.setPrimaryStartDate(Instant.now().with(ChronoField.YEAR, year)
//                                .with(ChronoField.MONTH_OF_YEAR, month + 1)
//                                .with(ChronoField.DAY_OF_MONTH, dayOfMonth).toEpochMilli());
                        refreshDates();
                    }
                });
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mSecondaryDateTextView.setText(formatter.format(LocalDateTime.now().plusDays(10)));
        mSecondaryDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionDialog fragment = DateSelectionDialog.getInstance();
                fragment.setMinDate(currentBrew.getPrimaryStartDate());
                fragment.setDate(currentBrew.getSecondaryStartDate());
                fragment.setMaxDate(currentBrew.getEndDate());
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentBrew.setSecondaryStartDate(new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis());
                        refreshDates();
                    }
                });
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        mEndDateTextView.setText(formatter.format(LocalDateTime.now().plusDays(12)));
        mEndDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionDialog fragment = DateSelectionDialog.getInstance();
                fragment.setMinDate(currentBrew.getSecondaryStartDate());
                fragment.setDate(currentBrew.getEndDate());
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentBrew.setEndDate(new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis());
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
        mBrewName.setText(currentBrew.getRecipe().getName());
        refreshDates();
        currentTea = currentBrew.getRecipe().getTea();
        mTeaName.setText(currentTea.getName());
        mTeaAmountPicker.setText(String.valueOf(currentBrew.getRecipe().getTeaAmount()));
        mTeaPicker.setText(currentTea.getTeaType().getCode());
        mSugarPicker.setText(currentBrew.getRecipe().getPrimarySweetener());
        mSugarAmountPicker.setText(currentBrew.getRecipe().getPrimarySweetenerAmount());
        mSugarPickerTwo.setText(currentBrew.getRecipe().getSecondarySweetener());
        mSugarAmountPickerTwo.setText(currentBrew.getRecipe().getSecondarySweetenerAmount());
        selectedIngredients = currentBrew.getRecipe().getIngredients();
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
            if (currentBrew.getRecipe().getIngredients().contains(i)) {
                chip.setChecked(true);
            }
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        currentBrew.getRecipe().getIngredients().add(i);
                    } else {
                        currentBrew.getRecipe().getIngredients().remove(i);
                    }
                }
            });
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentBrew.getRecipe().getIngredients().remove(i);
                    db.collection(Ingredient.COLLECTION).whereEqualTo("name", i.getName()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            db.collection(Ingredient.COLLECTION).document(document.getId()).delete();
                                        }
                                    }
                                }
                            });
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
                            db.collection(Ingredient.COLLECTION).add(newIngredient);
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
                    currentBrew.getRecipe().setPrimarySweetener(which);
                    mSugarPicker.setText(getResources().getStringArray(R.array.array_sugar_types)[which]);
                } else {
                    currentBrew.getRecipe().setSecondarySweetener(which);
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
        mPrimaryDateTextView.setText(formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(currentBrew.getPrimaryStartDate()),
                ZoneId.systemDefault())));
        mSecondaryDateTextView.setText(formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(currentBrew.getSecondaryStartDate()),
                ZoneId.systemDefault())));
        mEndDateTextView.setText(formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(currentBrew.getEndDate()),
                ZoneId.systemDefault())));
    }

    private void saveBrew() {

        // Read Fields
        currentBrew.getRecipe().setName(mBrewName.getText().toString().trim());
        currentTea.setName(mTeaName.getText().toString().trim());
        currentBrew.getRecipe().setTea(currentTea);
        currentBrew.getRecipe().setTeaAmount(Integer.parseInt(mTeaAmountPicker.getText().toString()));
        currentBrew.getRecipe().setPrimarySweetenerAmount(Integer.parseInt(mSugarAmountPicker.getText().toString()));
        currentBrew.getRecipe().setSecondarySweetenerAmount(Integer.parseInt(mSugarAmountPickerTwo.getText().toString()));
        currentBrew.getRecipe().setIngredients(selectedIngredients);

        if (Instant.now().isAfter(Instant.ofEpochMilli(currentBrew.getPrimaryStartDate())) &&
                Instant.now().isBefore(Instant.ofEpochMilli(currentBrew.getSecondaryStartDate()))) {
            currentBrew.setStage(Stage.PRIMARY);
            currentBrew.setRunning(true);
        } else if (Instant.now().isAfter(Instant.ofEpochMilli(currentBrew.getSecondaryStartDate())) &&
                Instant.now().isBefore(Instant.ofEpochMilli(currentBrew.getEndDate()))) {
            currentBrew.setStage(Stage.SECONDARY);
            currentBrew.setRunning(true);
        } else {
            currentBrew.setRunning(false);
            currentBrew.setStage(null);
        }

        db.collection(Brew.COLLECTION).add(currentBrew);
        db.collection(Ingredient.COLLECTION).add(currentBrew.getRecipe().getTea());

        finish();
    }
}
