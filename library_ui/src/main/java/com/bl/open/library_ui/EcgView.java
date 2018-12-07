package com.bl.open.library_ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

public class EcgView extends SurfaceView implements SurfaceHolder.Callback {

  private static final String TAG = "EcgView";
  private HandlerThread mHandlerThread;
  private Handler mHandler;
  private EventHandler mEventHandler;
  private SurfaceHolder mHolder;
  private static final String HANDLE_THREAD_NAME = "surfaceThread";
  private float[] mData;
  private int mType;
  private int mFlags;
  private int mMode;
  private boolean isCreated;
  private Path mLinePath;
  private Paint mLinePaint;
  private Paint mBkgLinePaint;
  protected int mWidth;
  protected int mHeight;

  private Runnable mDrawRunnable = new Runnable() {
    @Override
    public void run() {
      Log.d(TAG, "run begin");
      draw();
    }
  };

  public EcgView(Context context) {
    this(context, null);
  }

  public EcgView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public EcgView(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public EcgView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    mWidth = MeasureUtil.measureSize(widthMeasureSpec, mWidth > 0 ? mWidth : 500);
    mHeight = MeasureUtil.measureSize(heightMeasureSpec, mHeight > 0 ? mHeight : 300);
    setMeasuredDimension(mWidth, mHeight);
    Log.d(TAG, "onMeasure() --- mWidth = " + mWidth + " --- mHeight = " + mHeight);
  }

  private void init() {
    mLinePath = new Path();
    mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mLinePaint.setStyle(Paint.Style.STROKE);
    mLinePaint.setColor(Color.BLUE);
    mLinePaint.setStrokeWidth(3);

    mBkgLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBkgLinePaint.setStyle(Paint.Style.STROKE);
    mBkgLinePaint.setColor(Color.RED);
    mBkgLinePaint.setStrokeWidth(2);

    mHandlerThread = new HandlerThread("HANDLE_THREAD_NAME", Thread.MAX_PRIORITY);
    mHandlerThread.start();
    mEventHandler = new EventHandler(this);
    mHolder = getHolder();
    mHolder.addCallback(this);
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    Log.d(TAG, "surfaceCreated");
    isCreated = true;
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    Log.d(TAG, "onDetachedFromWindow");
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    Log.d(TAG, "surfaceDestroyed");
    isCreated = false;
  }

  public void update(float[] data, int type) {
    this.mData = data != null ? data : mData;
    if (mData == null || mData.length == 0) {
      return;
    }
    this.mType = type;
    justPost();
  }

  public void update(float[] data, int type, int flags) {
    this.mData = data != null ? data : mData;
    if (mData == null || mData.length == 0) {
      return;
    }
    this.mType = type;
    this.mFlags = flags;
    justPost();
  }

  public void update(float[] data, int type, int flags, int mode) {
    this.mData = data != null ? data : mData;
    if (mData == null || mData.length == 0) {
      return;
    }
    this.mType = type;
    this.mFlags = flags;
    this.mMode = mode;
    justPost();
  }

  private void justPost() {
    if (mHandlerThread == null || !mHandlerThread.isAlive() || !isCreated) {
      mEventHandler.sendEmptyMessageDelayed(0, 100);
      return;
    }
    if (mHandler == null) {
      mHandler = new Handler(mHandlerThread.getLooper());
    }
    mHandler.post(mDrawRunnable);
  }

  private void draw() {
    Canvas canvas = mHolder.lockCanvas();
    if (canvas == null) {
      justPost();
      return;
    }
    canvas.drawColor(Color.WHITE);
    drawBackground(canvas);
    drawEcgLine(canvas);
    Log.d(TAG, "draw " + Thread.currentThread().getName());
    mHolder.unlockCanvasAndPost(canvas);
  }

  private void drawBackground(Canvas canvas) {
    canvas.save();
    for (int i = 0; i < mWidth / 5; i++) {
      canvas.drawLine(i * 5, 0, i * 5, mHeight, mBkgLinePaint);
    }
    for (int i = 0; i < mHeight / 5; i++) {
      canvas.drawLine(0, i * 5, mWidth, i * 5, mBkgLinePaint);
    }
    canvas.restore();
  }

  private void drawEcgLine(Canvas canvas) {
    canvas.save();
    canvas.translate(0, 200);
    mLinePath.reset();
    mLinePath.moveTo(0, mData[0]);
    int length = mData.length > 1000 ? 1000 : mData.length;
    for (int i = 1; i < length; i++) {
      mLinePath.lineTo(i, mData[i]);
    }
    canvas.drawPath(mLinePath, mLinePaint);
    canvas.restore();
  }

  private static class EventHandler extends Handler {
    private final WeakReference<EcgView> mWeak;

    EventHandler(EcgView ecgView) {
      mWeak = new WeakReference<>(ecgView);
    }

    @Override
    public void handleMessage(Message msg) {
      EcgView ecgView = mWeak.get();
      if (ecgView == null) {
        Log.e(TAG, "ecgView is null");
        return;
      }
      Log.d(TAG, "handleMessage msg.what=" + msg.what);
      switch (msg.what) {
        case 0:
          ecgView.justPost();
          break;

        default:
          break;
      }
    }
  }
}
