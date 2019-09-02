package com.tobiasfried.rasa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.google.firebase.firestore.FirebaseFirestore

class MainViewModelFactory(private val mDb: FirebaseFirestore) : ViewModelProvider.NewInstanceFactory() {
    private var mainViewModel: MainViewModel? = null

    val instance: MainViewModel
        get() {
            if (mainViewModel != null) {
                return mainViewModel
            } else {
                mainViewModel = create(MainViewModel::class.java)
                return mainViewModel
            }
        }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel() as T
    }
}