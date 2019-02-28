package com.tobiasfried.brewkeeper.interfaces;

public interface OnRecyclerClickListener {

    /**
     * Click listener that all RecyclerView ViewHolders should use to handle clicks
     * @param position within the RecyclerView
     * @param id returns id of the item within the ViewHolder, or -1 if the ViewHolder itself
     */
    void onRecyclerViewItemClicked(int position, int id);

}
