package com.alliejc.wcstinder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by acaldwell on 3/18/18.
 */

public class DancerViewHolder extends RecyclerView.ViewHolder {
    private TextView mName;
    public DancerViewHolder(View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.name_text);
    }

    public void onBind(Context context, Dancer dancer){
        Log.e("VIEW HOLDER", dancer.getFirstName());
        mName.setText(dancer.getFirstName() + dancer.getLastName());

    }
}
