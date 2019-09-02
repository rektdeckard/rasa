package com.tobiasfried.rasa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.google.firebase.firestore.FirebaseFirestore
import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.domain.Recipe

import java.util.ArrayList

class MainViewModel : ViewModel() {

    // Member LiveData
    private val currentBrews = MutableLiveData<List<Brew>>()
    private val completedBrews = MutableLiveData<List<Brew>>()
    private val recipes = MutableLiveData<List<Recipe>>()

    val brews: LiveData<List<Brew>>
        get() = currentBrews

    val history: LiveData<List<Brew>>
        get() = completedBrews

    // TODO is this needed anymore?? think about how to update brew stage based on time
    init {
        // Get DB Instance
        val database = FirebaseFirestore.getInstance()

        // Get Current Brews
        database.collection(Brew.CURRENT).whereEqualTo("isRunning", true).addSnapshotListener { queryDocumentSnapshots, e ->
            if (queryDocumentSnapshots != null && e == null) {
                val brews = ArrayList<Brew>()
                for (document in queryDocumentSnapshots) {
                    brews.add(document.toObject(Brew::class.java))
                }
                currentBrews.value = brews
            }
        }

        // Get Completed Brews
        database.collection(Brew.CURRENT).whereEqualTo("isRunning", false).addSnapshotListener { queryDocumentSnapshots, e ->
            if (queryDocumentSnapshots != null && e == null) {
                val brews = ArrayList<Brew>()
                for (document in queryDocumentSnapshots) {
                    brews.add(document.toObject(Brew::class.java))
                }
                completedBrews.value = brews
            }
        }

        // Get Recipes
        database.collection(Recipe.COLLECTION).addSnapshotListener { queryDocumentSnapshots, e ->
            if (queryDocumentSnapshots != null && e == null) {
                val r = ArrayList<Recipe>()
                for (document in queryDocumentSnapshots) {
                    r.add(document.toObject(Recipe::class.java))
                }
                recipes.value = r
            }
        }
    }

    fun getRecipes(): LiveData<List<Recipe>> {
        return recipes
    }

}
