package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tobiasfried.brewkeeper.model.Brew;
import com.tobiasfried.brewkeeper.interfaces.OnRecyclerClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CurrentFragment extends Fragment {

    private static final String LOG_TAG = CurrentFragment.class.getSimpleName();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference brewRef = db.collection(Brew.COLLECTION);
    private Brew deleted;

    private OnFragmentInteractionListener mListener;
    private BrewAdapter mAdapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Query query = brewRef.orderBy("name", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Brew> options = new FirestoreRecyclerOptions.Builder<Brew>()
                .setQuery(query, Brew.class)
                .build();
        mAdapter = new BrewAdapter(options);
        View rootView = inflater.inflate(R.layout.brew_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        return recyclerView;

//        // Inflate and setup RecyclerView
//        final View rootView = inflater.inflate(R.layout.brew_list, container, false);
//        RecyclerView mRecyclerView = rootView.findViewById(R.id.list);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        FirestoreRecyclerOptions<Brew> options = new FirestoreRecyclerOptions.Builder<Brew>()
//                .setLifecycleOwner(this)
//                .setQuery(db.collection(Brew.COLLECTION), Brew.class).build();
//        Log.i(LOG_TAG, db.collection(Brew.COLLECTION).get().toString());
//        mAdapter = new BrewAdapter(options);
//        mAdapter.setOnRecyclerClickListener(new OnRecyclerClickListener() {
//            @Override
//            public void onRecyclerViewItemClicked(int position, int id) {
//                String brewId = mAdapter.getSnapshots().getSnapshot(position).getId();
//                Intent intent = new Intent(getActivity(), EntryActivity.class);
//                intent.putExtra(EntryActivity.EXTRA_BREW_ID, brewId);
//                startActivity(intent);
//            }
//        });
//        mRecyclerView.setAdapter(mAdapter);
//
//        // Implement swipe actions
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
//                    String brewId = mAdapter.getSnapshots().getSnapshot(viewHolder.getAdapterPosition()).getId();
//                    deleted = db.collection(Brew.COLLECTION).document(brewId).get().getResult().toObject(Brew.class);
//                    db.collection(Brew.COLLECTION).document(brewId).delete();
//                    // Show Snackbar with undo action
//                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_LONG)
//                            .setAction("UNDO", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                   db.collection(Brew.COLLECTION).add(deleted).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                       @Override
//                                       public void onComplete(@NonNull Task<DocumentReference> task) {
//                                           if (task.isSuccessful()) {
//                                               deleted = null;
//                                           } else {
//                                               Log.i(LOG_TAG, "Failed to reinsert to the database");
//                                           }
//                                       }
//                                   });
//                                }
//                            })
//                            .setActionTextColor(getResources().getColor(R.color.colorAccent, getActivity().getTheme()))
//                            .show();
//                }
//            }
//        }).attachToRecyclerView(mRecyclerView);
//
//        return mRecyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
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
