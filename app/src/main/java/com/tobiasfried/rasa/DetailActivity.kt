package com.tobiasfried.rasa

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView

import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.domain.Ingredient
import com.tobiasfried.rasa.utils.TimeUtility
import com.tobiasfried.rasa.entry.EntryViewModel
import com.tobiasfried.rasa.entry.EntryViewModelFactory

import java.util.ArrayList
import java.util.Objects

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife

import com.tobiasfried.rasa.entry.EntryActivity.EXTRA_BREW_ID
import com.tobiasfried.rasa.entry.EntryActivity.EXTRA_BREW_ID_HISTORY

class DetailActivity : AppCompatActivity() {

    // Database
    protected var db: FirebaseFirestore
    protected var viewModel: EntryViewModel
    protected var brewId: String? = null
    protected var collection = Brew.CURRENT
    protected var docRef: DocumentReference? = null

    // Model
    protected var currentBrew: Brew
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

    @BindView(R.id.tea)
    internal var teaList: LinearLayout? = null

    @BindView(R.id.tea_name_autocomplete)
    internal var teaNameEditText: AutoCompleteTextView? = null

    @BindView(R.id.tea_amount_picker)
    internal var teaAmountEditText: EditText? = null

    @BindView(R.id.tea_picker)
    internal var teaSpinner: Spinner? = null

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

    @BindView(R.id.secondary_sugar_picker)
    internal var secondarySugarSpinner: Spinner? = null

    @BindView(R.id.secondary_sugar_amount_picker)
    internal var secondarySugarAmountEditText: EditText? = null

    @BindView(R.id.ingredient_chip_group)
    internal var flavorChipGroup: ChipGroup? = null

    @BindView(R.id.notes)
    internal var notesEditText: EditText? = null

    private val editing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
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
        }
        val factory = EntryViewModelFactory(db, collection, brewId)
        viewModel = ViewModelProviders.of(this, factory).get(EntryViewModel::class.java)
        fetchBrew()

        docRef = viewModel.documentReference

        setFieldsEditable()
    }

    private fun setFieldsEditable() {
        brewNameEditText!!.isEnabled = editing
        teaNameEditText!!.isEnabled = editing
        teaSpinner!!.isEnabled = editing
        teaAmountEditText!!.isEnabled = editing
        primarySugarSpinner!!.isEnabled = editing
        primarySugarAmountEditText!!.isEnabled = editing
        waterAmountEditText!!.isEnabled = editing

        for (i in 1 until teaList!!.childCount) {
            val v = teaList!!.getChildAt(i)
            v.findViewById<View>(R.id.tea_name_autocomplete).isEnabled = editing
            v.findViewById<View>(R.id.tea_picker).isEnabled = editing
            v.findViewById<View>(R.id.tea_amount_picker).isEnabled = editing
            v.findViewById<View>(R.id.remove_button).visibility = if (editing) View.VISIBLE else View.GONE
        }
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
        secondarySugarSpinner!!.adapter = sweetenerTypes
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
        fetchIngredients()

        brewNameEditText!!.setText(currentBrew.recipe.name)

        // Populate Teas
        val inflater = layoutInflater

        val firstTea = currentBrew.recipe.teas[0]
        teaNameEditText!!.setText(firstTea.name)
        setupTeaSpinner(teaSpinner!!, firstTea.teaType!!.code)
        teaAmountEditText!!.setText(firstTea.amount.toString())

        for (i in 1 until currentBrew.recipe.teas.size) {
            val tea = currentBrew.recipe.teas[i]

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

        primarySugarSpinner!!.setSelection(currentBrew.recipe.primarySweetener)
        primarySugarAmountEditText!!.setText(currentBrew.recipe.primarySweetenerAmount.toString(), TextView.BufferType.EDITABLE)

        waterAmountEditText!!.setText(currentBrew.recipe.water.toString())

        secondarySugarSpinner!!.setSelection(currentBrew.recipe.secondarySweetener)
        secondarySugarAmountEditText!!.setText(currentBrew.recipe.secondarySweetenerAmount.toString(), TextView.BufferType.EDITABLE)

        selectedIngredients!!.addAll(currentBrew.recipe.ingredients)

        notesEditText!!.setText(currentBrew.recipe.notes)
    }

    private fun setupChips() {
        flavorChipGroup!!.removeAllViews()
        // Populate ChipGroup from ingredients list
        for (i in currentBrew.recipe.ingredients) {
            val chip = Chip(this, null, R.style.ChipStyle)
            chip.text = i.name!!.toLowerCase()
            chip.setTextColor(resources.getColor(R.color.colorAccent, theme))
            chip.typeface = ResourcesCompat.getFont(this, R.font.google_sans_medium)
            //            chip.setChipBackgroundColorResource(R.color.color_states_chips);
            chip.setChipBackgroundColorResource(android.R.color.transparent)
            chip.setChipStrokeColorResource(R.color.colorAccent)
            chip.chipStrokeWidth = 4.0f
            chip.isChecked = true
            chip.isEnabled = false
            chip.isCheckedIconVisible = false

            // Add to ChipGroup
            flavorChipGroup!!.addView(chip)
        }
    }

    private fun refreshDates() {
        if (currentBrew.primaryFerment != null) {
            val primaryDatesString = (if (currentBrew.primaryFerment!!.first != null) TimeUtility.formatDateShort(currentBrew.primaryFerment!!.first!!) else "") +
                    " – " + if (currentBrew.primaryFerment!!.second != null) TimeUtility.formatDateShort(currentBrew.primaryFerment!!.second!!) else ""
            primaryDateTextView!!.text = primaryDatesString
            val primaryDays = TimeUtility.daysBetween(currentBrew.primaryFerment!!.first!!, currentBrew.primaryFerment!!.second!!)
            val primaryDaysString = resources.getQuantityString(R.plurals.pluralDays, primaryDays, primaryDays)
            primaryRemainingDaysTextView!!.text = primaryDaysString
        }

        if (currentBrew.secondaryFerment != null) {
            val secondaryDatesString = (if (currentBrew.secondaryFerment!!.first != null) TimeUtility.formatDateShort(currentBrew.secondaryFerment!!.first!!) else "") +
                    " – " + if (currentBrew.secondaryFerment!!.second != null) TimeUtility.formatDateShort(currentBrew.secondaryFerment!!.second!!) else ""
            secondaryDateTextView!!.text = secondaryDatesString
            if (currentBrew.secondaryFerment!!.second != null) {
                val secondaryDays = TimeUtility.daysBetween(currentBrew.secondaryFerment!!.first!!, currentBrew.secondaryFerment!!.second!!)
                val secondaryDaysString = resources.getQuantityString(R.plurals.pluralDays, secondaryDays, secondaryDays)
                secondaryRemainingDaysTextView!!.text = secondaryDaysString
            }
        }
    }

}
