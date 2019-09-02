package com.tobiasfried.rasa.brews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tobiasfried.rasa.domain.Brew

class BrewsViewModel : ViewModel() {

    private var _brews = MutableLiveData<List<Brew>>()
    val brews: LiveData<List<Brew>>
        get() = _brews

    init {
        val database = FirebaseFirestore.getInstance()

        // Get current brews
        database.collection(Brew.CURRENT)
                .whereEqualTo("isRunning", true)
                .addSnapshotListener { queryDocumentSnapshots, e ->
                    if (queryDocumentSnapshots != null && e == null) {
                        _brews.value = queryDocumentSnapshots.map {
                            it.toObject(Brew::class.java)
                        }
                    }
                }
    }
}