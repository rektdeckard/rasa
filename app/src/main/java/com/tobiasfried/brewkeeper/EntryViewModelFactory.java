package com.tobiasfried.brewkeeper;

import com.tobiasfried.brewkeeper.data.AppDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EntryViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final long mBrewId;

    public EntryViewModelFactory(AppDatabase database, long brewId) {
        mDb = database;
        mBrewId = brewId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EntryViewModel(mDb, mBrewId);
    }

}
