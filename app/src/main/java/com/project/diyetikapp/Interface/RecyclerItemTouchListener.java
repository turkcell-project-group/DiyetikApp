package com.project.diyetikapp.Interface;

import android.support.v7.widget.RecyclerView;

public interface RecyclerItemTouchListener {
    void  onSwiped(RecyclerView.ViewHolder viewHolder,int directions,int position);
}
