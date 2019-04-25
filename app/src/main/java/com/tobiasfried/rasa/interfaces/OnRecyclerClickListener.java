package com.tobiasfried.rasa.interfaces;

public interface OnRecyclerClickListener {

    /**
     * Click listener that all RecyclerView ViewHolders should use to handle clicks
     * @param position within the RecyclerView
     * @param brewId returns id of the item within the ViewHolder, or null
     */
    void onItemClicked(int position, String brewId);

}
