package com.alliejc.wcstinder.viewholder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.alliejc.wcstinder.callback.IOnSelected
import com.alliejc.wcstinder.trackmyswing.Dancer
import kotlinx.android.synthetic.main.item_dancer.view.*

/**
 * Created by acaldwell on 4/8/18.
 */
class DancerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun onBind(context: Context, dancer: Dancer?, listener:IOnSelected){
        if(dancer != null){
            itemView.name_text.text = dancer.firstName.plus(" ").plus(dancer.lastName)
            itemView.level_text.text = dancer.division
            itemView.point_text.text = ": ".plus(dancer.currentPoints.toString())
            itemView.relevance_text.text = dancer.relevance.toString()
        } else {
            itemView.point_text.text = ": 0"
        }

        itemView.contact_button.setOnClickListener {
            listener.onDancerSelected(dancer?.firstName + " " + dancer?.lastName)
        }
    }
}