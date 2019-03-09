package com.tobiasfried.brewkeeper;

import android.app.Application;

import com.tobiasfried.brewkeeper.data.AppDatabase;
import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.data.Recipe;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    // Member LiveData
    private LiveData<List<Brew>> currentBrews;
    private LiveData<List<Brew>> completedBrews;
    private LiveData<List<Recipe>> recipes;

    public MainViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(getApplication());
        currentBrews = database.brewDao().getCurrentBrews();
        completedBrews = database.brewDao().getCompletedBrews();
        recipes = database.recipeDao().getAllRecipes();
    }

    public LiveData<List<Brew>> getCurrentBrews() {
        return currentBrews;
    }

    public LiveData<List<Brew>> getCompletedBrews() {
        return completedBrews;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

}
