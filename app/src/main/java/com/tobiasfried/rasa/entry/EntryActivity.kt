package com.tobiasfried.rasa.entry

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
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
import java.util.Objects

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.tobiasfried.rasa.DateRangeDialog
import com.tobiasfried.rasa.R


class EntryActivity : AppCompatActivity() {

    // Database
    protected var db: FirebaseFirestore
    protected var viewModel: EntryViewModel
    protected var brewId: String? = null
    protected var collection = Brew.CURRENT
    protected var docRef: DocumentReference? = null

    // Model
    protected var currentBrew: Brew? = null
    protected var teas: List<Ingredient>? = null

    // Views
    @BindView(R.id.create_edit_text)
    internal var brewNameEditText: EditText? = null

    @BindView(R.id.primary_date_calendar)
    internal var primaryDateTextView: TextView? = null

    @BindView(R.id.primary_remaining_days)
    internal var primaryRemainingDaysTextView: TextView? = null

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

    @BindView(R.id.notes)
    internal var notesEditText: EditText? = null

    @BindView(R.id.button_start)
    internal var submitButton: ExtendedFloatingActionButton? = null

    private var hasRequiredFields = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        ButterKnife.bind(this)

        // Get Database instance
        db = FirebaseFirestore.getInstance()

        // Get Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.inflateMenu(R.menu.menu_entry)

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
        if (intent.hasExtra(EXTRA_BREW_ID)) {
            brewId = Objects.requireNonNull(intent.extras).getString(EXTRA_BREW_ID)
        } else if (intent.hasExtra(EXTRA_BREW_ID_HISTORY)) {
            brewId = Objects.requireNonNull(intent.extras).getString(EXTRA_BREW_ID_HISTORY)
            collection = Brew.HISTORY
        }
        val factory = EntryViewModelFactory(db, collection, brewId)
        viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel::class.java)
        fetchBrew()

        docRef = viewModel.documentReference

        setupDialogs()
        setupFieldWatchers()
        setupButtons()

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

                hasRequiredFields = !name.isEmpty() && !teaName.isEmpty() && !teaAmount.isEmpty() &&
                        !primarySugarAmount.isEmpty() && !waterAmount.isEmpty()

                submitButton!!.isEnabled = hasRequiredFields && currentBrew!!.primaryFerment != null
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

        // Set ClickListeners
        val primaryDate = findViewById<RelativeLayout>(R.id.primary_date)
        primaryDate.setOnClickListener { v ->
            val fragment = DateRangeDialog()
            fragment.setCallback(object : DateRangeDialog.Callback {
                override fun onCancelled() {
                    fragment.dismiss()
                }

                override fun onDateTimeRecurrenceSet(selectedDate: SelectedDate, hourOfDay: Int, minute: Int, recurrenceOption: SublimeRecurrencePicker.RecurrenceOption, recurrenceRule: String) {
                    val primaryFerment = Ferment(selectedDate.startDate.timeInMillis,
                            selectedDate.endDate.timeInMillis)
                    currentBrew!!.primaryFerment = primaryFerment
                    currentBrew!!.secondaryFerment = Ferment(primaryFerment.second, null)
                    refreshDates()
                    submitButton!!.isEnabled = hasRequiredFields && currentBrew!!.primaryFerment != null
                }

            })
            val options = SublimeOptions()
            options.setCanPickDateRange(true)
            options.setDisplayOptions(SublimeOptions.ACTIVATE_DATE_PICKER)

            val args = Bundle()
            args.putParcelable("SUBLIME_OPTIONS", options)
            fragment.arguments = args
            fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0)
            fragment.show(supportFragmentManager, "datePicker")
        }
    }

    private fun setupButtons() {
        submitButton!!.setOnClickListener { v ->
            saveBrew()
            finish()
        }

        teaAddButton!!.setOnClickListener { v ->
            val inflater = layoutInflater
            val tea = inflater.inflate(R.layout.list_item_tea, null)
            val removeButton = tea.findViewById<ImageButton>(R.id.remove_button)
            removeButton.setOnClickListener { v1 -> teaList!!.removeView(tea) }
            teaList!!.addView(tea)

            val spinner = tea.findViewById<Spinner>(R.id.tea_picker)
            setupTeaSpinner(spinner, 0)
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
            removeButton.setOnClickListener { v -> teaList!!.removeView(teaItem) }

            teaList!!.addView(teaItem)
        }

        setupSugarSpinners()

        primarySugarSpinner!!.setSelection(currentBrew!!.recipe.primarySweetener)
        primarySugarAmountEditText!!.setText(currentBrew!!.recipe.primarySweetenerAmount.toString(), TextView.BufferType.EDITABLE)

        waterAmountEditText!!.setText(currentBrew!!.recipe.water.toString())

        notesEditText!!.setText(currentBrew!!.recipe.notes)
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
    }

    private fun saveBrew() {

        // Read Fields
        currentBrew!!.recipe.name = brewNameEditText!!.text.toString().trim { it <= ' ' }
        currentBrew!!.recipe.primarySweetenerAmount = Integer.parseInt(primarySugarAmountEditText!!.text.toString())
        currentBrew!!.recipe.water = java.lang.Double.parseDouble(waterAmountEditText!!.text.toString())
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

        currentBrew!!.stage = Stage.PRIMARY
        if (System.currentTimeMillis() > currentBrew!!.endDate) {
            currentBrew!!.advanceStage()
        }

        docRef!!.set(currentBrew!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                finish()
            } else {
                val rootView = findViewById<CoordinatorLayout>(R.id.root_view)
                Snackbar.make(rootView, "Error making database changes", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry") { v -> saveBrew() }
                        .show()
            }
        }

    }

    companion object {

        private val LOG_TAG = EntryActivity::class.java.simpleName
        var EXTRA_BREW_ID = "brewID"
        var EXTRA_BREW_ID_HISTORY = "brewIDHistory"
    }

}
