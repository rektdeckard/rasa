package com.tobiasfried.brewkeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tobiasfried.brewkeeper.constants.IngredientType;
import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.constants.TeaType;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.model.Ingredient;
import com.tobiasfried.brewkeeper.utils.TimeUtility;
import com.tobiasfried.brewkeeper.viewmodel.EntryViewModel;
import com.tobiasfried.brewkeeper.viewmodel.EntryViewModelFactory;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;


public class EntryActivity extends AppCompatActivity {

    private static final String LOG_TAG = EntryActivity.class.getSimpleName();
    public static String EXTRA_BREW_ID = "brewID";
    public static String EXTRA_BREW_ID_HISTORY = "brewIDHistory";

    // Database
    private FirebaseFirestore db;
    private EntryViewModel viewModel;
    private String brewId;
    private String collection = Brew.CURRENT;
    private DocumentReference docRef;

    // Model
    private Brew currentBrew;
    private List<Ingredient> teas;
    private List<Ingredient> ingredients;
    private List<Ingredient> selectedIngredients;

    // Views
    private EditText brewNameEditText;
    private TextView primaryDateTextView;
    private TextView primaryRemainingDaysTextView;
    private LinearLayout teaList;
    private AutoCompleteTextView teaNameEditText;
    private EditText teaAmountEditText;
    private Spinner teaSpinner;
    private MaterialButton teaAddButton;
    private Spinner primarySugarSpinner;
    private EditText primarySugarAmountEditText;
    private EditText waterAmountEditText;
    private TextView secondaryDateTextView;
    private TextView secondaryRemainingDaysTextView;
    private Spinner secondarySugarSpinner;
    private EditText secondarySugarAmountEditText;
    private ChipGroup flavorChipGroup;
    private MaterialButton flavorAddButton;
    private TextView endDateTextView;
    private EditText notesEditText;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // Bind Views
        brewNameEditText = findViewById(R.id.create_edit_text);
        primaryDateTextView = findViewById(R.id.primary_date_calendar);
        primaryRemainingDaysTextView = findViewById(R.id.primary_remaining_days);
        teaList = findViewById(R.id.tea);
        teaNameEditText = findViewById(R.id.tea_name_autocomplete);
        teaAmountEditText = findViewById(R.id.tea_amount_picker);
        teaSpinner = findViewById(R.id.tea_picker);
        teaAddButton = findViewById(R.id.add_tea_button);
        primarySugarSpinner = findViewById(R.id.primary_sugar_picker);
        primarySugarAmountEditText = findViewById(R.id.sugar_amount_picker);
        waterAmountEditText = findViewById(R.id.water_amount_picker);
        secondaryDateTextView = findViewById(R.id.secondary_date_calendar);
        secondaryRemainingDaysTextView = findViewById(R.id.secondary_remaining_days);
        secondarySugarSpinner = findViewById(R.id.secondary_sugar_picker);
        secondarySugarAmountEditText = findViewById(R.id.secondary_sugar_amount_picker);
        flavorChipGroup = findViewById(R.id.ingredient_chip_group);
        flavorAddButton = findViewById(R.id.add_ingredient_button);
        endDateTextView = findViewById(R.id.end_date_calendar);
        notesEditText = findViewById(R.id.notes);
        submitButton = findViewById(R.id.button_start);

        // TODO fetch from loaded brew
        selectedIngredients = new ArrayList<>();

        // Get Database instance
        db = FirebaseFirestore.getInstance();

        // Get Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("");
        toolbar.inflateMenu(R.menu.menu_entry);

        // Get ViewModel
        if (getIntent().hasExtra(EXTRA_BREW_ID)) {
            brewId = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_BREW_ID);
        } else if (getIntent().hasExtra(EXTRA_BREW_ID_HISTORY)) {
            brewId = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_BREW_ID_HISTORY);
            collection = Brew.HISTORY;
        }
        EntryViewModelFactory factory = new EntryViewModelFactory(db, collection, brewId);
        viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel.class);
        fetchBrew();

        docRef = viewModel.getDocumentReference();

        setupDialogs();
        setupFieldWatchers();
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

    private void setupTeaSpinner(Spinner spinner, int position) {
        ArrayAdapter<CharSequence> teaTypes = ArrayAdapter.createFromResource(this, R.array.array_tea_types, R.layout.spinner_item_small);
        teaTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(teaTypes);
        spinner.setSelection(position);
    }

    private void setupSugarSpinners() {
        ArrayAdapter<CharSequence> sweetenerTypes = ArrayAdapter.createFromResource(this, R.array.array_sugar_types, R.layout.spinner_item);
        sweetenerTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        primarySugarSpinner.setAdapter(sweetenerTypes);
        secondarySugarSpinner.setAdapter(sweetenerTypes);
    }

    private void setupFieldWatchers() {

        TextWatcher requiredWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = brewNameEditText.getText().toString().trim();
                String teaName = teaNameEditText.getText().toString().trim();
                String teaAmount = teaAmountEditText.getText().toString().trim();
                String primarySugarAmount = primarySugarAmountEditText.getText().toString().trim();
                String waterAmount = waterAmountEditText.getText().toString().trim();

                submitButton.setEnabled(!name.isEmpty() && !teaName.isEmpty() && !teaAmount.isEmpty() &&
                        !primarySugarAmount.isEmpty() && !waterAmount.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        brewNameEditText.addTextChangedListener(requiredWatcher);
        teaNameEditText.addTextChangedListener(requiredWatcher);
        teaAmountEditText.addTextChangedListener(requiredWatcher);
        primarySugarAmountEditText.addTextChangedListener(requiredWatcher);
        waterAmountEditText.addTextChangedListener(requiredWatcher);

    }

    private void setupDialogs() {
        // Set Default Date Strings
        if (currentBrew != null) {
            refreshDates();
        }

        // Set OnClickListeners
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

        teaAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teaList.getChildCount() < 4) {
                    LayoutInflater inflater = getLayoutInflater();
                    final View tea = inflater.inflate(R.layout.list_item_tea, null);
                    ImageButton removeButton = tea.findViewById(R.id.remove_button);
                    removeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            teaList.removeView(tea);
                        }
                    });
                    teaList.addView(tea);

                    Spinner spinner = tea.findViewById(R.id.tea_picker);
                    setupTeaSpinner(spinner, 0);
                } else {
                    teaAddButton.setError("Cannot add any more!");
                }
            }
        });

        flavorAddButton.setOnClickListener(new View.OnClickListener() {
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

    private void fetchBrew() {
        viewModel.getBrew().observe(this, new Observer<Brew>() {
            @Override
            public void onChanged(Brew brew) {
                currentBrew = brew;
                if (brewId != null) {
                    setupBrew();
                } else {
                    setupTeaSpinner(teaSpinner, 0);
                    setupSugarSpinners();
                    refreshDates();
                }
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
        refreshDates();

        brewNameEditText.setText(currentBrew.getRecipe().getName());

        // Populate Teas
        LayoutInflater inflater = getLayoutInflater();

        Ingredient firstTea = currentBrew.getRecipe().getTeas().get(0);
        teaNameEditText.setText(firstTea.getName());
        setupTeaSpinner(teaSpinner, firstTea.getTeaType().getCode());
        teaAmountEditText.setText(String.valueOf(firstTea.getAmount()));

        for (int i = 1; i < currentBrew.getRecipe().getTeas().size(); i++) {
            Ingredient tea = currentBrew.getRecipe().getTeas().get(i);

            final View teaItem = inflater.inflate(R.layout.list_item_tea, null);

            AutoCompleteTextView nameEditText = teaItem.findViewById(R.id.tea_name_autocomplete);
            nameEditText.setText(tea.getName(), TextView.BufferType.EDITABLE);

            EditText amountEditText = teaItem.findViewById(R.id.tea_amount_picker);
            amountEditText.setText(String.valueOf(tea.getAmount()), TextView.BufferType.EDITABLE);

            Spinner typeSpinner = teaItem.findViewById(R.id.tea_picker);
            setupTeaSpinner(typeSpinner, tea.getTeaType().getCode());

            ImageButton removeButton = teaItem.findViewById(R.id.remove_button);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    teaList.removeView(teaItem);
                }
            });

            teaList.addView(teaItem);
        }

        setupSugarSpinners();

        primarySugarSpinner.setSelection(currentBrew.getRecipe().getPrimarySweetener());
        primarySugarAmountEditText.setText(String.valueOf(currentBrew.getRecipe().getPrimarySweetenerAmount()), TextView.BufferType.EDITABLE);

        waterAmountEditText.setText(String.valueOf(currentBrew.getRecipe().getWater()));

        secondarySugarSpinner.setSelection(currentBrew.getRecipe().getSecondarySweetener());
        secondarySugarAmountEditText.setText(String.valueOf(currentBrew.getRecipe().getSecondarySweetenerAmount()), TextView.BufferType.EDITABLE);

        selectedIngredients.addAll(currentBrew.getRecipe().getIngredients());

        notesEditText.setText(currentBrew.getRecipe().getNotes());

    }

    private void setupChips() {
        flavorChipGroup.removeAllViews();
        for (final Ingredient i : ingredients) {
            final Chip chip = new Chip(this, null, R.style.ChipTheme);
            chip.setText(i.getName().toLowerCase());
            chip.setTextColor(getColorStateList(R.color.color_states_chips));
            chip.setTypeface(ResourcesCompat.getFont(this, R.font.google_sans_medium));
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(R.color.color_states_chips);
            chip.setChipStrokeWidth(4.0f);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            if (currentBrew != null && currentBrew.getRecipe().getIngredients().contains(i)) {
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

    }

    private void refreshDates() {
        primaryDateTextView.setText(TimeUtility.formatDateShort(currentBrew.getPrimaryStartDate()));
        int primaryDays = TimeUtility.daysBetween(currentBrew.getPrimaryStartDate(), currentBrew.getSecondaryStartDate());
        String primaryString = getResources().getQuantityString(R.plurals.pluralDays, primaryDays, primaryDays);
        primaryRemainingDaysTextView.setText(primaryString);

        secondaryDateTextView.setText(TimeUtility.formatDateShort(currentBrew.getSecondaryStartDate()));
        int secondaryDays = TimeUtility.daysBetween(currentBrew.getSecondaryStartDate(), currentBrew.getEndDate());
        String secondaryString = getResources().getQuantityString(R.plurals.pluralDays, secondaryDays, secondaryDays);
        secondaryRemainingDaysTextView.setText(secondaryString);

        endDateTextView.setText(TimeUtility.formatDateShort(currentBrew.getEndDate()));
    }

    private void saveBrew() {

        // Read Fields
        currentBrew.getRecipe().setName(brewNameEditText.getText().toString().trim());
        currentBrew.getRecipe().setPrimarySweetenerAmount(Integer.parseInt(primarySugarAmountEditText.getText().toString()));
        currentBrew.getRecipe().setWater(Double.parseDouble(waterAmountEditText.getText().toString()));
        currentBrew.getRecipe().setSecondarySweetenerAmount(Integer.parseInt(secondarySugarAmountEditText.getText().toString()));
        currentBrew.getRecipe().setIngredients(selectedIngredients);
        currentBrew.getRecipe().setNotes(notesEditText.getText().toString().trim());

        // Read each tea row
        currentBrew.getRecipe().getTeas().clear();
        for (int i = 0; i < teaList.getChildCount(); i++) {
            final View teaItem = teaList.getChildAt(i);
            AutoCompleteTextView nameEditText = teaItem.findViewById(R.id.tea_name_autocomplete);
            String name = nameEditText.getText().toString().trim();
            Spinner typeSpinner = teaItem.findViewById(R.id.tea_picker);
            TeaType type = TeaType.get(typeSpinner.getSelectedItemPosition());
            EditText amountEditText = teaItem.findViewById(R.id.tea_amount_picker);
            int amount = Integer.parseInt(amountEditText.getText().toString());
            currentBrew.getRecipe().addTea(new Ingredient(name, IngredientType.TEA, type, amount));
        }

        if (System.currentTimeMillis() > currentBrew.getPrimaryStartDate() &&
                System.currentTimeMillis() < currentBrew.getSecondaryStartDate()) {
            currentBrew.setStage(Stage.PRIMARY);
            currentBrew.setRunning(true);
        } else if (System.currentTimeMillis() > currentBrew.getSecondaryStartDate() &&
                System.currentTimeMillis() < currentBrew.getEndDate()) {
            currentBrew.setStage(Stage.SECONDARY);
            currentBrew.setRunning(true);
        } else if (System.currentTimeMillis() < currentBrew.getPrimaryStartDate()) {
            currentBrew.setStage(Stage.PAUSED);
            currentBrew.setRunning(true);
        } else if (System.currentTimeMillis() > currentBrew.getEndDate()) {
            currentBrew.setStage(Stage.COMPLETE);
            currentBrew.setRunning(false);
        }

        if (currentBrew.isRunning()) {
            docRef.set(currentBrew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        finish();
                    } else {
                        CoordinatorLayout rootView = findViewById(R.id.root_view);
                        Snackbar.make(rootView, "Error making database changes", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveBrew();
                                    }
                                })
                                .show();
                    }
                }
            });
        } else {
            docRef.delete();
            db.collection(Brew.HISTORY).add(currentBrew).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        finish();
                    } else {
                        CoordinatorLayout rootView = findViewById(R.id.root_view);
                        Snackbar.make(rootView, "Error making database changes", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveBrew();
                                    }
                                })
                                .show();
                    }
                }
            });
        }

    }

}
