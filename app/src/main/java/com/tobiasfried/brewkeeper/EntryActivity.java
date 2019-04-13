package com.tobiasfried.brewkeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.animation.ObjectAnimator;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.internal.CollapsingTextHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.tobiasfried.brewkeeper.constants.IngredientType;
import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.constants.TeaType;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.model.Ferment;
import com.tobiasfried.brewkeeper.model.Ingredient;
import com.tobiasfried.brewkeeper.utils.TimeUtility;
import com.tobiasfried.brewkeeper.viewmodel.EntryViewModel;
import com.tobiasfried.brewkeeper.viewmodel.EntryViewModelFactory;

import java.util.List;
import java.util.Objects;


public class EntryActivity extends AppCompatActivity {

    private static final String LOG_TAG = EntryActivity.class.getSimpleName();
    public static String EXTRA_BREW_ID = "brewID";
    public static String EXTRA_BREW_ID_HISTORY = "brewIDHistory";

    // Database
    protected FirebaseFirestore db;
    protected EntryViewModel viewModel;
    protected String brewId;
    protected String collection = Brew.CURRENT;
    protected DocumentReference docRef;

    // Model
    protected Brew currentBrew;
    protected List<Ingredient> teas;

    // Views
    @BindView(R.id.create_edit_text)
    EditText brewNameEditText;

    @BindView(R.id.primary_date_calendar)
    TextView primaryDateTextView;

    @BindView(R.id.primary_remaining_days)
    TextView primaryRemainingDaysTextView;

    @BindView(R.id.tea)
    LinearLayout teaList;

    @BindView(R.id.tea_name_autocomplete)
    AutoCompleteTextView teaNameEditText;

    @BindView(R.id.tea_amount_picker)
    EditText teaAmountEditText;

    @BindView(R.id.tea_picker)
    Spinner teaSpinner;

    @BindView(R.id.add_tea_button)
    MaterialButton teaAddButton;

    @BindView(R.id.primary_sugar_picker)
    Spinner primarySugarSpinner;

    @BindView(R.id.sugar_amount_picker)
    EditText primarySugarAmountEditText;

    @BindView(R.id.water_amount_picker)
    EditText waterAmountEditText;

    @BindView(R.id.notes)
    EditText notesEditText;

    @BindView(R.id.button_start)
    ExtendedFloatingActionButton submitButton;

    private boolean hasRequiredFields = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        ButterKnife.bind(this);

        // Get Database instance
        db = FirebaseFirestore.getInstance();

        // Get Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_entry);

        // Collapsing Text
//        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
//        TextView createTextView = findViewById(R.id.create_text_view);
//        appBarLayout.addOnOffsetChangedListener((l, verticalOffset) -> {
//            if (verticalOffset != 0) {
//                // TODO fine-tune scaling
//                brewNameEditText.setEnabled(false);
//                brewNameEditText.setScaleX(((float) (l.getTotalScrollRange() + verticalOffset)) / (float) l.getTotalScrollRange());
//                brewNameEditText.setScaleY(((float) (l.getTotalScrollRange() + verticalOffset)) / (float) l.getTotalScrollRange());
//                createTextView.setAlpha(((float) (l.getTotalScrollRange() + verticalOffset)) / (float) l.getTotalScrollRange());
//            } else {
//                brewNameEditText.setEnabled(true);
//                brewNameEditText.setScaleX(1f);
//                brewNameEditText.setScaleY(1f);
//                createTextView.setAlpha(1f);
//            }
//        });

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

                hasRequiredFields = (!name.isEmpty() && !teaName.isEmpty() && !teaAmount.isEmpty() &&
                        !primarySugarAmount.isEmpty() && !waterAmount.isEmpty());

                submitButton.setEnabled(hasRequiredFields && currentBrew.getPrimaryFerment() != null);
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

        // Set ClickListeners
        RelativeLayout primaryDate = findViewById(R.id.primary_date);
        primaryDate.setOnClickListener(v -> {
            DateRangeDialog fragment = new DateRangeDialog();
            fragment.setCallback(new DateRangeDialog.Callback() {
                @Override
                public void onCancelled() {
                    fragment.dismiss();
                }

                @Override
                public void onDateTimeRecurrenceSet(SelectedDate selectedDate, int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption, String recurrenceRule) {
                    Ferment primaryFerment = new Ferment(selectedDate.getStartDate().getTimeInMillis(),
                            selectedDate.getEndDate().getTimeInMillis());
                    currentBrew.setPrimaryFerment(primaryFerment);
                    currentBrew.setSecondaryFerment(new Ferment(primaryFerment.second, null));
                    refreshDates();
                    submitButton.setEnabled(hasRequiredFields && currentBrew.getPrimaryFerment() != null);
                }

            });
            SublimeOptions options = new SublimeOptions();
            options.setCanPickDateRange(true);
            options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER);

            Bundle args = new Bundle();
            args.putParcelable("SUBLIME_OPTIONS", options);
            fragment.setArguments(args);
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });
    }

    private void setupButtons() {
        submitButton.setOnClickListener(v -> {
            saveBrew();
            finish();
        });

        teaAddButton.setOnClickListener(v -> {
            LayoutInflater inflater = getLayoutInflater();
            final View tea = inflater.inflate(R.layout.list_item_tea, null);
            ImageButton removeButton = tea.findViewById(R.id.remove_button);
            removeButton.setOnClickListener(v1 -> teaList.removeView(tea));
            teaList.addView(tea);

            Spinner spinner = tea.findViewById(R.id.tea_picker);
            setupTeaSpinner(spinner, 0);
        });
    }

    private void fetchBrew() {
        viewModel.getBrew().observe(this, brew -> {
            currentBrew = brew;
            if (brewId != null) {
                setupBrew();
            } else {
                setupTeaSpinner(teaSpinner, 0);
                setupSugarSpinners();
                refreshDates();
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
            removeButton.setOnClickListener(v -> teaList.removeView(teaItem));

            teaList.addView(teaItem);
        }

        setupSugarSpinners();

        primarySugarSpinner.setSelection(currentBrew.getRecipe().getPrimarySweetener());
        primarySugarAmountEditText.setText(String.valueOf(currentBrew.getRecipe().getPrimarySweetenerAmount()), TextView.BufferType.EDITABLE);

        waterAmountEditText.setText(String.valueOf(currentBrew.getRecipe().getWater()));

        notesEditText.setText(currentBrew.getRecipe().getNotes());
    }

    private void refreshDates() {
        if (currentBrew.getPrimaryFerment() != null) {
            String primaryDatesString = (currentBrew.getPrimaryFerment().first != null ? TimeUtility.formatDateShort(currentBrew.getPrimaryFerment().first) : "") +
                    " – " + (currentBrew.getPrimaryFerment().second != null ? TimeUtility.formatDateShort(currentBrew.getPrimaryFerment().second) : "");
            primaryDateTextView.setText(primaryDatesString);
            int primaryDays = TimeUtility.daysBetween(currentBrew.getPrimaryFerment().first, currentBrew.getPrimaryFerment().second);
            String primaryDaysString = getResources().getQuantityString(R.plurals.pluralDays, primaryDays, primaryDays);
            primaryRemainingDaysTextView.setText(primaryDaysString);
        }
    }

    private void saveBrew() {

        // Read Fields
        currentBrew.getRecipe().setName(brewNameEditText.getText().toString().trim());
        currentBrew.getRecipe().setPrimarySweetenerAmount(Integer.parseInt(primarySugarAmountEditText.getText().toString()));
        currentBrew.getRecipe().setWater(Double.parseDouble(waterAmountEditText.getText().toString()));
        currentBrew.getRecipe().setNotes(notesEditText.getText().toString().trim());

        // Read each tea row
        currentBrew.getRecipe().getTeas().clear();
        for (int i = 0; i < teaList.getChildCount(); i++) {
            final View teaItem = teaList.getChildAt(i);
            AutoCompleteTextView nameEditText = teaItem.findViewById(R.id.tea_name_autocomplete);
            String name = nameEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                Spinner typeSpinner = teaItem.findViewById(R.id.tea_picker);
                TeaType type = TeaType.get(typeSpinner.getSelectedItemPosition());
                EditText amountEditText = teaItem.findViewById(R.id.tea_amount_picker);
                int amount = Integer.parseInt(amountEditText.getText().toString());
                currentBrew.getRecipe().addTea(new Ingredient(name, IngredientType.TEA, type, amount));
            }
        }

        currentBrew.setStage(Stage.PRIMARY);
        if (System.currentTimeMillis() > currentBrew.getEndDate()) {
            currentBrew.advanceStage();
        }

        docRef.set(currentBrew).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                finish();
            } else {
                CoordinatorLayout rootView = findViewById(R.id.root_view);
                Snackbar.make(rootView, "Error making database changes", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", v -> saveBrew())
                        .show();
            }
        });

    }

}
