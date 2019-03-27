package com.tobiasfried.brewkeeper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tobiasfried.brewkeeper.model.Brew;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryFragment extends Fragment {

    private static final String LOG_TAG = HistoryFragment.class.getSimpleName();

    private FirebaseFirestore db;

    private View rootView;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Brew, BrewViewHolder> mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.brew_list, container, false);

        return rootView;
    }
}
