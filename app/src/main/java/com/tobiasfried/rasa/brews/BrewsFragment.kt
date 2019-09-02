package com.tobiasfried.rasa.brews

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import butterknife.BindView
import butterknife.Unbinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.MainViewModel
import com.tobiasfried.rasa.MainViewModelFactory

import java.util.Objects

import com.tobiasfried.rasa.entry.EntryExtendedActivity
import com.tobiasfried.rasa.R
import com.tobiasfried.rasa.databinding.FragmentBrewsBinding

class BrewsFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private var mAdapter: FirestoreRecyclerAdapter<Brew, BrewViewHolder>? = null
    private var options: FirestoreRecyclerOptions<Brew>? = null
    private var query: Query? = null
    private var sortOptions: String? = null
    private var sortOrder: Query.Direction = Query.Direction.ASCENDING
    private var deleted: Brew? = null
    private var unbinder: Unbinder? = null

    private var rootView: View? = null

    @BindView(R.id.button_sort_order)
    internal var sortOrderButton: MaterialButton? = null

    private lateinit var binding: FragmentBrewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get Database instance
        db = FirebaseFirestore.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBrewsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        val factory = MainViewModelFactory(db)
        binding.viewModel = ViewModelProviders.of(this, factory).get(BrewsViewModel::class.java)

        binding.list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        // Set Spinner Adapter
        val sortAdapter = ArrayAdapter.createFromResource(context,
                R.array.array_sort_names, R.layout.spinner_item_sort)
        binding.spinnerSortBy.adapter = sortAdapter
        binding.spinnerSortBy.setSelection(0)
        binding.spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        query = db!!.collection(Brew.CURRENT)
                .orderBy(sortOptions!!, sortOrder)

        options = FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query!!, Brew::class.java)
                .setLifecycleOwner(this)
                .build()

        mAdapter = object : FirestoreRecyclerAdapter<Brew, BrewViewHolder>(options!!) {

            private var expandedItemIndex = -1

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrewViewHolder {
                val itemView = layoutInflater.inflate(R.layout.list_item_brew, parent, false)
                return BrewViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: BrewViewHolder, position: Int, brew: Brew) {
                // Bind Views
                holder.bind(brew)

                // Set expander ClickListener
                holder.card!!.setOnClickListener { v ->
                    if (holder.adapterPosition == expandedItemIndex) {
                        notifyItemChanged(holder.adapterPosition)
                        expandedItemIndex = -1
                    } else {
                        if (expandedItemIndex != -1) {
                            notifyItemChanged(expandedItemIndex)
                        }
                        expandedItemIndex = holder.adapterPosition
                        notifyItemChanged(holder.adapterPosition)
                    }
                }

                if (position == expandedItemIndex) {
                    holder.quickActions!!.visibility = View.VISIBLE
                } else {
                    holder.quickActions!!.visibility = View.GONE
                }

                // Set quick action ClickListeners
                holder.details!!.setOnClickListener { v ->
                    val brewId = snapshots.getSnapshot(holder.adapterPosition).id
                    val intent = Intent(activity, EntryExtendedActivity::class.java)
                    intent.putExtra(EXTRA_BREW_ID, brewId)
                    startActivity(intent)
                    holder.expanded = false
                    //notifyItemRangeChanged(0, mAdapter.getItemCount());
                }

                holder.markComplete!!.setOnClickListener { v ->
                    val brewId = mAdapter!!.snapshots.getSnapshot(holder.adapterPosition).id
                    if (!brew.advanceStage()) {
                        brew.secondaryFerment!!.second = System.currentTimeMillis()
                        db.collection(Brew.HISTORY).add(brew)
                        deleted = mAdapter!!.snapshots.getSnapshot(holder.adapterPosition).toObject(Brew::class.java)
                        db.collection(Brew.CURRENT).document(brewId).delete()

                        // Show Snackbar with undo action
                        Snackbar.make(binding.root, "Brew marked complete", Snackbar.LENGTH_LONG)
                                .setAction("UNDO") {
                                    db.collection(Brew.CURRENT).add(deleted!!).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            deleted = null
                                        }
                                    }
                                    // TODO get the correct history collection ID
                                    db.collection(Brew.HISTORY).document(brewId).delete()
                                }
                                .setActionTextColor(resources.getColor(android.R.color.white, Objects.requireNonNull<FragmentActivity>(activity).getTheme()))
                                .show()
                    } else {
                        db!!.collection(Brew.CURRENT).document(brewId).set(brew)
                    }
                    holder.expanded = false
                    notifyItemChanged(holder.adapterPosition)
                }

                holder.delete!!.setOnClickListener { v ->
                    val brewId = mAdapter!!.snapshots.getSnapshot(holder.adapterPosition).id
                    deleted = mAdapter!!.snapshots.getSnapshot(holder.adapterPosition).toObject(Brew::class.java)
                    db!!.collection(Brew.CURRENT).document(brewId).delete()

                    // Show Snackbar with undo action
                    Snackbar.make(rootView!!, "Brew Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") { h ->
                                db!!.collection(Brew.CURRENT).add(deleted!!).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        deleted = null
                                    }
                                }
                            }
                            .setActionTextColor(resources.getColor(android.R.color.white, Objects.requireNonNull<FragmentActivity>(activity).getTheme()))
                            .show()

                    holder.expanded = false
                    expandedItemIndex = -1
                    notifyItemRangeChanged(0, mAdapter!!.itemCount)
                }

            }

            override fun onViewRecycled(holder: BrewViewHolder) {
                super.onViewRecycled(holder)
                holder.expanded = false
            }

        }

        // Attach Touch Listener
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    // Move brew to recently deleted and delete it from database
                    val brewId = mAdapter!!.snapshots.getSnapshot(viewHolder.adapterPosition).id
                    deleted = mAdapter!!.snapshots.getSnapshot(viewHolder.adapterPosition).toObject(Brew::class.java)
                    // TODO delete view MainViewModel
                    db!!.collection(Brew.CURRENT).document(brewId).delete()

                    // Show Snackbar with undo action
                    Snackbar.make(rootView!!, "Brew Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") { v ->
                                db!!.collection(Brew.CURRENT).add(deleted!!).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        deleted = null
                                    }
                                }
                            }
                            .setActionTextColor(resources.getColor(android.R.color.white, Objects.requireNonNull<FragmentActivity>(activity).getTheme()))
                            .show()
                }
            }
        }).attachToRecyclerView(recyclerView)
        (Objects.requireNonNull<ItemAnimator>(recyclerView!!.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations = false

        recyclerView!!.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    companion object {

        private val LOG_TAG = BrewsFragment::class.java.simpleName
    }
}// Required empty public constructor