//package me.qiao.giflib.decoder.glide;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.ColorFilter;
//import android.graphics.Paint;
//import android.graphics.PixelFormat;
//import android.graphics.Rect;
//import android.graphics.drawable.Animatable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.view.Gravity;
//
//
//import java.nio.ByteBuffer;
//
//import me.qiao.giflib.decoder.lib2.GifDecoder;
//
///**
// * An animated {@link android.graphics.drawable.Drawable} that plays the frames of an animated GIF.
// */
//public class GifDrawable extends Drawable implements GifFrameLoader.FrameCallback,
//        Animatable {
//  /**
//   * A constant indicating that an animated drawable should loop continuously.
//   */
//  public static final int LOOP_FOREVER = -1;
//  /**
//   * A constant indicating that an animated drawable should loop for its default number of times.
//   * For animated GIFs, this constant indicates the GIF should use the netscape loop count if
//   * present.
//   */
//  public static final int LOOP_INTRINSIC = 0;
//
//  private final GifState state;
//  /**
//   * True if the drawable is currently animating.
//   */
//  private boolean isRunning;
//  /**
//   * True if the drawable should animate while visible.
//   */
//  private boolean isStarted;
//  /**
//   * True if the drawable's resources have been recycled.
//   */
//  private boolean isRecycled;
//  /**
//   * True if the drawable is currently visible. Default to true because on certain platforms (at
//   * least 4.1.1), setVisible is not called on {@link android.graphics.drawable.Drawable Drawables}
//   * during {@link android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)}.
//   * See issue #130.
//   */
//  private boolean isVisible = true;
//  /**
//   * The number of times we've looped over all the frames in the gif.
//   */
//  private int loopCount;
//  /**
//   * The number of times to loop through the gif animation.
//   */
//  private int maxLoopCount = LOOP_FOREVER;
//
//  private boolean applyGravity;
//  private Paint paint;
//  private Rect destRect;
//
//  public GifDrawable(Context context, GifDecoder gifDecoder,int targetFrameWidth, int targetFrameHeight,
//                     Bitmap firstFrame) {
//    this(new GifState(context,
//        new GifFrameLoader(context, gifDecoder, targetFrameWidth, targetFrameHeight, firstFrame)));
//  }
//
//  GifDrawable(GifState state) {
//    this.state = state;
//  }
//
//  // Visible for testing.
//  GifDrawable(Context context, GifFrameLoader frameLoader, Paint paint) {
//    this(new GifState(context, frameLoader));
//    this.paint = paint;
//  }
//
//  public int getSize() {
//    return state.frameLoader.getSize();
//  }
//
//  public Bitmap getFirstFrame() {
//    return state.frameLoader.getFirstFrame();
//  }
//
//  public ByteBuffer getBuffer() {
//    return state.frameLoader.getBuffer();
//  }
//
//  public int getFrameCount() {
//    return state.frameLoader.getFrameCount();
//  }
//
//  /**
//   * Returns the current frame index in the range 0..{@link #getFrameCount()} - 1, or -1 if no frame
//   * is displayed.
//   */
//  public int getFrameIndex() {
//    return state.frameLoader.getCurrentIndex();
//  }
//
//  private void resetLoopCount() {
//    loopCount = 0;
//  }
//
//  @Override
//  public void start() {
//    isStarted = true;
//    resetLoopCount();
//    if (isVisible) {
//      startRunning();
//    }
//  }
//
//  @Override
//  public void stop() {
//    isStarted = false;
//    stopRunning();
//  }
//
//  private void startRunning() {
//    // If we have only a single frame, we don't want to decode it endlessly.
//    if (state.frameLoader.getFrameCount() == 1) {
//      invalidateSelf();
//    } else if (!isRunning) {
//      isRunning = true;
//      state.frameLoader.subscribe(this);
//      invalidateSelf();
//    }
//  }
//
//  private void stopRunning() {
//    isRunning = false;
//    state.frameLoader.unsubscribe(this);
//  }
//
//  @Override
//  public boolean setVisible(boolean visible, boolean restart) {
//    isVisible = visible;
//    if (!visible) {
//      stopRunning();
//    } else if (isStarted) {
//      startRunning();
//    }
//    return super.setVisible(visible, restart);
//  }
//
//  @Override
//  public int getIntrinsicWidth() {
//    return state.frameLoader.getWidth();
//  }
//
//  @Override
//  public int getIntrinsicHeight() {
//    return state.frameLoader.getHeight();
//  }
//
//  @Override
//  public boolean isRunning() {
//    return isRunning;
//  }
//
//  // For testing.
//  void setIsRunning(boolean isRunning) {
//    this.isRunning = isRunning;
//  }
//
//  @Override
//  protected void onBoundsChange(Rect bounds) {
//    super.onBoundsChange(bounds);
//    applyGravity = true;
//  }
//
//  @Override
//  public void draw(Canvas canvas) {
//    if (isRecycled) {
//      return;
//    }
//
//    if (applyGravity) {
//      Gravity.apply(GifState.GRAVITY, getIntrinsicWidth(), getIntrinsicHeight(), getBounds(),
//          getDestRect());
//      applyGravity = false;
//    }
//
//    Bitmap currentFrame = state.frameLoader.getCurrentFrame();
//    canvas.drawBitmap(currentFrame, null, getDestRect(), getPaint());
//  }
//
//  @Override
//  public void setAlpha(int i) {
//    getPaint().setAlpha(i);
//  }
//
//  @Override
//  public void setColorFilter(ColorFilter colorFilter) {
//    getPaint().setColorFilter(colorFilter);
//  }
//
//  private Rect getDestRect() {
//    if (destRect == null) {
//      destRect = new Rect();
//    }
//    return destRect;
//  }
//
//  private Paint getPaint() {
//    if (paint == null) {
//      paint = new Paint(Paint.FILTER_BITMAP_FLAG);
//    }
//    return paint;
//  }
//
//  @Override
//  public int getOpacity() {
//    // We can't tell, so default to transparent to be safe.
//    return PixelFormat.TRANSPARENT;
//  }
//
//  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//  @Override
//  public void onFrameReady() {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && getCallback() == null) {
//      stop();
//      invalidateSelf();
//      return;
//    }
//
//    invalidateSelf();
//
//    if (getFrameIndex() == getFrameCount() - 1) {
//      loopCount++;
//    }
//
//    if (maxLoopCount != LOOP_FOREVER && loopCount >= maxLoopCount) {
//      stop();
//    }
//  }
//
//  @Override
//  public ConstantState getConstantState() {
//    return state;
//  }
//
//  /**
//   * Clears any resources for loading frames that are currently held on to by this object.
//   */
//  public void recycle() {
//    isRecycled = true;
//    state.frameLoader.clear();
//  }
//
//  // For testing.
//  boolean isRecycled() {
//    return isRecycled;
//  }
//
//  public void setLoopCount(int loopCount) {
//    if (loopCount <= 0 && loopCount != LOOP_FOREVER && loopCount != LOOP_INTRINSIC) {
//      throw new IllegalArgumentException("Loop count must be greater than 0, or equal to "
//          + "GlideDrawable.LOOP_FOREVER, or equal to GlideDrawable.LOOP_INTRINSIC");
//    }
//
//    if (loopCount == LOOP_INTRINSIC) {
//      maxLoopCount = state.frameLoader.getLoopCount();
//    } else {
//      maxLoopCount = loopCount;
//    }
//  }
//
//  static class GifState extends ConstantState {
//    static final int GRAVITY = Gravity.FILL;
//    final Context context;
//    final GifFrameLoader frameLoader;
//
//    public GifState(Context context, GifFrameLoader frameLoader) {
//      this.context = context.getApplicationContext();
//      this.frameLoader = frameLoader;
//    }
//
//    @Override
//    public Drawable newDrawable(Resources res) {
//      return newDrawable();
//    }
//
//    @Override
//    public Drawable newDrawable() {
//      return new GifDrawable(this);
//    }
//
//    @Override
//    public int getChangingConfigurations() {
//      return 0;
//    }
//  }
//}
