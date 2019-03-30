package com.tobiasfried.brewkeeper.viewmodel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tobiasfried.brewkeeper.constants.IngredientType;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.model.Ingredient;

import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EntryViewModel extends ViewModel {

    private static final String LOG_TAG = EntryViewModel.class.getSimpleName();

    private DocumentReference mDocRef;

    private MutableLiveData<Brew> mBrew = new MutableLiveData<>();
    private MutableLiveData<List<Ingredient>> mTeas = new MutableLiveData<>();
    private MutableLiveData<List<Ingredient>> mFlavors = new MutableLiveData<>();

    /**
     * ViewModel Constructor
     * @param database DB instance
     * @param brewId ID of edited Brew
     */
    public EntryViewModel(FirebaseFirestore database, String c, String brewId) {
        // Get Brew
        if (brewId == null) {
            mDocRef = database.collection(c).document();
            mBrew.setValue(new Brew());
        } else {
            mDocRef = database.collection(c).document(brewId);
            mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        mBrew.setValue(task.getResult().toObject(Brew.class));
                    }
                }
            });
        }
        // Get all Teas
        database.collection(Ingredient.COLLECTION)
                .whereEqualTo("type", IngredientType.TEA)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && e == null) {
                    mTeas.setValue(queryDocumentSnapshots.toObjects(Ingredient.class));
                }
            }
        });
        // Get all Ingredients
        database.collection(Ingredient.COLLECTION)
                .whereEqualTo("type", IngredientType.FLAVOR)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && e == null) {
                   mFlavors.setValue(queryDocumentSnapshots.toObjects(Ingredient.class));
                }
            }
        });
    }

    //

    // Getters

    public DocumentReference getDocumentReference() {
        return mDocRef;
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
