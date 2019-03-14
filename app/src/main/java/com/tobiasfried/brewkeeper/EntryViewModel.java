package com.tobiasfried.brewkeeper;

import com.tobiasfried.brewkeeper.data.AppDatabase;
import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.data.Ingredient;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class EntryViewModel extends ViewModel {

    private LiveData<Brew> mBrew;
    private LiveData<List<Ingredient>> mTeas;
    private LiveData<List<Ingredient>> mFlavors;

    /**
     * ViewModel Constructor
     * @param database DB instance
     * @param brewId ID of edited Brew
     */
    public EntryViewModel(AppDatabase database, long brewId) {
        mBrew = database.brewDao().getBrew(brewId);
        mTeas = database.ingredientDao().getAllTeas();
        mFlavors = database.ingredientDao().getAllFlavors();
    }

    public LiveData<Brew> getBrew() {
        return mBrew;
    }

    public LiveData<List<Ingredient>> getTeas() {
        return mTeas;
    }

    public LiveData<List<Ingredient>> getFlavors() {
        return mFlavors;
    }
}
