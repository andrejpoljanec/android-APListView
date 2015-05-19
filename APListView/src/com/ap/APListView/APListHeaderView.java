package com.ap.APListView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by andrejp on 17.5.2015.
 */
public class APListHeaderView {

    private Bitmap headerBitmap;
    private int position;
    private int height;
    private int offset;

    public APListHeaderView() {
        headerBitmap = null;
        offset = 0;
    }

    public void setHeader(View headerView, int position) {
        this.position = position;
        headerView.setDrawingCacheEnabled(true);
        headerBitmap = Bitmap.createBitmap(headerView.getDrawingCache());
        headerView.setDrawingCacheEnabled(false);
        offset = 0;
        if (headerBitmap != null) {
            height = headerBitmap.getHeight();
        }
    }

    public void clearHeader() {
        headerBitmap = null;
        offset = 0;
    }

    public void draw(Canvas canvas) {
        if (headerBitmap != null) {
            canvas.drawBitmap(headerBitmap, 0, offset, null);
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
