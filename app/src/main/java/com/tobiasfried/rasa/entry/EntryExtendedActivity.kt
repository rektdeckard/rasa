package com.tobiasfried.rasa.entry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tobiasfried.rasa.constants.IngredientType
import com.tobiasfried.rasa.constants.Stage
import com.tobiasfried.rasa.constants.TeaType
import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.domain.Ferment
import com.tobiasfried.rasa.domain.Ingredient
import com.tobiasfried.rasa.utils.TimeUtility

import java.util.ArrayList
import java.util.Objects

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.tobiasfried.rasa.DateRangeDialog
import com.tobiasfried.rasa.InputDialog
import com.tobiasfried.rasa.R

import com.tobiasfried.rasa.entry.EntryActivity.EXTRA_BREW_ID
import com.tobiasfried.rasa.entry.EntryActivity.EXTRA_BREW_ID_HISTORY

class EntryExtendedActivity : AppCompatActivity() {

    // Database
    protected var db: FirebaseFirestore
    protected var viewModel: EntryViewModel
    protected var brewId: String? = null
    protected var collection = Brew.CURRENT
    protected var docRef: DocumentReference? = null

    // Model
    protected var currentBrew: Brew? = null
    protected var teas: List<Ingredient>
    private var ingredients: List<Ingredient>? = null
    private var selectedIngredients: MutableList<Ingredient>? = null

    // Views
    @BindView(R.id.create_edit_text)
    internal var brewNameEditText: EditText? = null

    @BindView(R.id.primary_date_calendar)
    internal var primaryDateTextView: TextView? = null

    @BindView(R.id.primary_remaining_days)
    internal var primaryRemainingDaysTextView: TextView? = null

    @BindView(R.id.primary_date)
    internal var primaryDate: RelativeLayout? = null

    @BindView(R.id.tea)
    internal var teaList: LinearLayout? = null

    @BindView(R.id.tea_name_autocomplete)
    internal var teaNameEditText: AutoCompleteTextView? = null

    @BindView(R.id.tea_amount_picker)
    internal var teaAmountEditText: EditText? = null

    @BindView(R.id.tea_picker)
    internal var teaSpinner: Spinner? = null

    @BindView(R.id.add_tea_button)
    internal var teaAddButton: MaterialButton? = null

    @BindView(R.id.primary_sugar_picker)
    internal var primarySugarSpinner: Spinner? = null

    @BindView(R.id.sugar_amount_picker)
    internal var primarySugarAmountEditText: EditText? = null

    @BindView(R.id.water_amount_picker)
    internal var waterAmountEditText: EditText? = null

    @BindView(R.id.secondary_date_calendar)
    internal var secondaryDateTextView: TextView? = null

    @BindView(R.id.secondary_remaining_days)
    internal var secondaryRemainingDaysTextView: TextView? = null

    @BindView(R.id.secondary_date)
    internal var secondaryDate: RelativeLayout? = null

    @BindView(R.id.secondary_sugar_picker)
    internal var secondarySugarSpinner: Spinner? = null

    @BindView(R.id.secondary_sugar_amount_picker)
    internal var secondarySugarAmountEditText: EditText? = null

    @BindView(R.id.ingredient_chip_group)
    internal var flavorChipGroup: ChipGroup? = null

    @BindView(R.id.add_ingredient_button)
    internal var flavorAddButton: MaterialButton? = null

    @BindView(R.id.notes)
    internal var notesEditText: EditText? = null

    @BindView(R.id.button_start)
    internal var submitButton: ExtendedFloatingActionButton? = null

    private var editing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_extended)
        ButterKnife.bind(this)

        // TODO fetch from loaded brew
        selectedIngredients = ArrayList()

        // Get Database instance
        db = FirebaseFirestore.getInstance()

        // Get Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.menu_entry)

        // Get ViewModel
        if (intent.hasExtra(EXTRA_BREW_ID)) {
            brewId = Objects.requireNonNull(intent.extras).getString(EXTRA_BREW_ID)
        } else if (intent.hasExtra(EXTRA_BREW_ID_HISTORY)) {
            brewId = Objects.requireNonNull(intent.extras).getString(EXTRA_BREW_ID_HISTORY)
            collection = Brew.HISTORY
            // TODO Not this
            setContentView(R.layout.card_history)
        }
        val factory = EntryViewModelFactory(db, collection, brewId)
        viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel::class.java)
        fetchBrew()

        docRef = viewModel.documentReference

        setupDialogs()
        setupFieldWatchers()
        setupButtons()
        setFieldsEditable()
        setupFab()
    }

    private fun toggleEditMode() {
        editing = !editing
        submitButton!!.setText(if (editing) R.string.action_save else R.string.edit)
        setFieldsEditable()
    }

    private fun setFieldsEditable() {
        //        CardView primaryCard = findViewById(R.id.primary_card);
        //        RelativeLayout primaryLayout = findViewById(R.id.primary_layout);
        //        if (!editing) {
        //            primaryCard.setCardBackgroundColor(getResources().getColor(android.R.color.transparent, getTheme()));
        //            primaryLayout.setBackground(getDrawable(R.drawable.card_border));
        //        } else {
        //            primaryCard.setCardBackgroundColor(getResources().getColor(android.R.color.white, getTheme()));
        //            primaryLayout.setBackground(null);
        //        }

        brewNameEditText!!.isEnabled = editing
        teaNameEditText!!.isEnabled = editing
        teaSpinner!!.isEnabled = editing
        teaAmountEditText!!.isEnabled = editing
        teaAddButton!!.visibility = if (editing) View.VISIBLE else View.GONE
        findViewById<View>(R.id.divider_teas).visibility = if (editing) View.VISIBLE else View.GONE
        primarySugarSpinner!!.isEnabled = editing
        primarySugarAmountEditText!!.isEnabled = editing
        waterAmountEditText!!.isEnabled = editing

        for (i in 1 until teaList!!.childCount) {
            val v = teaList!!.getChildAt(i)
            v.findViewById<View>(R.id.tea_name_autocomplete).isClickable = editing
            v.findViewById<View>(R.id.tea_picker).isClickable = editing
            v.findViewById<View>(R.id.tea_amount_picker).isClickable = editing
            v.findViewById<View>(R.id.remove_button).visibility = if (editing) View.VISIBLE else View.GONE
        }

        primaryDate!!.isClickable = editing
        secondaryDate!!.isClickable = editing
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_entry, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cancel -> finish()
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        fetchIngredients()
    }

    private fun setupTeaSpinner(spinner: Spinner, position: Int) {
        val teaTypes = ArrayAdapter.createFromResource(this, R.array.array_tea_types, R.layout.spinner_item_small)
        teaTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = teaTypes
        spinner.setSelection(position)
    }

    private fun setupSugarSpinners() {
        val sweetenerTypes = ArrayAdapter.createFromResource(this, R.array.array_sugar_types, R.layout.spinner_item)
        sweetenerTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        primarySugarSpinner!!.adapter = sweetenerTypes
        secondarySugarSpinner!!.adapter = sweetenerTypes
    }

    private fun setupFieldWatchers() {

        val requiredWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name = brewNameEditText!!.text.toString().trim { it <= ' ' }
                val teaName = teaNameEditText!!.text.toString().trim { it <= ' ' }
                val teaAmount = teaAmountEditText!!.text.toString().trim { it <= ' ' }
                val primarySugarAmount = primarySugarAmountEditText!!.text.toString().trim { it <= ' ' }
                val waterAmount = waterAmountEditText!!.text.toString().trim { it <= ' ' }

                submitButton!!.isEnabled = !name.isEmpty() && !teaName.isEmpty() && !teaAmount.isEmpty() &&
                        !primarySugarAmount.isEmpty() && !waterAmount.isEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        brewNameEditText!!.addTextChangedListener(requiredWatcher)
        teaNameEditText!!.addTextChangedListener(requiredWatcher)
        teaAmountEditText!!.addTextChangedListener(requiredWatcher)
        primarySugarAmountEditText!!.addTextChangedListener(requiredWatcher)
        waterAmountEditText!!.addTextChangedListener(requiredWatcher)

    }

    private fun setupDialogs() {
        // Set Default Date Strings
        if (currentBrew != null) {
            refreshDates()
        }

        // Set OnClickListeners
        primaryDate!!.setOnClickListener { v ->
            val fragment = DateRangeDialog()
            fragment.setCallback(object : DateRangeDialog.Callback {
                override fun onCancelled() {
                    fragment.dismiss()
                }

                override fun onDateTimeRecurrenceSet(selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
                                                     recurrenceOption: SublimeRecurrencePicker.RecurrenceOption, recurrenceRule: String) {
                    val primaryFerment = Ferment(selectedDate.startDate.timeInMillis,
                            selectedDate.endDate.timeInMillis)
                    currentBrew!!.primaryFerment = primaryFerment
                    currentBrew!!.secondaryFerment = Ferment(primaryFerment.second, null)
                    refreshDates()
                }

            })

            // Sublime Options
            val options = SublimeOptions()
            options.setCanPickDateRange(true)
            options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER)

            val args = Bundle()
            args.putParcelable("SUBLIME_OPTIONS", options)
            fragment.arguments = args
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            fragment.show(supportFragmentManager, "SUBLIME_PICKER")
        }

        secondaryDate!!.setOnClickListener { v ->
            val fragment = DateRangeDialog()
            fragment.setCallback(object : DateRangeDialog.Callback {
                override fun onCancelled() {
                    fragment.dismiss()
                }

                override fun onDateTimeRecurrenceSet(selectedDate: SelectedDate, hourOfDay: Int, minute: Int,
                                                     recurrenceOption: SublimeRecurrencePicker.RecurrenceOption, recurrenceRule: String) {
                    val secondaryFerment = Ferment(selectedDate.startDate.timeInMillis,
                            selectedDate.endDate.timeInMillis)
                    currentBrew!!.secondaryFerment = secondaryFerment
                    refreshDates()
                }

            })

            // Sublime Options
            val options = SublimeOptions()
            options.setCanPickDateRange(true)
            options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER)
            if (currentBrew!!.primaryFerment!!.second != null) {
                options.setDateRange(currentBrew!!.primaryFerment!!.second!!, TimeUtility.MAX_DATE)
            }

            val args = Bundle()
            args.putParcelable("SUBLIME_OPTIONS", options)
            fragment.arguments = args
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            fragment.show(supportFragmentManager, "SUBLIME_PICKER_2")
        }
    }

    private fun setupButtons() {

        teaAddButton!!.setOnClickListener { v ->
            val inflater = layoutInflater
            val tea = inflater.inflate(R.layout.list_item_tea, teaList)
            val removeButton = tea.findViewById<ImageButton>(R.id.remove_button)
            removeButton.setOnClickListener { v1 -> teaList!!.removeView(tea) }
            teaList!!.addView(tea)

            val spinner = tea.findViewById<Spinner>(R.id.tea_picker)
            setupTeaSpinner(spinner, 0)
        }

        flavorAddButton!!.setOnClickListener { v ->
            val newFragment = InputDialog.instance
            newFragment.setOnClickListener({ dialog, input ->
                if (input != "") {
                    val newIngredient = Ingredient(input, IngredientType.FLAVOR, null, 0)
                    db.collection(Ingredient.COLLECTION).add(newIngredient)
                    setupChips()
                }
            })

            newFragment.show(supportFragmentManager, "ingredientInput")
        }
    }

    private fun setupFab() {
        submitButton!!.setOnClickListener { v ->
            if (editing) {
                saveBrew()
                toggleEditMode()
            } else {
                toggleEditMode()
            }
        }
    }

    private fun fetchBrew() {
        viewModel.brew.observe(this, { brew ->
            currentBrew = brew
            if (brewId != null) {
                setupBrew()
            } else {
                setupTeaSpinner(teaSpinner!!, 0)
                setupSugarSpinners()
                refreshDates()
            }
        })
    }

    private fun fetchIngredients() {
        viewModel.flavors.observe(this, { allIngredients ->
            ingredients = allIngredients
            setupChips()
        })

        viewModel.teas.observe(this, { allTeas -> teas = allTeas })
    }

    private fun setupBrew() {
        refreshDates()

        brewNameEditText!!.setText(currentBrew!!.recipe.name)

        // Populate Teas
        val inflater = layoutInflater

        val firstTea = currentBrew!!.recipe.teas[0]
        teaNameEditText!!.setText(firstTea.name)
        setupTeaSpinner(teaSpinner!!, firstTea.teaType!!.code)
        teaAmountEditText!!.setText(firstTea.amount.toString())

        for (i in 1 until currentBrew!!.recipe.teas.size) {
            val tea = currentBrew!!.recipe.teas[i]

            val teaItem = inflater.inflate(R.layout.list_item_tea, null)

            val nameEditText = teaItem.findViewById<AutoCompleteTextView>(R.id.tea_name_autocomplete)
            nameEditText.setText(tea.name, TextView.BufferType.EDITABLE)

            val amountEditText = teaItem.findViewById<EditText>(R.id.tea_amount_picker)
            amountEditText.setText(tea.amount.toString(), TextView.BufferType.EDITABLE)

            val typeSpinner = teaItem.findViewById<Spinner>(R.id.tea_picker)
            setupTeaSpinner(typeSpinner, tea.teaType!!.code)

            val removeButton = teaItem.findViewById<ImageButton>(R.id.remove_button)
            removeButton.visibility = View.GONE
            removeButton.setOnClickListener { v -> teaList!!.removeView(teaItem) }

            teaList!!.addView(teaItem)
        }

        setupSugarSpinners()

        primarySugarSpinner!!.setSelection(currentBrew!!.recipe.primarySweetener)
        primarySugarAmountEditText!!.setText(currentBrew!!.recipe.primarySweetenerAmount.toString(), TextView.BufferType.EDITABLE)

        waterAmountEditText!!.setText(currentBrew!!.recipe.water.toString())

        secondarySugarSpinner!!.setSelection(currentBrew!!.recipe.secondarySweetener)
        secondarySugarAmountEditText!!.setText(currentBrew!!.recipe.secondarySweetenerAmount.toString(), TextView.BufferType.EDITABLE)

        selectedIngredients!!.addAll(currentBrew!!.recipe.ingredients)

        notesEditText!!.setText(currentBrew!!.recipe.notes)
    }

    private fun setupChips() {
        flavorChipGroup!!.removeAllViews()
        // Populate ChipGroup from ingredients list
        for (i in ingredients!!) {
            val chip = Chip(this, null, R.style.ChipStyle)
            chip.text = i.name!!.toLowerCase()
            chip.setTextColor(getColorStateList(R.color.color_states_chip_text))
            chip.typeface = ResourcesCompat.getFont(this, R.font.google_sans_medium)
            chip.setChipBackgroundColorResource(android.R.color.transparent)
            chip.setChipStrokeColorResource(R.color.color_states_chip_text)
            chip.chipStrokeWidth = 4.0f
            chip.isCheckable = true
            chip.isCheckedIconVisible = false

            // TODO fix check chip
            if (currentBrew != null && currentBrew!!.recipe.ingredients.contains(i)) {
                chip.isChecked = true
            }

            // Set CheckedListener
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    selectedIngredients!!.add(i)
                } else {
                    selectedIngredients!!.remove(i)
                }
            }

            // Set LongClick for delete action
            chip.setOnLongClickListener { v ->
                if (chip.isCloseIconVisible) {
                    chip.isCheckable = true
                } else {
                    chip.isChecked = false
                    chip.isCheckable = false
                }
                chip.isCloseIconVisible = !chip.isCloseIconVisible
                true
            }


            // Set delete action
            chip.setOnCloseIconClickListener { v ->
                currentBrew!!.recipe.ingredients.remove(i)
                db.collection(Ingredient.COLLECTION).whereEqualTo("name", i.name).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful && !Objects.requireNonNull<QuerySnapshot>(task.result).isEmpty()) {
                                for (document in task.result!!) {
                                    db.collection(Ingredient.COLLECTION).document(document.id).delete()
                                }
                            }
                        }
            }

            // Add to ChipGroup
            flavorChipGroup!!.addView(chip)
        }
    }

    private fun refreshDates() {
        if (currentBrew!!.primaryFerment != null) {
            val primaryDatesString = (if (currentBrew!!.primaryFerment!!.first != null) TimeUtility.formatDateShort(currentBrew!!.primaryFerment!!.first!!) else "") +
                    " – " + if (currentBrew!!.primaryFerment!!.second != null) TimeUtility.formatDateShort(currentBrew!!.primaryFerment!!.second!!) else ""
            primaryDateTextView!!.text = primaryDatesString
            val primaryDays = TimeUtility.daysBetween(currentBrew!!.primaryFerment!!.first!!, currentBrew!!.primaryFerment!!.second!!)
            val primaryDaysString = resources.getQuantityString(R.plurals.pluralDays, primaryDays, primaryDays)
            primaryRemainingDaysTextView!!.text = primaryDaysString
        }

        if (currentBrew!!.secondaryFerment != null) {
            val secondaryDatesString = (if (currentBrew!!.secondaryFerment!!.first != null) TimeUtility.formatDateShort(currentBrew!!.secondaryFerment!!.first!!) else "") +
                    " – " + if (currentBrew!!.secondaryFerment!!.second != null) TimeUtility.formatDateShort(currentBrew!!.secondaryFerment!!.second!!) else ""
            secondaryDateTextView!!.text = secondaryDatesString
            if (currentBrew!!.secondaryFerment!!.second != null) {
                val secondaryDays = TimeUtility.daysBetween(currentBrew!!.secondaryFerment!!.first!!, currentBrew!!.secondaryFerment!!.second!!)
                val secondaryDaysString = resources.getQuantityString(R.plurals.pluralDays, secondaryDays, secondaryDays)
                secondaryRemainingDaysTextView!!.text = secondaryDaysString
            }
        }
    }

    private fun saveBrew() {

        // Read Fields
        currentBrew!!.recipe.name = brewNameEditText!!.text.toString().trim { it <= ' ' }
        currentBrew!!.recipe.primarySweetenerAmount = Integer.parseInt(primarySugarAmountEditText!!.text.toString())
        currentBrew!!.recipe.water = java.lang.Double.parseDouble(waterAmountEditText!!.text.toString())
        currentBrew!!.recipe.secondarySweetenerAmount = Integer.parseInt(secondarySugarAmountEditText!!.text.toString())
        currentBrew!!.recipe.ingredients = selectedIngredients
        currentBrew!!.recipe.notes = notesEditText!!.text.toString().trim { it <= ' ' }

        // Read each tea row
        currentBrew!!.recipe.teas.clear()
        for (i in 0 until teaList!!.childCount) {
            val teaItem = teaList!!.getChildAt(i)
            val nameEditText = teaItem.findViewById<AutoCompleteTextView>(R.id.tea_name_autocomplete)
            val name = nameEditText.text.toString().trim { it <= ' ' }
            if (!name.isEmpty()) {
                val typeSpinner = teaItem.findViewById<Spinner>(R.id.tea_picker)
                val type = TeaType.get(typeSpinner.selectedItemPosition)
                val amountEditText = teaItem.findViewById<EditText>(R.id.tea_amount_picker)
                val amount = Integer.parseInt(amountEditText.text.toString())
                currentBrew!!.recipe.addTea(Ingredient(name, IngredientType.TEA, type, amount))
            }
        }

        if (currentBrew!!.secondaryFerment!!.first != null && System.currentTimeMillis() > currentBrew!!.secondaryFerment!!.first) {
            if (currentBrew!!.secondaryFerment!!.second != null && System.currentTimeMillis() > currentBrew!!.secondaryFerment!!.second) {
                currentBrew!!.stage = Stage.COMPLETE
                docRef!!.delete()
                db.collection(Brew.HISTORY).add(currentBrew!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        finish()
                    } else {
                        val rootView = findViewById<CoordinatorLayout>(R.id.root_view)
                        Snackbar.make(rootView, "Error making database changes", Snackbar.LENGTH_LONG)
                                .setAction("Retry") { v -> saveBrew() }
                                .show()
                    }
                }
            } else {
                currentBrew!!.stage = Stage.SECONDARY
            }
        } else if (System.currentTimeMillis() > currentBrew!!.primaryFerment!!.second) {
            currentBrew!!.stage = Stage.PAUSED
        } else {
            currentBrew!!.stage = Stage.PRIMARY
        }

        // Save brew, show SnackBar if save fails
        docRef!!.set(currentBrew!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                finish()
            } else {
                val rootView = findViewById<CoordinatorLayout>(R.id.root_view)
                Snackbar.make(rootView, "Error making database changes", Snackbar.LENGTH_LONG)
                        .setAction("Retry") { v -> saveBrew() }
                        .show()
            }
        }


    }
}
