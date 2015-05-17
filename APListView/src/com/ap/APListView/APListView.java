package com.ap.APListView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by andrejp on 16.5.2015.
 */
public class APListView extends ListView {

    private APListScroller listScroller = null;

    public APListView(Context context) {
        super(context);
        initialize();
    }

    public APListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public APListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public APListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        listScroller = new APListScroller(getContext(), this);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        listScroller.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return listScroller.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof APListAdapter) {
            listScroller.setAdapter(adapter);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        listScroller.onSizeChanged(w, h, oldw, oldh);
    }
}