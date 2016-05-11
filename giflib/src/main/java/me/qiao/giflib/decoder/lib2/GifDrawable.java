package me.qiao.giflib.decoder.lib2;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by Qiao on 2016/5/9.
 * function：
 */
public class GifDrawable extends Drawable implements Runnable{
    private static final String TAG = "GifDrawable";

    private GifDecoder gifDecoder;
    private Bitmap tmpBitmap;
    private boolean animating;
    private boolean shouldClear;
    private Thread animationThread;
    private OnFrameAvailable frameCallback = null;
    private long framesDisplayDuration = -1L;
    private OnAnimationStop animationStopCallback = null;

    private ColorStateList mTint;
    private PorterDuffColorFilter mTintFilter;
    private PorterDuff.Mode mTintMode;

    private Rect mSrcRect;
    private final Rect mDstRect = new Rect();

    protected final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updateResults = new Runnable() {
        @Override
        public void run() {
            if (tmpBitmap != null && !tmpBitmap.isRecycled()) {
                invalidateSelf();
            }
        }
    };

    private final Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
            if(tmpBitmap!=null) {
                tmpBitmap.recycle();
                tmpBitmap = null;
            }
            gifDecoder = null;
            animationThread = null;
            shouldClear = false;
        }
    };

    public GifDrawable(final byte[] bytes) {
        gifDecoder = new GifDecoder();
        try {
            gifDecoder.read(bytes);
            gifDecoder.advance();
        } catch (final OutOfMemoryError e) {
            gifDecoder = null;
            Log.e(TAG, e.getMessage(), e);
            return;
        }

        final int mScaledWidth = gifDecoder.getWidth();
        final int mScaledHeight = gifDecoder.getHeight();
//        tmpBitmap = Bitmap.createBitmap(mScaledWidth, mScaledHeight, Bitmap.Config.ARGB_8888);

        mSrcRect = new Rect(0, 0, mScaledWidth, mScaledHeight);

        if (canStart()) {
            animationThread = new Thread(this);
            animationThread.start();
        }
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    public long getFramesDisplayDuration() {
        return framesDisplayDuration;
    }

    private boolean canStart() {
        return animating && gifDecoder != null && animationThread == null;
    }

    public void setFramesDisplayDuration(long framesDisplayDuration) {
        this.framesDisplayDuration = framesDisplayDuration;
    }

    public void startAnimation() {
        animating = true;

        if (canStart()) {
            animationThread = new Thread(this);
            animationThread.start();
        }
    }

    public boolean isAnimating() {
        return animating;
    }

    public void stopAnimation() {
        animating = false;

        if (animationThread != null) {
            animationThread.interrupt();
            animationThread = null;
        }
    }

    public void clear() {
        animating = false;
        shouldClear = true;
        stopAnimation();
        handler.post(cleanupRunnable);
    }

    @Override
    public int getIntrinsicHeight() {
        return gifDecoder.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return gifDecoder.getWidth();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mDstRect.set(bounds);
    }

    private PorterDuffColorFilter updateTintFilter(ColorStateList tint, PorterDuff.Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }

        final int color = tint.getColorForState(getState(), Color.TRANSPARENT);
        return new PorterDuffColorFilter(color, tintMode);
    }

    @Override
    public void setTintList(ColorStateList tint) {
        mTint = tint;
        mTintFilter = updateTintFilter(tint, mTintMode);
        invalidateSelf();
    }

    @Override
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
        mTintMode = tintMode;
        mTintFilter = updateTintFilter(mTint, tintMode);
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] stateSet) {
        if (mTint != null && mTintMode != null) {
            mTintFilter = updateTintFilter(mTint, mTintMode);
            return true;
        }
        return false;
    }

    @Override
    public boolean isStateful() {
        return super.isStateful() || (mTint != null && mTint.isStateful());
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        final boolean changed = super.setVisible(visible, restart);
        if (!visible) {
            stopAnimation();
        } else if (animating) {
            startAnimation();
        }
        return changed;
    }

    @Override
    public void draw(Canvas canvas) {
        final boolean clearColorFilter;
        if (mTintFilter != null && mPaint.getColorFilter() == null) {
            mPaint.setColorFilter(mTintFilter);
            clearColorFilter = true;
        } else {
            clearColorFilter = false;
        }
        if(tmpBitmap!=null) {
            canvas.drawBitmap(tmpBitmap, mSrcRect, mDstRect, mPaint);
        }
        if (clearColorFilter) {
            mPaint.setColorFilter(null);
        }
    }

    @Override public void run() {
        if (shouldClear) {
            handler.post(cleanupRunnable);
            return;
        }

        final int n = gifDecoder.getFrameCount();
        do {
            for (int i = 0; i < n; i++) {
                if (!animating) {
                    break;
                }
                //milliseconds spent on frame decode
                long frameDecodeTime = 0;
                try {
                    long before = System.nanoTime();
                    tmpBitmap = gifDecoder.getNextFrame();
                    frameDecodeTime = (System.nanoTime() - before) / 1000000;
                    if (frameCallback != null) {
                        tmpBitmap = frameCallback.onFrameAvailable(tmpBitmap);
                    }

                    if (!animating) {
                        break;
                    }
                    handler.post(updateResults);
                } catch (final ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    Log.w(TAG, e);
                }
                if (!animating) {
                    break;
                }
                gifDecoder.advance();
                try {
                    int delay = gifDecoder.getNextDelay();
                    // Sleep for frame duration minus time already spent on frame decode
                    // Actually we need next frame decode duration here,
                    // but I use previous frame time to make code more readable
                    delay -= frameDecodeTime;
                    if (delay > 0) {
                        Thread.sleep(framesDisplayDuration > 0 ? framesDisplayDuration : delay);
                    }
                } catch (final Exception e) {
                    // suppress any exception
                    // it can be InterruptedException or IllegalArgumentException
                }
            }
        } while (animating);
        if (animationStopCallback != null) {
            animationStopCallback.onAnimationStop();
        }
    }

    public OnFrameAvailable getOnFrameAvailable() {
        return frameCallback;
    }

    public void setOnFrameAvailable(OnFrameAvailable frameProcessor) {
        this.frameCallback = frameProcessor;
    }

    public OnAnimationStop getOnAnimationStop() {
        return animationStopCallback;
    }

    public void setOnAnimationStop(OnAnimationStop animationStop) {
        this.animationStopCallback = animationStop;
    }

    public interface OnAnimationStop {
        void onAnimationStop();
    }

    public void recycle(){
        clear();
    }

    public interface OnFrameAvailable {
        Bitmap onFrameAvailable(Bitmap bitmap);
    }
}
