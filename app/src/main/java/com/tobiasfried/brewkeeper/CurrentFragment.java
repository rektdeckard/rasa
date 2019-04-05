package com.tobiasfried.brewkeeper;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.tobiasfried.brewkeeper.constants.Stage;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.utils.TimeUtility;

import java.util.Objects;

import static com.tobiasfried.brewkeeper.EntryActivity.EXTRA_BREW_ID;

public class CurrentFragment extends Fragment {

    private static final String LOG_TAG = CurrentFragment.class.getSimpleName();

    private FirebaseFirestore db;
    private Brew deleted;

    private View rootView;
    @BindView(R.id.list) RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Brew, BrewViewHolder> mAdapter;

    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.array_sort_names, R.layout.spinner_item_sort);
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

        return rootView;
    }

    private void setupRecyclerView(String sortOption) {
        // Inflate and setup RecyclerView
        Query query = db.collection(Brew.CURRENT)
                .orderBy(sortOption, Query.Direction.ASCENDING);
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
                // Bind Views
                holder.bind(brew);

                // Set expander ClickListener
                holder.card.setOnClickListener(v -> {
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        if (i != position) {
                            BrewViewHolder vh = ((BrewViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i)));
                            vh.expanded = false;
                        }
                    }
                    holder.expanded = !holder.expanded;
                    notifyItemRangeChanged(0, mAdapter.getItemCount());
                });

                // Set quick action ClickListeners
                holder.details.setOnClickListener(v -> {
                    String brewId = getSnapshots().getSnapshot(position).getId();
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(EXTRA_BREW_ID, brewId);
                    startActivity(intent);
                    holder.expanded = false;
                    //notifyItemRangeChanged(0, mAdapter.getItemCount());
                });

                holder.markComplete.setOnClickListener(v -> {
                    String brewId = mAdapter.getSnapshots().getSnapshot(position).getId();
                    if (!brew.advanceStage()) {
                        brew.getSecondaryFerment().second = System.currentTimeMillis();
                        db.collection(Brew.HISTORY).add(brew);
                        deleted = mAdapter.getSnapshots().getSnapshot(position).toObject(Brew.class);
                        db.collection(Brew.CURRENT).document(brewId).delete();

                        // Show Snackbar with undo action
                        Snackbar.make(rootView, "Brew marked complete", Snackbar.LENGTH_INDEFINITE)
                                .setAction("UNDO", v2 -> {
                                    db.collection(Brew.CURRENT).add(deleted).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            deleted = null;
                                        }
                                    });
                                    // TODO get the correct history collection ID
                                    db.collection(Brew.HISTORY).document(brewId).delete();
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.white, Objects.requireNonNull(getActivity()).getTheme()))
                                .show();
                    } else {
                        db.collection(Brew.CURRENT).document(brewId).set(brew);
                    }
                    holder.expanded = false;
                    notifyItemChanged(position);
                });

                holder.delete.setOnClickListener(v -> {
                    String brewId = mAdapter.getSnapshots().getSnapshot(position).getId();
                    deleted = mAdapter.getSnapshots().getSnapshot(position).toObject(Brew.class);
                    db.collection(Brew.CURRENT).document(brewId).delete();

                    // Show Snackbar with undo action
                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_INDEFINITE)
                            .setAction("UNDO", h -> db.collection(Brew.CURRENT).add(deleted).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    deleted = null;
                                }
                            }))
                            .setActionTextColor(getResources().getColor(android.R.color.white, Objects.requireNonNull(getActivity()).getTheme()))
                            .show();

                    holder.expanded = false;
//                    notifyItemChanged(position);
                });

            }

            @Override
            public void onDataChanged() {
                notifyItemRangeChanged(0, mAdapter.getItemCount());
            }

        };

        // Attach Touch Listener
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
                    db.collection(Brew.CURRENT).document(brewId).delete();

                    // Show Snackbar with undo action
                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_INDEFINITE)
                            .setAction("UNDO", v -> db.collection(Brew.CURRENT).add(deleted).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    deleted = null;
                                }
                            }))
                            .setActionTextColor(getResources().getColor(android.R.color.white, Objects.requireNonNull(getActivity()).getTheme()))
                            .show();
                }
            }
        }).attachToRecyclerView(recyclerView);
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
