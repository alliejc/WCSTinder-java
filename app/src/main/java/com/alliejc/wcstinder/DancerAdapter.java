package com.alliejc.wcstinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by acaldwell on 3/18/18.
 */

public class DancerAdapter extends RecyclerView.Adapter<DancerViewHolder> {
    private Context mContext;
    private List<Dancer> mDancerList;
    private IOnSelected mListener;

    public DancerAdapter(Context context, IOnSelected listener) {
        this.mContext = context;
        this.mDancerList = new ArrayList<>();
        this.mListener = listener;
    }

    @NonNull
    @Override
    public DancerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_dancer, parent, false);
        return new DancerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DancerViewHolder holder, int position) {
        Dancer dancer = mDancerList.get(holder.getAdapterPosition());
        holder.onBind(mContext, dancer, mListener);
    }

    @Override
    public int getItemCount() {
        return mDancerList.size();
    }

    public void updateAdapter(List list){
        mDancerList = list;
        notifyDataSetChanged();
    }
}
