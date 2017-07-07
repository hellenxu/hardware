package ca.six.hardware.ble;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author hellenxu
 * @date 2017-07-06
 * Copyright 2017 Six. All rights reserved.
 */

public abstract class ItemClickListener implements OnItemTouchListener {
    private GestureDetectorCompat gestureDetector;
    private RecyclerView rv;

    public ItemClickListener(Context ctx, RecyclerView rv){
        this.rv = rv;
        gestureDetector = new GestureDetectorCompat(ctx, new GestureListener());
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View childView = rv.findChildViewUnder(e.getX(), e.getY());
            if(childView != null){
                RecyclerView.ViewHolder vh = rv.getChildViewHolder(childView);
                onItemClick(vh);
            }
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public abstract void onItemClick(RecyclerView.ViewHolder vh);
}
