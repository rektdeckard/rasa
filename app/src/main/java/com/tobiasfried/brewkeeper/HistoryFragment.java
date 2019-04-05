package com.tobiasfried.brewkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.utils.TimeUtility;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.tobiasfried.brewkeeper.EntryActivity.EXTRA_BREW_ID_HISTORY;

public class HistoryFragment extends Fragment {

    private static final String LOG_TAG = HistoryFragment.class.getSimpleName();

    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Brew, HistoryViewHolder> mAdapter;
    private FirestoreRecyclerOptions<Brew> options;
    private String sortOptions;
    private Query.Direction sortOrder = Query.Direction.ASCENDING;
    private Unbinder unbinder;

    private View rootView;

    @BindView(R.id.list)
    RecyclerView recyclerView;

    @BindView(R.id.spinner_sort_by)
    Spinner sortSpinner;

    @BindView(R.id.button_sort_order)
    MaterialButton sortOrderButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Database instance
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_brews, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        // Set Spinner Adapter
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.array_sort_names_history, R.layout.spinner_item_sort);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setSelection(0);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortOptions = getResources().getStringArray(R.array.array_sort_options)[position];
                setupRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set Sort Order Button
        sortOrderButton.setOnClickListener(v -> {
            float rotation = v.getRotation();
            v.setRotation(rotation == 0 ? 180 : 0);
            sortOrder = rotation == 0 ? Query.Direction.ASCENDING : Query.Direction.DESCENDING;
            setupRecyclerView();
        });

        return rootView;
    }

    private void setupRecyclerView() {
        // Inflate and setup RecyclerView
        Query query = db.collection(Brew.HISTORY)
                .orderBy(sortOptions, sortOrder);
        FirestoreRecyclerOptions<Brew> options = new FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query, Brew.class)
                .setLifecycleOwner(this)
                .build();
        mAdapter = new FirestoreRecyclerAdapter<Brew, HistoryViewHolder>(options) {
            @NonNull
            @Override
            public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.list_item_history, parent, false);
                return new HistoryViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull HistoryViewHolder holder, int position, @NonNull Brew brew) {
                // Apply fields
                holder.name.setText(brew.getRecipe().getName());
                holder.date.setText(TimeUtility.formatDateShort(brew.getEndDate()));

                // Set ClickListener
                final String brewId = getSnapshots().getSnapshot(position).getId();
                holder.card.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), EntryActivity.class);
                    intent.putExtra(EXTRA_BREW_ID_HISTORY, brewId);
                    startActivity(intent);
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
