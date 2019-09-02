package com.tobiasfried.rasa.entry

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.tobiasfried.rasa.constants.IngredientType
import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.domain.Ingredient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EntryViewModel
/**
 * ViewModel Constructor
 * @param database DB instance
 * @param brewId ID of edited Brew
 */
(database: FirebaseFirestore, c: String, brewId: String?) : ViewModel() {

    //

    // Getters

    var documentReference: DocumentReference? = null
        private set

    private val mBrew = MutableLiveData<Brew>()
    private val mTeas = MutableLiveData<List<Ingredient>>()
    private val mFlavors = MutableLiveData<List<Ingredient>>()

    val brew: LiveData<Brew>
        get() = mBrew

    val teas: LiveData<List<Ingredient>>
        get() = mTeas

    val flavors: LiveData<List<Ingredient>>
        get() = mFlavors

    init {
        // Get Brew
        if (brewId == null) {
            documentReference = database.collection(c).document()
            mBrew.setValue(Brew())
        } else {
            documentReference = database.collection(c).document(brewId)
            documentReference!!.get().addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    mBrew.value = task.result!!.toObject(Brew::class.java)
                }
            }
        }
        // Get all Teas
        database.collection(Ingredient.COLLECTION)
                .whereEqualTo("type", IngredientType.TEA)
                .addSnapshotListener { queryDocumentSnapshots, e ->
                    if (queryDocumentSnapshots != null && e == null) {
                        mTeas.value = queryDocumentSnapshots.toObjects(Ingredient::class.java)
                    }
                }
        // Get all Ingredients
        database.collection(Ingredient.COLLECTION)
                .whereEqualTo("type", IngredientType.FLAVOR)
                .addSnapshotListener { queryDocumentSnapshots, e ->
                    if (queryDocumentSnapshots != null && e == null) {
                        mFlavors.value = queryDocumentSnapshots.toObjects(Ingredient::class.java)
                    }
                }
    }

    companion object {

        private val LOG_TAG = EntryViewModel::class.java.simpleName
    }

}
