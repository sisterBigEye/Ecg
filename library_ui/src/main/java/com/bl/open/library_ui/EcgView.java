package com.bl.open.library_ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bl.open.library_data.EcgInfo;
import com.bl.open.library_ui.helper.EcgBackground;
import com.bl.open.library_ui.helper.EcgLine;
import com.bl.open.library_ui.util.EcgUtil;

import java.lang.ref.WeakReference;

public class EcgView extends SurfaceView implements SurfaceHolder.Callback {

  private static final String TAG = "EcgView";
  private HandlerThread mHandlerThread;
  private Handler mHandler;
  private EventHandler mEventHandler;
  private SurfaceHolder mHolder;
  private static final String HANDLE_THREAD_NAME = "surfaceThread";
  private EcgInfo mEcgInfo;
  private float mHorizontalMultiple = 1;
  private float mVerticalMultiple = 1;
  // Todo
  private boolean isDynamic;
  private boolean isCreated;
  protected int mWidth;
  protected int mHeight;

  EcgBackground mEcgBackground;
  EcgLine mEcgLine;

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

    mWidth = EcgUtil.measureSize(widthMeasureSpec, mWidth > 0 ? mWidth : 500);
    mHeight = EcgUtil.measureSize(heightMeasureSpec, mHeight > 0 ? mHeight : 300);
    setMeasuredDimension(mWidth, mHeight);
    Log.d(TAG, "onMeasure() --- mWidth = " + mWidth + " --- mHeight = " + mHeight);
  }

  private void init() {
    mEcgBackground = new EcgBackground();
    mEcgLine = new EcgLine();

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


  public void update(EcgInfo info) {
    this.update(info, mHorizontalMultiple, mVerticalMultiple);
  }

  public void update(EcgInfo info, float horizontalMultiple, float verticalMultiple) {
    this.update(info, horizontalMultiple, verticalMultiple, false);
  }

  public void update(EcgInfo info, float horizontalMultiple, float verticalMultiple, boolean isDynamic) {
    this.mEcgInfo = info != null ? info : mEcgInfo;
    if (mEcgInfo == null || mEcgInfo.ecgDataArray == null || mEcgInfo.ecgDataArray.length == 0) {
      return;
    }
    this.mHorizontalMultiple = horizontalMultiple;
    this.mVerticalMultiple = verticalMultiple;
    this.isDynamic = isDynamic;
    justPost();
  }

  private void justPost() {
    if (mHandlerThread == null || !mHandlerThread.isAlive() || !isCreated) {
      mEventHandler.sendEmptyMessageDelayed(0, 50);
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
    mEcgBackground.drawBackground(canvas, mWidth, mHeight);
    mEcgLine.drawEcgLine(canvas, mEcgInfo);
    Log.d(TAG, "draw " + Thread.currentThread().getName());
    mHolder.unlockCanvasAndPost(canvas);
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
