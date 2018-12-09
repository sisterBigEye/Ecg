package com.bl.open.library_ui.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.bl.open.library_data.EcgInfo;

public class EcgLine {

  private int mWidth;
  private int mHeight;
  private int mLineColor = Color.BLUE;
  private Paint mLinePaint;
  private int mLinePaintWidth = 3;
  private Path mLinePath;

  public EcgLine() {
    mLinePath = new Path();
    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setStyle(Paint.Style.STROKE);
    mLinePaint.setColor(mLineColor);
    mLinePaint.setStrokeWidth(mLinePaintWidth);

  }

  public void drawEcgLine(Canvas canvas, EcgInfo data) {
    if (data == null || data.ecgDataArray == null) {
      return;
    }
    float lastR = 0;
    for (float maxR : data.ecgMaxR) {
      canvas.save();
      canvas.translate(0, lastR);
      mLinePath.reset();
      mLinePath.moveTo(0, maxR - data.ecgDataArray[0][0]);
      int length = data.ecgDataArray[0].length > 2000 ? 2000 : data.ecgDataArray[0].length;
      for (int i = 1; i < length; i++) {
        mLinePath.lineTo(i, maxR - data.ecgDataArray[0][i * 2]);
      }
      canvas.drawPath(mLinePath, mLinePaint);
      canvas.restore();
      lastR = maxR;
      // Todo
      break;
    }
  }

  public void setWidth(int mWidth) {
    this.mWidth = mWidth;
  }

  public void setHeight(int mHeight) {
    this.mHeight = mHeight;
  }

  public void setLineColor(int mBkgLineColor) {
    this.mLineColor = mBkgLineColor;
  }

  public void setSize(int width, int height) {
    this.mWidth = width;
    this.mHeight = height;
  }

  public void setLinePaintWidth(int linePaintWidth) {
    this.mLinePaintWidth = linePaintWidth;
  }
}
