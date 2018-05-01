package com.alliejc.wcstinder.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.sticky_header.view.*

class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

fun onBind(title: String?) {
    if (title != null) {
        itemView.header_text_view.text = title
    }
}
}