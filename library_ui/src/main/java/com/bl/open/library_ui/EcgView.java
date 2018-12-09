package com.bl.open.library_ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;
import android.widget.Scroller;

import com.bl.open.library_data.EcgBean;
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

  private EcgBean mEcgBean;
  private float mHorizontalMultiple = 1;
  private float mVerticalMultiple = 1;
  private boolean isDynamic;
  private boolean isCreated;
  protected int mWidth;
  protected int mHeight;

  EcgBackground mEcgBackground;
  EcgLine mEcgLine;

  private float mLastX;
  private int mDistance;

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
    setClickable(true);
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


  public void update(EcgBean bean) {
    this.update(bean, mHorizontalMultiple, mVerticalMultiple);
  }

  public void update(EcgBean info, float horizontalMultiple, float verticalMultiple) {
    this.update(info, horizontalMultiple, verticalMultiple, false);
  }

  public void update(EcgBean info, float horizontalMultiple, float verticalMultiple, boolean isDynamic) {
    this.mEcgBean = info != null ? info : mEcgBean;
    if (mEcgBean == null || mEcgBean.getWaveData() == null || mEcgBean.getWaveData().size() == 0) {
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
    mEcgBackground.drawBackground(canvas, mWidth, mHeight, mDistance);
    mEcgLine.drawEcgLine(canvas, mEcgBean, mWidth, mHeight, mDistance);
    Log.d(TAG, "draw " + Thread.currentThread().getName());
    mHolder.unlockCanvasAndPost(canvas);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (isDynamic) {
      return true;
    }
    float x = event.getX();
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mLastX = x;
        break;

      case MotionEvent.ACTION_MOVE:
        float distance = x - mLastX;
        if (mDistance <= 0 && distance >= 0) {
          Log.d(TAG, "onTouchEvent() isMin, just break");
          break;
        }
        boolean haveData = false;
        int max = 0;
        if (mEcgBean != null && mEcgBean.haveData()) {
          Float[] data = mEcgBean.getWaveData().get(0).getValue();
          haveData = true;
          max = data.length - mWidth;
          if (mDistance >= (data.length - mWidth) && distance <= 0) {
            Log.d(TAG, "onTouchEvent() isMax - just break");
            break;
          }
        }
        mDistance -= distance;
        Log.d(TAG, "onTouchEvent() --- distance = " + distance + "-mDistance=" + mDistance);
        if (mDistance < 0) {
          mDistance = 0;
        }
        if (haveData && mDistance > max) {
          mDistance = max;
        }
        mLastX = x;
        justPost();
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        break;
    }
    return super.onTouchEvent(event);
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
