package com.tobiasfried.brewkeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class EntryActivity extends AppCompatActivity {

    public static String EXTRA_BREW_ID = "brewID";

    private DateTimeFormatter formatter;

    private Brew currentBrew;
    private List<Ingredient> teas;
    private List<Ingredient> ingredients;
    private List<Ingredient> selectedIngredients;

    // Views
    private EditText brewNameEditText;
    private TextView primaryDateTextView;
    private AutoCompleteTextView teaNameEditText;
    private EditText teaAmountEditText;
    private Spinner teaSpinner;
    private Spinner primarySugarSpinner;
    private EditText primarySugarAmountEditText;
    private TextView secondaryDateTextView;
    private Spinner secondarySugarSpinner;
    private EditText secondarySugarAmountEditText;
    private ChipGroup flavorChipGroup;
    private TextView flavorAddTextView;
    private TextView endDateTextView;
    private EditText notesEditText;
    private MaterialButton submitButton;

    // ViewModel
    private EntryViewModel viewModel;

    // Database
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // Get Date
        formatter = DateTimeFormatter.ofPattern("LLL d");

        // Bind Views
        brewNameEditText = findViewById(R.id.create_edit_text);
        primaryDateTextView = findViewById(R.id.primary_date_calendar);
        teaNameEditText = findViewById(R.id.tea_name_autocomplete);
        teaAmountEditText = findViewById(R.id.tea_amount_picker);
        teaSpinner = findViewById(R.id.tea_picker);
        primarySugarSpinner = findViewById(R.id.primary_sugar_picker);
        primarySugarAmountEditText = findViewById(R.id.sugar_amount_picker);
        secondaryDateTextView = findViewById(R.id.secondary_date_calendar);
        secondarySugarSpinner = findViewById(R.id.secondary_sugar_picker);
        secondarySugarAmountEditText = findViewById(R.id.secondary_sugar_amount_picker);
        flavorChipGroup = findViewById(R.id.ingredient_chip_group);
        flavorAddTextView = findViewById(R.id.add_ingredient_edit_text);
        endDateTextView = findViewById(R.id.end_date_calendar);
        notesEditText = findViewById(R.id.notes);
        submitButton = findViewById(R.id.button_start);

        // Get Database instance
        db = FirebaseFirestore.getInstance();

        String brewId = getIntent().hasExtra(EXTRA_BREW_ID) ? getIntent().getExtras().getString(EXTRA_BREW_ID) : null;
        if (brewId != null) {
            // Edit mode
            EntryViewModelFactory factory = new EntryViewModelFactory(FirebaseFirestore.getInstance(), brewId);
            viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel.class);
            fetchBrew();
        } else {
            // Create mode
            currentBrew = new Brew();
            currentBrew.setPrimaryStartDate(Instant.now().toEpochMilli());
            teas = new ArrayList<>();
            teas.add(new Ingredient(null, IngredientType.TEA, TeaType.OTHER, 0));
            EntryViewModelFactory factory = new EntryViewModelFactory(db, null);
            viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel.class);
        }

        setupSpinners();
        setupDialogs();
        setupButtons();

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

    private void setupSpinners() {
        ArrayAdapter<CharSequence> teaTypes = ArrayAdapter.createFromResource(this, R.array.array_tea_types, R.layout.spinner_item_small);
        teaTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teaSpinner.setAdapter(teaTypes);
        teaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //currentTeas.getRecipe().getTeas().get(0).setTeaType(TeaType.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> sweetenerTypes = ArrayAdapter.createFromResource(this, R.array.array_sugar_types, R.layout.spinner_item);
        sweetenerTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        primarySugarSpinner.setAdapter(sweetenerTypes);
        secondarySugarSpinner.setAdapter(sweetenerTypes);
    }

    private void setupDialogs() {
        primaryDateTextView.setText(formatter.format(LocalDateTime.now()));
        primaryDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateSelectionDialog fragment = DateSelectionDialog.getInstance();
                fragment.setDate(currentBrew.getPrimaryStartDate());
                fragment.setMaxDate(currentBrew.getSecondaryStartDate());
                fragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        currentBrew.setPrimaryStartDate(new GregorianCalendar(year, month, dayOfMonth).getTimeInMillis());
                        refreshDates();
                    }
                });
                fragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        secondaryDateTextView.setText(formatter.format(LocalDateTime.now().plusDays(10)));
        secondaryDateTextView.setOnClickListener(new View.OnClickListener() {
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

        endDateTextView.setText(formatter.format(LocalDateTime.now().plusDays(12)));
        endDateTextView.setOnClickListener(new View.OnClickListener() {
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

    private void setupButtons() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBrew();
                finish();
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

        viewModel.getTeas().observe(this, new Observer<List<Ingredient>>() {
            @Override
            public void onChanged(List<Ingredient> allTeas) {
                teas = allTeas;
            }
        });
    }

    private void setupBrew() {
        brewNameEditText.setText(currentBrew.getRecipe().getName());
        refreshDates();
        teaNameEditText.setText(currentBrew.getRecipe().getTeas().get(0).getName());
        teaAmountEditText.setText(String.valueOf(currentBrew.getRecipe().getTeas().get(0).getAmount()));
        teaSpinner.setSelection(currentBrew.getRecipe().getTeas().get(0).getTeaType().getCode());
        primarySugarSpinner.setSelection(currentBrew.getRecipe().getPrimarySweetener());
        primarySugarAmountEditText.setText(currentBrew.getRecipe().getPrimarySweetenerAmount());
        secondarySugarSpinner.setSelection(currentBrew.getRecipe().getSecondarySweetener());
        secondarySugarAmountEditText.setText(currentBrew.getRecipe().getSecondarySweetenerAmount());
        selectedIngredients = currentBrew.getRecipe().getIngredients();

    }

    private void setupChips() {
        flavorChipGroup.removeAllViews();
        for (final Ingredient i : ingredients) {
            final Chip chip = new Chip(this, null, R.style.CustomChipColors);
            chip.setText(i.getName().toLowerCase());
            chip.setTextColor(getColorStateList(R.color.color_states_chips));
            chip.setTypeface(getResources().getFont(R.font.google_sans_medium));
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(R.color.color_states_chips);
            chip.setChipStrokeWidth(4.0f);
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
            flavorChipGroup.addView(chip);
        }

        flavorAddTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog newFragment = InputDialog.getInstance();
                newFragment.setOnClickListener(new InputDialog.InputSubmitListener() {
                    @Override
                    public void onSubmitInput(DialogFragment dialog, final String input) {
                        if (!input.equals("")) {
                            Ingredient newIngredient = new Ingredient(input, IngredientType.FLAVOR, null, 0);
                            db.collection(Ingredient.COLLECTION).add(newIngredient);
                            setupChips();
                        }
                    }
                });

                newFragment.show(getSupportFragmentManager(), "ingredientInput");
            }
        });
    }

//    private void showSweetenerPickerDialog(final View v) {
//        GenericPickerDialog newFragment = GenericPickerDialog.newInstance(R.array.array_sugar_types);
//        newFragment.setOnClickListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (v == primarySugarSpinner) {
//                    currentBrew.getRecipe().setPrimarySweetener(which);
//                    primarySugarSpinner.setText(getResources().getStringArray(R.array.array_sugar_types)[which]);
//                } else {
//                    currentBrew.getRecipe().setSecondarySweetener(which);
//                    secondarySugarSpinner.setText(getResources().getStringArray(R.array.array_sugar_types)[which]);
//                }
//            }
//        });
//        newFragment.show(getSupportFragmentManager(), "sweetenerPicker");
//    }
//
//    private void showTeaPickerDialog(View v) {
//        GenericPickerDialog newFragment = GenericPickerDialog.newInstance(R.array.array_tea_types);
//        newFragment.setOnClickListener(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                teas.setTeaType(TeaType.get(which));
//                teaSpinner.setText(getResources().getStringArray(R.array.array_tea_types)[which]);
//            }
//        });
//        newFragment.show(getSupportFragmentManager(), "teaPicker");
//    }

    private void refreshDates() {
        primaryDateTextView.setText(formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(currentBrew.getPrimaryStartDate()),
                ZoneId.systemDefault())));
        secondaryDateTextView.setText(formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(currentBrew.getSecondaryStartDate()),
                ZoneId.systemDefault())));
        endDateTextView.setText(formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(currentBrew.getEndDate()),
                ZoneId.systemDefault())));
    }

    private void saveBrew() {

        // Read Fields
        currentBrew.getRecipe().setName(brewNameEditText.getText().toString().trim());
        currentBrew.getRecipe().setTeas(teas);
        currentBrew.getRecipe().setTeas(teas);
        currentBrew.getRecipe().setPrimarySweetenerAmount(Integer.parseInt(primarySugarAmountEditText.getText().toString()));
        currentBrew.getRecipe().setSecondarySweetenerAmount(Integer.parseInt(secondarySugarAmountEditText.getText().toString()));
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

        finish();
    }
}
