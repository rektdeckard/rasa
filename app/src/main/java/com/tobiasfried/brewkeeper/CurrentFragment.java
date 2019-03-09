package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.tobiasfried.brewkeeper.data.AppDatabase;
import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.interfaces.OnRecyclerClickListener;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CurrentFragment extends Fragment {

    private AppDatabase mDb;
    private MainViewModel viewModel;
    private Deque<Brew> mDeletedList;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private BrewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public CurrentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Database instance and retrieve brews
        mDb = AppDatabase.getInstance(getContext());
        getViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate and setup RecyclerView
        final View rootView = inflater.inflate(R.layout.brew_list, container, false);
        mRecyclerView = rootView.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new BrewAdapter(getContext(), new OnRecyclerClickListener() {
            @Override
            public void onRecyclerViewItemClicked(int position, int id) {
                Intent intent = new Intent(getActivity(), EntryActivity.class);
                // TODO: prepopulate EntryActivity
                intent.putExtra(EntryActivity.EXTRA_BREW_ID, mAdapter.getBrews().get(position).getId());
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // Initialize temporary deleted deque
        mDeletedList = new ArrayDeque<>();

        // Implement swipe actions
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    // Move brew to recently deleted and delete it from database
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            int position = viewHolder.getAdapterPosition();
                            Brew deletedBrew = mAdapter.getBrews().get(position);
                            mDeletedList.addLast(deletedBrew);
                            mDb.brewDao().deleteBrew(deletedBrew);
                        }
                    });
                    // Show Snackbar with undo action
                    Snackbar.make(rootView, "Brew Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            mDb.brewDao().insertBrew(mDeletedList.poll());
                                        }
                                    });
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.colorAccent, getActivity().getTheme()))
                            .show();
                }
            }
        }).attachToRecyclerView(mRecyclerView);

        return mRecyclerView;

    }

    private void getViewModel() {
        // Get ViewModel and subscribe Observer
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getCurrentBrews().observe(this, new Observer<List<Brew>>() {
            @Override
            public void onChanged(List<Brew> brews) {
                mAdapter.setBrews(brews);
            }
        });
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
