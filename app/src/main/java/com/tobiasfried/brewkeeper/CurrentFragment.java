package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.tobiasfried.brewkeeper.interfaces.OnRecyclerClickListener;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class CurrentFragment extends Fragment {

    private static final String LOG_TAG = CurrentFragment.class.getSimpleName();

    private FirebaseFirestore db;
    private Brew deleted;

    private BrewAdapter mAdapter;

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

        final View rootView = inflater.inflate(R.layout.brew_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        // Get token
        // MessageService.getInstanceId();

        // Inflate and setup RecyclerView
        Query query = db.collection(Brew.COLLECTION);
        FirestoreRecyclerOptions<Brew> options = new FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query, Brew.class)
                .setLifecycleOwner(this)
                .build();
        mAdapter = new BrewAdapter(options, getContext());

        // Attach Click Listener
        mAdapter.setOnRecyclerClickListener(new OnRecyclerClickListener() {
            @Override
            public void onItemClicked(int position, int id) {
                String brewId = mAdapter.getSnapshots().getSnapshot(position).getId();
                Intent intent = new Intent(getActivity(), EntryActivity.class);
                intent.putExtra(EntryActivity.EXTRA_BREW_ID, brewId);
                startActivity(intent);
            }
        });

        // Attach Touch Listener
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
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
        return recyclerView;
    }

}
