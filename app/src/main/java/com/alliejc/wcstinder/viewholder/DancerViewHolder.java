package com.alliejc.wcstinder.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alliejc.wcstinder.trackmyswing.Dancer;
import com.alliejc.wcstinder.R;
import com.alliejc.wcstinder.callback.IOnSelected;

/**
 * Created by acaldwell on 3/18/18.
 */

public class DancerViewHolder extends RecyclerView.ViewHolder {
    private TextView mName;
    private TextView mPoints;
    private TextView mLevelText;
    private ImageButton mContactButton;
    private View mRoot;

    public DancerViewHolder(View itemView) {
        super(itemView);
        mRoot = itemView;
        mName = (TextView) itemView.findViewById(R.id.name_text);
        mPoints = (TextView) itemView.findViewById(R.id.point_text);
        mLevelText = (TextView) itemView.findViewById(R.id.level_text);
        mContactButton = (ImageButton) itemView.findViewById(R.id.contact_button);
    }

    public void onBind(Context context, final Dancer dancer, final IOnSelected listener){
        if(dancer != null) {
            mName.setText(dancer.getFirstName() + " " + dancer.getLastName());
            mLevelText.setText(dancer.getDivision().toString());
            if(dancer.getCurrentPoints() != null) {
                mPoints.setText(": " + String.valueOf(dancer.getCurrentPoints()));
            } else {
                mPoints.setText(": 0");
            }

            mContactButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDancerSelected(dancer.getFirstName());
                }
            });
        }


    }
}
