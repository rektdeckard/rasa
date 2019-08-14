package com.tobiasfried.rasa.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tobiasfried.rasa.model.Brew;
import com.tobiasfried.rasa.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

	private static final String LOG_TAG = MainViewModel.class.getSimpleName();

	// Member LiveData
	private MutableLiveData<List<Brew>> currentBrews = new MutableLiveData<>();
	private MutableLiveData<List<Brew>> completedBrews = new MutableLiveData<>();
	private MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();

	// TODO is this needed anymore?? think about how to update brew stage based on time
	public MainViewModel() {
		// Get DB Instance
		FirebaseFirestore database = FirebaseFirestore.getInstance();

		// Get Current Brews
		database.collection(Brew.CURRENT).whereEqualTo("isRunning", true).addSnapshotListener((queryDocumentSnapshots, e) -> {
			if (queryDocumentSnapshots != null && e == null) {
				List<Brew> brews = new ArrayList<>();
				for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
					brews.add(document.toObject(Brew.class));
				}
				currentBrews.setValue(brews);
			}
		});

		// Get Completed Brews
		database.collection(Brew.CURRENT).whereEqualTo("isRunning", false).addSnapshotListener((queryDocumentSnapshots, e) -> {
			if (queryDocumentSnapshots != null && e == null) {
				List<Brew> brews = new ArrayList<>();
				for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
					brews.add(document.toObject(Brew.class));
				}
				completedBrews.setValue(brews);
			}
		});

		// Get Recipes
		database.collection(Recipe.COLLECTION).addSnapshotListener((queryDocumentSnapshots, e) -> {
			if (queryDocumentSnapshots != null && e == null) {
				List<Recipe> r = new ArrayList<>();
				for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
					r.add(document.toObject(Recipe.class));
				}
				recipes.setValue(r);
			}
		});
	}

	public LiveData<List<Brew>> getBrews() {
		return currentBrews;
	}

	public LiveData<List<Brew>> getHistory() {
		return completedBrews;
	}

	public LiveData<List<Recipe>> getRecipes() {
		return recipes;
	}

}
