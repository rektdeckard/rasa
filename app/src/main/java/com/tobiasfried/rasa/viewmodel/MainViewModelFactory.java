package com.tobiasfried.rasa.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

	private FirebaseFirestore mDb;
	private MainViewModel mainViewModel;

	public MainViewModelFactory(FirebaseFirestore db) {
		this.mDb = db;
	}

	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new MainViewModel();
	}

	public MainViewModel getInstance() {
		if (mainViewModel != null) {
			return mainViewModel;
		} else {
			mainViewModel = create(MainViewModel.class);
			return mainViewModel;
		}
	}
}