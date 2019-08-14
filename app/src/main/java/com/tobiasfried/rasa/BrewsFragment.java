package com.tobiasfried.rasa;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.tobiasfried.rasa.model.Brew;
import com.tobiasfried.rasa.viewmodel.MainViewModel;
import com.tobiasfried.rasa.viewmodel.MainViewModelFactory;

import java.util.Objects;

import static com.tobiasfried.rasa.EntryActivity.EXTRA_BREW_ID;

public class BrewsFragment extends Fragment {

    private static final String LOG_TAG = BrewsFragment.class.getSimpleName();

    private MainViewModel viewModel;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter<Brew, BrewViewHolder> mAdapter;
    private FirestoreRecyclerOptions<Brew> options;
    private Query query;
    private String sortOptions;
    private Query.Direction sortOrder = Query.Direction.ASCENDING;
    private Brew deleted;
    private Unbinder unbinder;

    private View rootView;

    @BindView(R.id.list)
    RecyclerView recyclerView;

    @BindView(R.id.spinner_sort_by)
    Spinner sortSpinner;

    @BindView(R.id.button_sort_order)
    MaterialButton sortOrderButton;

    public BrewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Database instance
        db = FirebaseFirestore.getInstance();
        MainViewModelFactory factory = new MainViewModelFactory(db);
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_brews, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        // Set Spinner Adapter
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(Objects.requireNonNull(getContext()),
                R.array.array_sort_names, R.layout.spinner_item_sort);
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
        query = db.collection(Brew.CURRENT)
                .orderBy(sortOptions, sortOrder);

        options = new FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query, Brew.class)
                .setLifecycleOwner(this)
                .build();

        mAdapter = new FirestoreRecyclerAdapter<Brew, BrewViewHolder>(options) {

            private int expandedItemIndex = -1;

            @NonNull
            @Override
            public BrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.list_item_brew, parent, false);
                return new BrewViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull BrewViewHolder holder, int position, @NonNull Brew brew) {
                // Bind Views
                holder.bind(brew);

                // Set expander ClickListener
                holder.card.setOnClickListener(v -> {
                    if (holder.getAdapterPosition() == expandedItemIndex) {
                        notifyItemChanged(holder.getAdapterPosition());
                        expandedItemIndex = -1;
                    } else {
                        if (expandedItemIndex != -1) {
                            notifyItemChanged(expandedItemIndex);
                        }
                        expandedItemIndex = holder.getAdapterPosition();
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                });

                if (position == expandedItemIndex) {
                    holder.quickActions.setVisibility(View.VISIBLE);
                } else {
                    holder.quickActions.setVisibility(View.GONE);
                }

                // Set quick action ClickListeners
                holder.details.setOnClickListener(v -> {
                    String brewId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                    Intent intent = new Intent(getActivity(), EntryExtendedActivity.class);
                    intent.putExtra(EXTRA_BREW_ID, brewId);
                    startActivity(intent);
                    holder.expanded = false;
                    //notifyItemRangeChanged(0, mAdapter.getItemCount());
                });

                holder.markComplete.setOnClickListener(v -> {
                    String brewId = mAdapter.getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                    if (!brew.advanceStage()) {
                        brew.getSecondaryFerment().second = System.currentTimeMillis();
                        db.collection(Brew.HISTORY).add(brew);
                        deleted = mAdapter.getSnapshots().getSnapshot(holder.getAdapterPosition()).toObject(Brew.class);
                        db.collection(Brew.CURRENT).document(brewId).delete();

                        // Show Snackbar with undo action
                        Snackbar.make(rootView, "Brew marked complete", Snackbar.LENGTH_LONG)
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
                    notifyItemChanged(holder.getAdapterPosition());
                });

                holder.delete.setOnClickListener(v -> {
                    String brewId = mAdapter.getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();
                    deleted = mAdapter.getSnapshots().getSnapshot(holder.getAdapterPosition()).toObject(Brew.class);
                    db.collection(Brew.CURRENT).document(brewId).delete();

                    // Show Snackbar with undo action
                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", h -> db.collection(Brew.CURRENT).add(deleted).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    deleted = null;
                                }
                            }))
                            .setActionTextColor(getResources().getColor(android.R.color.white, Objects.requireNonNull(getActivity()).getTheme()))
                            .show();

                    holder.expanded = false;
                    expandedItemIndex = -1;
                    notifyItemRangeChanged(0, mAdapter.getItemCount());
                });

            }

            @Override
            public void onViewRecycled(@NonNull BrewViewHolder holder) {
                super.onViewRecycled(holder);
                holder.expanded = false;
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
                    // TODO delete view MainViewModel
                    db.collection(Brew.CURRENT).document(brewId).delete();

                    // Show Snackbar with undo action
                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_LONG)
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