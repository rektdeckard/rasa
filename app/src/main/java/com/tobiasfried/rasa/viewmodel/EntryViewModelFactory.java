package com.tobiasfried.rasa.viewmodel;

import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final FirebaseFirestore mDb;
    private final String mCollection;
    private final String mBrewId;

    public EntryViewModelFactory(FirebaseFirestore database, String collection, String brewId) {
        mDb = database;
        mCollection = collection;
        mBrewId = brewId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EntryViewModel(mDb, mCollection, mBrewId);
    }

}
