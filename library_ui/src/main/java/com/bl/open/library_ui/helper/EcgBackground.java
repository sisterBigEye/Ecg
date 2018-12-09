package com.bl.open.library_ui.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class EcgBackground {


  private int mWidth;
  private int mHeight;
  private int mBkgColor;
  private Paint mBkgLinePaint;
  private int mBkgLineColor = Color.RED;
  private int mLinePaintWidth = 1;
  private static final float BLANK = 10;


  public EcgBackground() {
    mBkgLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBkgLinePaint.setStyle(Paint.Style.STROKE);
    mBkgLinePaint.setColor(mBkgLineColor);
    mBkgLinePaint.setStrokeWidth(mLinePaintWidth);
  }

  public void drawBackground(Canvas canvas, int width, int height, int distance) {
    this.mWidth = width;
    this.mHeight = height;
    canvas.save();
    for (int i = 0; i < mWidth / BLANK; i++) {
      canvas.drawLine(i * BLANK, 0, i * BLANK, mHeight, mBkgLinePaint);
    }
    for (int i = 0; i < mHeight / 5; i++) {
      canvas.drawLine(0, i * BLANK, mWidth, i * BLANK, mBkgLinePaint);
    }
    canvas.restore();
  }

  public void setWidth(int mWidth) {
    this.mWidth = mWidth;
  }

  public void setHeight(int mHeight) {
    this.mHeight = mHeight;
  }

  public void setBkgColor(int mBkgColor) {
    this.mBkgColor = mBkgColor;
  }

  public void setBkgLineColor(int mBkgLineColor) {
    this.mBkgLineColor = mBkgLineColor;
  }

  public void setSize(int width, int height) {
    this.mWidth = width;
    this.mHeight = height;
  }
}
