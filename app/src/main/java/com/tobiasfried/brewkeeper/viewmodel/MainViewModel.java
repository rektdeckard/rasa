package com.tobiasfried.brewkeeper.viewmodel;

import android.app.Application;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.model.Recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    // Member LiveData
    private MutableLiveData<List<Brew>> currentBrews = new MutableLiveData<>();
    private MutableLiveData<List<Brew>> completedBrews = new MutableLiveData<>();
    private MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);
        // Get DB Instance
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Get Current Brews
        database.collection(Brew.CURRENT).whereEqualTo("isRunning", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && e == null) {
                    List<Brew> brews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        brews.add(document.toObject(Brew.class));
                    }
                    currentBrews.setValue(brews);
                }
            }
        });

        // Get Completed Brews
        database.collection(Brew.CURRENT).whereEqualTo("isRunning", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && e == null) {
                    List<Brew> brews = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        brews.add(document.toObject(Brew.class));
                    }
                    completedBrews.setValue(brews);
                }
            }
        });

        // Get Recipes
        database.collection(Recipe.COLLECTION).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && e == null) {
                    List<Recipe> r = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        r.add(document.toObject(Recipe.class));
                    }
                    recipes.setValue(r);
                }
            }
        });
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
