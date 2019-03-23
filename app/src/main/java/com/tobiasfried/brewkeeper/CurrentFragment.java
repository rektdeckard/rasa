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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference brewRef = db.collection(Brew.COLLECTION);
    private Brew deleted;

    private OnFragmentInteractionListener mListener;
    private FirestoreRecyclerAdapter mAdapter;

    public CurrentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Database instance and retrieve brews
//        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.brew_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        // Get token
        // MessageService.getInstanceId();

        // Inflate and setup RecyclerView
        Query query = brewRef.orderBy("name").limit(10);
        FirestoreRecyclerOptions<Brew> options = new FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query, Brew.class)
                .setLifecycleOwner(this)
                .build();
        mAdapter = new FirestoreRecyclerAdapter<Brew, BrewViewHolder>(options) {
            @NonNull
            @Override
            public BrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
                Log.i(LOG_TAG, "onCreateViewHolder has run.");
                return new BrewViewHolder(itemView);
            }

            @Override
            public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
                Log.i(LOG_TAG, "Adapter attached to RecyclerView");
            }

            @Override
            protected void onBindViewHolder(@NonNull BrewViewHolder holder, int i, @NonNull Brew brew) {
                // Bind name and ColorStateList
                holder.name.setText(brew.getRecipe().getName());
                holder.progressBar.setProgressTintList(getContext().getColorStateList(R.color.color_states_progress));

                // Calculate and bind remaining days
                long endDate;
                if (brew.getStage() == Stage.PRIMARY) {
                    endDate = brew.getSecondaryStartDate();
                } else {
                    endDate = brew.getEndDate();
                }
                double days = ChronoUnit.DAYS.between(Instant.now(), Instant.ofEpochMilli(endDate));
                String remaining;
                if (days < 0) {
                    remaining = "Brew ended!";
                    holder.card.getLayoutParams().height = 50;
                } else if (days == 0) {
                    remaining = "Ending today";
                } else {
                    remaining = (int) days + " days remaining";
                }
                holder.remainingDays.setText(remaining);

                // Set progress indicators
                double totalDays;
                if (brew.getStage() == (Stage.PRIMARY)) {
                    holder.stage.setText(R.string.stage_primary);
                    totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(brew.getPrimaryStartDate()), Instant.ofEpochMilli(brew.getSecondaryStartDate()));
                } else {
                    holder.stage.setText(R.string.stage_secondary);
                    totalDays = ChronoUnit.DAYS.between(Instant.ofEpochMilli(brew.getSecondaryStartDate()), Instant.ofEpochMilli(brew.getEndDate()));
                }
                holder.progressBar.setProgress((int) (((totalDays - days) / totalDays) * 100));

                Log.i(LOG_TAG, "onBindViewHolder has run.");
            }

        };

        // Attach Click Listener
//        mAdapter.setOnRecyclerClickListener(new OnRecyclerClickListener() {
//            @Override
//            public void onItemClicked(int position, int id) {
//                String brewId = mAdapter.getSnapshots().getSnapshot(position).getId();
//                Intent intent = new Intent(getActivity(), EntryActivity.class);
//                intent.putExtra(EntryActivity.EXTRA_BREW_ID, brewId);
//                startActivity(intent);
//            }
//        });

                // Attach Touch Listener
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
//                if (direction == ItemTouchHelper.RIGHT) {
//                    // Move brew to recently deleted and delete it from database
//                    String brewId = mAdapter.getSnapshots().getSnapshot(viewHolder.getAdapterPosition());
//                    deleted = db.collection(Brew.COLLECTION).document(brewId).get().getResult().toObject(Brew.class);
//                    db.collection(Brew.COLLECTION).document(brewId).delete();
//                    // Show Snackbar with undo action
//                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_LONG)
//                            .setAction("UNDO", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    db.collection(Brew.COLLECTION).add(deleted).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<DocumentReference> task) {
//                                            if (task.isSuccessful()) {
//                                                deleted = null;
//                                            } else {
//                                                Log.i(LOG_TAG, "Failed to reinsert to the database");
//                                            }
//                                        }
//                                    });
//                                }
//                            })
//                            .setActionTextColor(getResources().getColor(R.color.colorAccent, getActivity().getTheme()))
//                            .show();
//                }
//            }
//        }).attachToRecyclerView(recyclerView);

                recyclerView.setAdapter(mAdapter);
        return recyclerView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
