package com.ap.APListView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by andrejp on 17.5.2015.
 */
public class APListHeaderView {

    private Bitmap headerBitmap;
    private int position;
    private int width;
    private int height;
    private int offset;

    public APListHeaderView() {
        headerBitmap = null;
        offset = 0;
    }

    public void setHeader(View headerView, int position) {
        if (width == 0 || height == 0) {
            return;
        }
        this.position = position;
        headerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(headerBitmap);
        Drawable bgDrawable = headerView.getBackground();
        if (bgDrawable!=null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        headerView.draw(canvas);
        offset = 0;
    }

    public void setHeaderSize(int width, int height) {
        this.width = width;
        this.height = height;
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

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
