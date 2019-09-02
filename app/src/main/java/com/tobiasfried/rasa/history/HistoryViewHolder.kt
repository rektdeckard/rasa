package com.tobiasfried.rasa.history

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife

class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Member views
    @BindView(R.id.history_card)
    internal var card: LinearLayout? = null
    @BindView(R.id.text_view_name)
    internal var name: TextView? = null
    @BindView(R.id.text_view_date)
    internal var date: TextView? = null

    init {
        ButterKnife.bind(this, itemView)

    }
}
