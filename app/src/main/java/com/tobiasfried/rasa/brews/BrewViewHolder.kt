package com.tobiasfried.rasa.brews

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.tobiasfried.rasa.domain.Brew
import com.tobiasfried.rasa.utils.TimeUtility
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.tobiasfried.rasa.R
import com.tobiasfried.rasa.constants.Stage

class BrewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var expanded = false
    private val MIN_PROGRESS = 19
    private val expandedParams: LinearLayout.LayoutParams
    private val collapsedParams: LinearLayout.LayoutParams

    //    private float heightExpanded = TypedValue.complexToDimension((int) itemView.getContext().getResources().getDimension(R.dimen.list_item_height),
    //            itemView.getResources().getDisplayMetrics());
    //    private float heightCollapsed = TypedValue.complexToDimension((int) itemView.getContext().getResources().getDimension(R.dimen.list_item_height_collapsed),
    //            itemView.getResources().getDisplayMetrics());
    //
    private val radiusExpanded = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemView.context.resources.getDimension(R.dimen.list_item_corner_radius),
            itemView.resources.displayMetrics)
    private val radiusCollapsed = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemView.context.resources.getDimension(R.dimen.list_item_corner_radius_collapsed),
            itemView.resources.displayMetrics)

    // Member views
    @BindView(R.id.progress_card)
    internal var card: CardView? = null

    @BindView(R.id.text_view_name)
    internal var name: TextView? = null

    @BindView(R.id.text_view_status)
    internal var remainingDays: TextView? = null

    @BindView(R.id.text_view_stage)
    internal var stage: TextView? = null

    @BindView(R.id.progress_horizontal)
    internal var progressBar: ProgressBar? = null

    @BindView(R.id.image_view_check)
    internal var check: ImageView? = null

    @BindView(R.id.quick_actions)
    internal var quickActions: LinearLayout? = null

    @BindView(R.id.mark_complete)
    internal var markComplete: TextView? = null

    @BindView(R.id.details)
    internal var details: TextView? = null

    @BindView(R.id.delete)
    internal var delete: TextView? = null


    init {
        ButterKnife.bind(this, itemView)
        expandedParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemView.resources.getDimension(R.dimen.list_item_height).toInt())
        //        Log.d(LOG_TAG, "BrewViewHolder heightExpanded: " + heightExpanded + "px, " + itemView.getResources().getDimension(R.dimen.list_item_height) + "dp");
        collapsedParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemView.resources.getDimension(R.dimen.list_item_height_collapsed).toInt())
        //        Log.d(LOG_TAG, "BrewViewHolder heightCollapsed: " + heightCollapsed + "px, " + itemView.getResources().getDimension(R.dimen.list_item_height_collapsed) + "dp");
    }

    fun bind(brew: Brew) {
        // Bind name and ColorStateList
        name!!.text = brew.recipe?.name

        // Calculate and bind remaining days

        val endDate = brew.endDate
        val days = TimeUtility.daysBetween(System.currentTimeMillis(), endDate).toDouble()
        val remainingString: String
        if (days <= 0) {
            remainingString = "Complete"
        } else if (days == 1.0) {
            remainingString = "Ending tomorrow"
        } else {
            remainingString = days.toInt().toString() + " days left"
        }
        remainingDays!!.text = remainingString

        // Check for complete
        when (brew.stage) {
            Stage.PRIMARY, Stage.SECONDARY -> {
                card!!.layoutParams = expandedParams
                card!!.radius = R.dimen.list_item_corner_radius.toFloat()
                progressBar!!.visibility = View.VISIBLE
                remainingDays!!.visibility = View.VISIBLE
                check!!.visibility = View.INVISIBLE
            }
            Stage.PAUSED, Stage.COMPLETE -> {
                card!!.layoutParams = collapsedParams
                card!!.radius = R.dimen.list_item_corner_radius_collapsed.toFloat()
                progressBar!!.visibility = View.INVISIBLE
                remainingDays!!.visibility = View.INVISIBLE
                check!!.visibility = View.VISIBLE
            }
        }

        // Set progress indicators
        when (brew.stage) {
            Stage.PRIMARY, Stage.PAUSED -> stage!!.setText(R.string.stage_primary)
            Stage.SECONDARY, Stage.COMPLETE -> stage!!.setText(R.string.stage_secondary)
        }

        val totalDays = TimeUtility.daysBetween(brew.startDate, brew.endDate).toDouble()
        var progress = ((totalDays - days) / totalDays * 100).toInt()
        if (progress < MIN_PROGRESS) progress = MIN_PROGRESS
        progressBar!!.progress = progress
        progressBar!!.secondaryProgress = progress + 1

        //quickActions.setVisibility(expanded ? View.VISIBLE : View.GONE);

    }

    companion object {

        private val LOG_TAG = BrewViewHolder::class.java.simpleName
    }


}
