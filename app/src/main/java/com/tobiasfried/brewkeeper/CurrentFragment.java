package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.messaging.MessageService;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.utils.TimeUtility;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.tobiasfried.brewkeeper.EntryActivity.EXTRA_BREW_ID;

public class CurrentFragment extends Fragment {

    private static final String LOG_TAG = CurrentFragment.class.getSimpleName();

    private FirebaseFirestore db;
    private Brew deleted;

    private View rootView;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Brew, BrewViewHolder> mAdapter;

    public CurrentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Database instance
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        // Get token
        // MessageService.getInstanceId();

        //setupRecyclerView("name");

        return rootView;
    }

    private void setupRecyclerView(String sortOption) {
        // Inflate and setup RecyclerView
        Query query = db.collection(Brew.COLLECTION).orderBy(sortOption, Query.Direction.ASCENDING);
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
                double days = TimeUtility.daysBetween(Instant.now().toEpochMilli(), endDate);
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

        // Attach Touch Listener
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    // Move brew to recently deleted and delete it from database
                    String brewId = mAdapter.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getId();
                    deleted = mAdapter.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).toObject(Brew.class);
                    db.collection(Brew.COLLECTION).document(brewId).delete();

                    // Show Snackbar with undo action
                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    db.collection(Brew.COLLECTION).add(deleted).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                deleted = null;
                                            } else {
                                                Log.i(LOG_TAG, "Failed to reinsert to the database");
                                            }
                                        }
                                    });
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorAccent, getActivity().getTheme()))
                            .show();
                }
            }
        }).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(mAdapter);
    }

}
