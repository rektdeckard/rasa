package com.tobiasfried.rasa;

import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tobiasfried.rasa.model.Brew;
import com.tobiasfried.rasa.model.Ingredient;
import com.tobiasfried.rasa.utils.TimeUtility;
import com.tobiasfried.rasa.viewmodel.EntryViewModel;
import com.tobiasfried.rasa.viewmodel.EntryViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tobiasfried.rasa.EntryActivity.EXTRA_BREW_ID;
import static com.tobiasfried.rasa.EntryActivity.EXTRA_BREW_ID_HISTORY;

public class DetailActivity extends AppCompatActivity {

    // Database
    protected FirebaseFirestore db;
    protected EntryViewModel viewModel;
    protected String brewId;
    protected String collection = Brew.CURRENT;
    protected DocumentReference docRef;

    // Model
    protected Brew currentBrew;
    protected List<Ingredient> teas;
    private List<Ingredient> ingredients;
    private List<Ingredient> selectedIngredients;

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

    @BindView(R.id.primary_sugar_picker)
    Spinner primarySugarSpinner;

    @BindView(R.id.sugar_amount_picker)
    EditText primarySugarAmountEditText;

    @BindView(R.id.water_amount_picker)
    EditText waterAmountEditText;

    @BindView(R.id.secondary_date_calendar)
    TextView secondaryDateTextView;

    @BindView(R.id.secondary_remaining_days)
    TextView secondaryRemainingDaysTextView;

    @BindView(R.id.secondary_sugar_picker)
    Spinner secondarySugarSpinner;

    @BindView(R.id.secondary_sugar_amount_picker)
    EditText secondarySugarAmountEditText;

    @BindView(R.id.ingredient_chip_group)
    ChipGroup flavorChipGroup;

    @BindView(R.id.notes)
    EditText notesEditText;

    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        // TODO fetch from loaded brew
        selectedIngredients = new ArrayList<>();

        // Get Database instance
        db = FirebaseFirestore.getInstance();

        // Get Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_entry);

        // Get ViewModel
        if (getIntent().hasExtra(EXTRA_BREW_ID)) {
            brewId = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_BREW_ID);
        } else if (getIntent().hasExtra(EXTRA_BREW_ID_HISTORY)) {
            brewId = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_BREW_ID_HISTORY);
            collection = Brew.HISTORY;
            // TODO Not this
        }
        EntryViewModelFactory factory = new EntryViewModelFactory(db, collection, brewId);
        viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel.class);
        fetchBrew();

        docRef = viewModel.getDocumentReference();

        setFieldsEditable();
    }

    private void setFieldsEditable() {
        brewNameEditText.setEnabled(editing);
        teaNameEditText.setEnabled(editing);
        teaSpinner.setEnabled(editing);
        teaAmountEditText.setEnabled(editing);
        primarySugarSpinner.setEnabled(editing);
        primarySugarAmountEditText.setEnabled(editing);
        waterAmountEditText.setEnabled(editing);

        for (int i = 1; i < teaList.getChildCount(); i++) {
            View v = teaList.getChildAt(i);
            v.findViewById(R.id.tea_name_autocomplete).setEnabled(editing);
            v.findViewById(R.id.tea_picker).setEnabled(editing);
            v.findViewById(R.id.tea_amount_picker).setEnabled(editing);
            v.findViewById(R.id.remove_button).setVisibility(editing ? View.VISIBLE : View.GONE);
        }
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
        secondarySugarSpinner.setAdapter(sweetenerTypes);
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

    private void fetchIngredients() {
        viewModel.getFlavors().observe(this, allIngredients -> {
            ingredients = allIngredients;
            setupChips();
        });

        viewModel.getTeas().observe(this, allTeas -> teas = allTeas);
    }

    private void setupBrew() {
        refreshDates();
        fetchIngredients();

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
            removeButton.setVisibility(View.GONE);
            removeButton.setOnClickListener(v -> teaList.removeView(teaItem));

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
        // Populate ChipGroup from ingredients list
        for (final Ingredient i : currentBrew.getRecipe().getIngredients()) {
            Chip chip = new Chip(this, null, R.style.ChipStyle);
            chip.setText(i.getName().toLowerCase());
            chip.setTextColor(getResources().getColor(R.color.colorAccent, getTheme()));
            chip.setTypeface(ResourcesCompat.getFont(this, R.font.google_sans_medium));
//            chip.setChipBackgroundColorResource(R.color.color_states_chips);
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(R.color.colorAccent);
            chip.setChipStrokeWidth(4.0f);
            chip.setChecked(true);
            chip.setEnabled(false);
            chip.setCheckedIconVisible(false);

            // Add to ChipGroup
            flavorChipGroup.addView(chip);
        }
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

        if (currentBrew.getSecondaryFerment() != null) {
            String secondaryDatesString = (currentBrew.getSecondaryFerment().first != null ? TimeUtility.formatDateShort(currentBrew.getSecondaryFerment().first) : "") +
                    " – " + (currentBrew.getSecondaryFerment().second != null ? TimeUtility.formatDateShort(currentBrew.getSecondaryFerment().second) : "");
            secondaryDateTextView.setText(secondaryDatesString);
            if (currentBrew.getSecondaryFerment().second != null) {
                int secondaryDays = TimeUtility.daysBetween(currentBrew.getSecondaryFerment().first, currentBrew.getSecondaryFerment().second);
                String secondaryDaysString = getResources().getQuantityString(R.plurals.pluralDays, secondaryDays, secondaryDays);
                secondaryRemainingDaysTextView.setText(secondaryDaysString);
            }
        }
    }

}
