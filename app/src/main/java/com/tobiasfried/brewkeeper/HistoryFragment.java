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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tobiasfried.brewkeeper.constants.Stage;
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

import static com.tobiasfried.brewkeeper.EntryActivity.EXTRA_BREW_ID_HISTORY;

public class HistoryFragment extends Fragment {

    private static final String LOG_TAG = HistoryFragment.class.getSimpleName();

    private FirebaseFirestore db;

    private View rootView;
    @BindView(R.id.list) RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Brew, HistoryViewHolder> mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Database instance
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.brew_list, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()), R.array.array_sort_names, R.layout.spinner_item_sort);
        Spinner sortSpinner = rootView.findViewById(R.id.spinner_sort_by);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setSelection(0);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setupRecyclerView(getResources().getStringArray(R.array.array_sort_options)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    private void setupRecyclerView(String sortOption) {
        // Inflate and setup RecyclerView
        Query query = db.collection(Brew.HISTORY)
                .orderBy(sortOption, Query.Direction.ASCENDING);
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
}
