package com.tobiasfried.rasa.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.utils.TimeUtility

import java.util.Objects
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tobiasfried.rasa.DetailActivity
import com.tobiasfried.rasa.R
import com.tobiasfried.rasa.entry.EntryActivity.Companion.EXTRA_BREW_ID_HISTORY

class HistoryFragment : Fragment() {

    private var db: FirebaseFirestore? = null
    private var mAdapter: FirestoreRecyclerAdapter<Brew, HistoryViewHolder>? = null
    private val options: FirestoreRecyclerOptions<Brew>? = null
    private var sortOptions: String? = null
    private var sortOrder: Query.Direction = Query.Direction.DESCENDING
    private var unbinder: Unbinder? = null

    private var rootView: View? = null

    @BindView(R.id.list)
    internal var recyclerView: RecyclerView? = null

    @BindView(R.id.spinner_sort_by)
    internal var sortSpinner: Spinner? = null

    @BindView(R.id.button_sort_order)
    internal var sortOrderButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Database instance
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        rootView = inflater.inflate(R.layout.fragment_history, container, false)
        unbinder = ButterKnife.bind(this, rootView!!)
        recyclerView!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        // Set Spinner Adapter
        val sortAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull<Context>(context),
                R.array.array_sort_names_history, R.layout.spinner_item_sort)
        sortSpinner!!.adapter = sortAdapter
        sortSpinner!!.setSelection(0)
        sortSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                sortOptions = resources.getStringArray(R.array.array_sort_options)[position]
                setupRecyclerView()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        // Set Sort Order Button
        sortOrderButton!!.setOnClickListener { v ->
            val rotation = v.rotation
            v.rotation = (if (rotation == 0f) 180 else 0).toFloat()
            sortOrder = if (rotation == 0f) Query.Direction.ASCENDING else Query.Direction.DESCENDING
            setupRecyclerView()
        }

        return rootView
    }

    private fun setupRecyclerView() {
        // Inflate and setup RecyclerView
        val query = db!!.collection(Brew.HISTORY)
                .orderBy(sortOptions!!, sortOrder)
        val options = FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query, Brew::class.java)
                .setLifecycleOwner(this)
                .build()
        mAdapter = object : FirestoreRecyclerAdapter<Brew, HistoryViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
                val itemView = layoutInflater.inflate(R.layout.list_item_history, parent, false)
                return HistoryViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: HistoryViewHolder, position: Int, brew: Brew) {
                // Apply fields
                holder.name!!.text = brew.recipe?.name
                holder.date!!.text = TimeUtility.formatDateShort(brew.endDate)

                // Set ClickListener
                val brewId = snapshots.getSnapshot(position).id
                holder.card!!.setOnClickListener { v ->
                    val intent = Intent(activity, DetailActivity::class.java)
                    intent.putExtra(EXTRA_BREW_ID_HISTORY, brewId)
                    startActivity(intent)
                }
            }
        }

        recyclerView!!.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

}
