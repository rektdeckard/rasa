package com.tobiasfried.brewkeeper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tobiasfried.brewkeeper.data.AppDatabase;
import com.tobiasfried.brewkeeper.data.Brew;
import com.tobiasfried.brewkeeper.interfaces.OnRecyclerClickListener;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CurrentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CurrentFragment extends Fragment {

    private AppDatabase mDb;
    private List<Brew> mBrewList;

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

        // Get Database instance
        mDb = AppDatabase.getInstance(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate and setup RecyclerView
        View rootView = inflater.inflate(R.layout.brew_list, container, false);
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
                // intent.putExtra("id", mBrewList.get(id).getId());
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // Implement swipe actions
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            int position = viewHolder.getAdapterPosition();
                            List<Brew> brews = mAdapter.getBrews();
                            mDb.brewDao().deleteBrew(brews.get(position));
                            refreshViews();
                        }
                    });
                }
            }
        }).attachToRecyclerView(mRecyclerView);

        return mRecyclerView;

    }

    private void refreshViews() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mBrewList = mDb.brewDao().loadAllBrews();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setBrews(mBrewList);
                    }
                });
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
    public void onResume() {
        super.onResume();
        refreshViews();
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
