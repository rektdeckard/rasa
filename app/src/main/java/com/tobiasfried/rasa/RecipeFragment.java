package com.tobiasfried.rasa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.tobiasfried.rasa.model.Brew;
import com.tobiasfried.rasa.model.Recipe;

public class RecipeFragment extends Fragment {

    private static final String LOG_TAG = RecipeFragment.class.getSimpleName();

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Recipe, RecipeViewHolder> mAdapter;
    private FirestoreRecyclerOptions<Recipe> options;
    private String sortOptions;
    private Query.Direction sortOrder = Query.Direction.ASCENDING;
    private Unbinder unbinder;

    private View rootView;

    @BindView(R.id.list)
    RecyclerView recyclerView;

    @BindView(R.id.button_dummy)
    MaterialButton dummyButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get database instance
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        setupRecyclerView();

        // TODO remove dummmy add button
        dummyButton.setOnClickListener(v -> {
            String r = db.collection(Brew.HISTORY).document().getId();
            db.collection(Brew.HISTORY).whereGreaterThan(FieldPath.documentId(), r).limit(1).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    List<Brew> l = task.getResult().toObjects(Brew.class);
                    db.collection(Recipe.COLLECTION).add(l.get(0).getRecipe());
                }
            });
        });

        return rootView;
    }

    private void setupRecyclerView() {
        // Inflate and setup RecyclerView
        Query query = db.collection(Recipe.COLLECTION);
                //.orderBy(sortOptions, sortOrder);
        options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe.class)
                .setLifecycleOwner(this)
                .build();
        mAdapter = new FirestoreRecyclerAdapter<Recipe, RecipeViewHolder>(options) {

            @NonNull
            @Override
            public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.list_item_recipe, parent, false);
                return new RecipeViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull RecipeViewHolder holder, int position, @NonNull Recipe recipe) {
                holder.bind(recipe);

                // TODO remove dummy delete method
                holder.nameTextView.setOnLongClickListener(v -> {
                    String id = mAdapter.getSnapshots().getSnapshot(position).getId();
                    db.collection(Recipe.COLLECTION).document(id).delete();
                    return true;
                });
            }

        };

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
