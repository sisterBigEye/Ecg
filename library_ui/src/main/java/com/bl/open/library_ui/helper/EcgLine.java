package com.bl.open.library_ui.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.bl.open.library_data.EcgBean;
import com.bl.open.library_data.EcgInfo;

public class EcgLine {

  private int mWidth;
  private int mHeight;
  private int mLineColor = Color.BLUE;
  private Paint mLinePaint;
  private int mLinePaintWidth = 3;
  private Path mLinePath;
  private static final int BLANK = 20;

  public EcgLine() {
    mLinePath = new Path();
    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setStyle(Paint.Style.STROKE);
    mLinePaint.setColor(mLineColor);
    mLinePaint.setStrokeWidth(mLinePaintWidth);

  }

  public void drawEcgLine(Canvas canvas, EcgBean data, int width, int height, int distance) {
    if (data == null || data.getWaveData() == null || data.getWaveData().get(0) == null) {
      return;
    }
    float lastR = BLANK;
    if (distance < 0) {
      distance = 0;
    }
    for (int line = 0; line < data.getWaveData().size(); line++) {
      Float[] value = data.getWaveData().get(line).getValue();
      float maxR = data.ecgMaxR[line];
      canvas.save();
      canvas.translate(0, lastR);
      mLinePath.reset();
      int maxStart = value.length - mWidth;
      distance = distance > maxStart ? maxStart : distance;
      mLinePath.moveTo(0, maxR - value[distance]);
      int length = value.length;
      for (int i = 1; i < length; i++) {
        if (i > width) {
          break;
        }
        int dataIndex = i + distance;
        if (dataIndex >= length) {
          dataIndex = length - 1;
        }
        mLinePath.lineTo(i, maxR - value[dataIndex]);
      }
      canvas.drawPath(mLinePath, mLinePaint);
      canvas.restore();
      lastR = maxR + lastR + BLANK;
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
