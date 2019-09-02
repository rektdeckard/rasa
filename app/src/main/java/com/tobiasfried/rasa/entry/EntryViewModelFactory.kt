package com.tobiasfried.rasa.entry

import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EntryViewModelFactory(private val mDb: FirebaseFirestore, private val mCollection: String, private val mBrewId: String) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EntryViewModel(mDb, mCollection, mBrewId) as T
    }

}
