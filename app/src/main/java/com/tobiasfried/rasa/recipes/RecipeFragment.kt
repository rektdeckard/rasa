package com.tobiasfried.rasa.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.tobiasfried.rasa.R

import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.domain.Recipe

class RecipeFragment : Fragment() {

    private var db: FirebaseFirestore? = null
    private var mAdapter: FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>? = null
    private var options: FirestoreRecyclerOptions<Recipe>? = null
    private val sortOptions: String? = null
    private val sortOrder = Query.Direction.ASCENDING
    private var unbinder: Unbinder? = null

    private var rootView: View? = null

    @BindView(R.id.list)
    internal var recyclerView: RecyclerView? = null

    @BindView(R.id.button_dummy)
    internal var dummyButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get database instance
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        rootView = inflater.inflate(R.layout.fragment_recipes, container, false)
        unbinder = ButterKnife.bind(this, rootView!!)
        recyclerView!!.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        setupRecyclerView()

        // TODO remove dummmy add button
        dummyButton!!.setOnClickListener { v ->
            val r = db!!.collection(Brew.HISTORY).document().id
            db!!.collection(Brew.HISTORY).whereGreaterThan(FieldPath.documentId(), r).limit(1).get().addOnCompleteListener { task ->
                if (task.isSuccessful && !task.result!!.isEmpty) {
                    val l = task.result!!.toObjects(Brew::class.java)
                    db!!.collection(Recipe.COLLECTION).add(l[0].recipe)
                }
            }
        }

        return rootView
    }

    private fun setupRecyclerView() {
        // Inflate and setup RecyclerView
        val query = db!!.collection(Recipe.COLLECTION)
        //.orderBy(sortOptions, sortOrder);
        options = FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe::class.java)
                .setLifecycleOwner(this)
                .build()
        mAdapter = object : FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>(options!!) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
                val itemView = layoutInflater.inflate(R.layout.list_item_recipe, parent, false)
                return RecipeViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: RecipeViewHolder, position: Int, recipe: Recipe) {
                holder.bind(recipe)

                // TODO remove dummy delete method
                holder.nameTextView!!.setOnLongClickListener { v ->
                    val id = mAdapter!!.snapshots.getSnapshot(position).id
                    db!!.collection(Recipe.COLLECTION).document(id).delete()
                    true
                }
            }

        }

        recyclerView!!.adapter = mAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    companion object {

        private val LOG_TAG = RecipeFragment::class.java.simpleName
    }
}
