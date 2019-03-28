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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.utils.TimeUtility;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.tobiasfried.brewkeeper.EntryActivity.EXTRA_BREW_ID;

public class HistoryFragment extends Fragment {

    private static final String LOG_TAG = HistoryFragment.class.getSimpleName();

    private FirebaseFirestore db;
    private Brew deleted;

    private View rootView;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Brew, BrewViewHolder> mAdapter;

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
        recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(), R.array.array_sort_names, R.layout.spinner_item_sort);
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
        Query query = db.collection(Brew.COLLECTION)
                //.whereEqualTo("stage", Stage.COMPLETE)
                .whereEqualTo("running", false);
                //.orderBy(sortOption, Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Brew> options = new FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query, Brew.class)
                .setLifecycleOwner(this)
                .build();
        mAdapter = new FirestoreRecyclerAdapter<Brew, BrewViewHolder>(options) {
            @NonNull
            @Override
            public BrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.list_item_card, parent, false);
                return new BrewViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull BrewViewHolder holder, int position, @NonNull Brew brew) {
                // Bind name and ColorStateList
                holder.name.setText(brew.getRecipe().getName());

                // Calculate and bind remaining days
                long endDate;
                if (brew.getStage() == Stage.PRIMARY) {
                    endDate = brew.getSecondaryStartDate();
                } else {
                    endDate = brew.getEndDate();
                }
                double days = TimeUtility.daysBetween(System.currentTimeMillis(), endDate);
                String remainingString;
                if (days <= 0) {
                    remainingString = "Complete";
                    holder.card.getLayoutParams().height = 140;
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    holder.remainingDays.setVisibility(View.INVISIBLE);
                    holder.check.setVisibility(View.VISIBLE);
                    holder.name.setTextColor(getResources().getColor(android.R.color.white, getContext().getTheme()));
                    holder.stage.setTextColor(getResources().getColor(android.R.color.white, getContext().getTheme()));
                } else if (days == 1) {
                    remainingString = "Ending tomorrow";
                } else {
                    remainingString = getResources().getQuantityString(R.plurals.pluralDays, (int) days, (int) days) + " left";
                }
                holder.remainingDays.setText(remainingString);

                // Set progress indicators
                double totalDays;
                if (brew.getStage() == (Stage.PRIMARY)) {
                    holder.stage.setText(R.string.stage_primary);
                    totalDays = TimeUtility.daysBetween(brew.getPrimaryStartDate(), brew.getSecondaryStartDate());
                } else {
                    holder.stage.setText(R.string.stage_secondary);
                    totalDays = TimeUtility.daysBetween(brew.getSecondaryStartDate(), brew.getEndDate());
                }
                int progress = (int) (((totalDays - days) / totalDays) * 100);
                holder.progressBar.setProgress(progress);
                holder.progressBar.setSecondaryProgress(progress + 1);

                // Set ClickListener
                final String brewId = getSnapshots().getSnapshot(position).getId();
                holder.card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EntryActivity.class);
                        intent.putExtra(EXTRA_BREW_ID, brewId);
                        startActivity(intent);
                    }
                });
            }

        };

        recyclerView.setAdapter(mAdapter);
    }
}
