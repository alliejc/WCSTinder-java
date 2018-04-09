package com.alliejc.wcstinder.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.alliejc.wcstinder.R
import com.alliejc.wcstinder.callback.IOnSelected
import com.alliejc.wcstinder.viewholder.DancerViewHolder
import com.alliejc.wcstinder.ext.inflate
import com.alliejc.wcstinder.trackmyswing.Dancer

class DancerAdapter(context: Context, listener:IOnSelected): RecyclerView.Adapter<DancerViewHolder>() {

    var mDancerList: MutableList<Dancer>? = mutableListOf()
    var mContext = context
    var mListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DancerViewHolder {
        var view = parent.inflate(R.layout.item_dancer, false)
        return DancerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DancerViewHolder, position: Int) {
        var dancer = mDancerList?.get(holder.adapterPosition)
        holder.onBind(mContext, dancer, mListener)
    }

    override fun getItemCount(): Int {
        var count = 0
        if(mDancerList != null){
            count = mDancerList!!.size
        }
        return count
    }

    fun updateAdapter(dancer: Dancer){
        mDancerList?.add(dancer)
        notifyDataSetChanged()
    }

    fun updateAdapter(list: MutableList<Dancer>?){
        mDancerList = list
        notifyDataSetChanged()
    }
}