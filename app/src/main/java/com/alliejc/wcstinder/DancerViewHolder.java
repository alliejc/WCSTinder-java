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
    private TextView mPoints;

    public DancerViewHolder(View itemView) {
        super(itemView);
        mName = (TextView) itemView.findViewById(R.id.name_text);
        mPoints = (TextView) itemView.findViewById(R.id.point_text);
    }

    public void onBind(Context context, Dancer dancer){
        if(dancer != null) {
            mName.setText(dancer.getFirstName() + " " + dancer.getLastName());
            if(dancer.getCurrentPoints() != null) {
                mPoints.setText(String.valueOf(dancer.getCurrentPoints()));
            } else {
                mPoints.setText("0");
            }
        }

    }
}
