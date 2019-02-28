package com.tobiasfried.brewkeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.tobiasfried.brewkeeper.constants.IngredientType;
import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.constants.TeaType;
import com.tobiasfried.brewkeeper.data.AppDatabase;
import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.data.Ingredient;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;


public class EntryActivity extends AppCompatActivity {



    private AppDatabase mDb;
    private ZonedDateTime primaryStartDate;
    private ZonedDateTime secondaryStartDate;
    private ZonedDateTime endDate;

    private Brew currentBrew;
    private Ingredient currentTea;
    private List<Ingredient> ingredients;
    private List<Ingredient> addedIngredients;
    private List<Ingredient> selectedIngredients;
    private List<Ingredient> deletedIngredients;

    private AutoCompleteTextView mBrewName;
    private TextView mPrimaryStartDate;
    private AutoCompleteTextView mTeaName;
    private EditText mTeaAmountPicker;
    private TextView mTeaPicker;
    private TextView mSugarPicker;
    private EditText mSugarAmountPicker;
    private TextView mSecondaryStartDate;
    private TextView mSugarPickerTwo;
    private EditText mSugarAmountPickerTwo;
    private ChipGroup mIngredientChipGroup;
    private TextView mEndDate;
    private MaterialButton mCancelButton;
    private MaterialButton mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        // Get AppDatabase instance
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Get Date
        primaryStartDate = ZonedDateTime.now();

        // Bind Views
        mBrewName = findViewById(R.id.brew_name_autocomplete);
        mPrimaryStartDate = findViewById(R.id.primary_date_calendar);
        mTeaName = findViewById(R.id.tea_name_autocomplete);
        mTeaAmountPicker = findViewById(R.id.tea_amount_picker);
        mTeaPicker = findViewById(R.id.tea_picker);
        mSugarPicker = findViewById(R.id.sugar_picker);
        mSugarAmountPicker = findViewById(R.id.sugar_amount_picker);
        mSecondaryStartDate = findViewById(R.id.secondary_date_calendar);
        mSugarPickerTwo = findViewById(R.id.sugar_picker_2);
        mSugarAmountPickerTwo = findViewById(R.id.sugar_amount_picker_2);
        mIngredientChipGroup = findViewById(R.id.ingredient_chip_group);
        mEndDate = findViewById(R.id.end_date_calendar);
        mCancelButton = findViewById(R.id.button_cancel);
        mSubmitButton = findViewById(R.id.button_start);

        currentBrew = new Brew();
        currentBrew.setPrimarySweetener(0);
        currentBrew.setSecondarySweetener(0);
        currentBrew.setStage(Stage.PRIMARY);

        currentTea = new Ingredient(null, IngredientType.TEA, TeaType.OTHER);

        addedIngredients = new ArrayList<>();
        selectedIngredients = new ArrayList<>();
        deletedIngredients = new ArrayList<>();

        setupButtons();
        setupDialogs();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get ingredients from database
        fetchIngredients();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Push changes to database
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.ingredientDao().deleteIngredients(deletedIngredients);
                mDb.ingredientDao().insertIngredientList(addedIngredients);
                Log.i("diskIO Executor", "removed ingredient");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchIngredients();
                        Log.i("runOnUiThread", "fetched ingredients");
                    }
                });
            }
        });
    }

    private void setupButtons() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

        mPrimaryStartDate.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(primaryStartDate));

        mSecondaryStartDate.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(primaryStartDate.plusDays(12)));
        mSecondaryStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        mEndDate.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(primaryStartDate.plusDays(15)));
        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
    }

    private void fetchIngredients() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                ingredients = new ArrayList<>(mDb.ingredientDao().loadAllFlavors());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupChips();
                    }
                });
            }
        });
    }

    private void setupChips() {
        mIngredientChipGroup.removeAllViews();
        for (final Ingredient i : ingredients) {
            final Chip chip = new Chip(this,  null, R.style.CustomChipColors);
            chip.setText(i.getName().toLowerCase());
            chip.setChipBackgroundColorResource(R.color.background_color_chip_state_list);
            chip.setCloseIconTint(getColorStateList(R.color.text_color_chip_state_list));
            chip.setTextColor(getColorStateList(R.color.text_color_chip_state_list));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
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

    private void showSweetenerPickerDialog(View v) {
        GenericPickerDialog newFragment = GenericPickerDialog.newInstance(R.array.array_sugar_types);
        newFragment.setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentBrew.setPrimarySweetener(which);
                mSugarPicker.setText(getResources().getStringArray(R.array.array_sugar_types)[which]);
                currentBrew.setSecondarySweetener(which);
                mSugarPickerTwo.setText(getResources().getStringArray(R.array.array_sugar_types)[which]);
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

    private void showDatePickerDialog(View v) {
        DateSelectionDialog newFragment = new DateSelectionDialog();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void saveBrew() {

        // Read Fields
        currentTea.setName(mTeaName.getText().toString().trim());
        currentBrew.setName(mBrewName.getText().toString().trim());
        currentBrew.setTeaAmount(Integer.parseInt(mTeaAmountPicker.getText().toString()));
        currentBrew.setPrimarySweetenerAmount(Integer.parseInt(mSugarAmountPicker.getText().toString()));
        currentBrew.setSecondarySweetenerAmount(Integer.parseInt(mSugarAmountPickerTwo.getText().toString()));
        currentBrew.setIngredients(selectedIngredients);

        primaryStartDate = ZonedDateTime.now();
        secondaryStartDate = primaryStartDate.plusDays(10);
        endDate = secondaryStartDate.plusDays(3);

        currentBrew.setPrimaryStartDate(primaryStartDate);
        currentBrew.setSecondaryStartDate(secondaryStartDate);
        currentBrew.setEndDate(endDate);
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
