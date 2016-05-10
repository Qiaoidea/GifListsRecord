//package me.qiao.giflib.decoder.glide;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.os.SystemClock;
//
//import java.nio.ByteBuffer;
//import java.util.ArrayList;
//import java.util.List;
//
//import me.qiao.giflib.decoder.lib2.GifDecoder;
//
//
//public class GifFrameLoader {
//  private final GifDecoder gifDecoder;
//  private final Handler handler;
//  private final Context context;
//  private final List<FrameCallback> callbacks = new ArrayList<>();
//
//  private boolean isRunning = false;
//  private boolean isLoadPending = false;
//  private DelayTarget current;
//  private boolean isCleared;
//  private DelayTarget next;
//  private Bitmap firstFrame;
//
//  public interface FrameCallback {
//    void onFrameReady();
//  }
//
//  public GifFrameLoader(Context context, GifDecoder gifDecoder, int width, int height, Bitmap firstFrame) {
//    this(context,
//        gifDecoder,
//        null /*handler*/,
//            firstFrame);
//  }
//
//  @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
//  GifFrameLoader(Context context, GifDecoder gifDecoder,
//                 Handler handler,
//                 Bitmap firstFrame) {
//    if (handler == null) {
//      handler = new Handler(Looper.getMainLooper(), new FrameLoaderCallback());
//    }
//    this.context = context;
//    this.handler = handler;
//
//    this.gifDecoder = gifDecoder;
//
//  }
//
//
//  Bitmap getFirstFrame() {
//    return firstFrame;
//  }
//
//  void subscribe(FrameCallback frameCallback) {
//    if (isCleared) {
//      throw new IllegalStateException("Cannot subscribe to a cleared frame loader");
//    }
//    boolean start = callbacks.isEmpty();
//    if (callbacks.contains(frameCallback)) {
//      throw new IllegalStateException("Cannot subscribe twice in a row");
//    }
//    callbacks.add(frameCallback);
//    if (start) {
//      start();
//    }
//  }
//
//  void unsubscribe(FrameCallback frameCallback) {
//    callbacks.remove(frameCallback);
//    if (callbacks.isEmpty()) {
//      stop();
//    }
//  }
//
//  int getWidth() {
//    return getCurrentFrame().getWidth();
//  }
//
//  int getHeight() {
//    return getCurrentFrame().getHeight();
//  }
//
//  int getSize() {
//    return gifDecoder.getByteSize() + getFrameSize();
//  }
//
//  int getCurrentIndex() {
//    return current != null ? current.index : -1;
//  }
//
//  private int getFrameSize() {
//    return getBitmapByteSize(getCurrentFrame().getWidth(), getCurrentFrame().getHeight(),
//        getCurrentFrame().getConfig());
//  }
//
//  ByteBuffer getBuffer() {
//    return gifDecoder.getData().asReadOnlyBuffer();
//  }
//
//  int getFrameCount() {
//    return gifDecoder.getFrameCount();
//  }
//
//  int getLoopCount() {
//    return gifDecoder.getLoopCount();
//  }
//
//  private void start() {
//    if (isRunning) {
//      return;
//    }
//    isRunning = true;
//    isCleared = false;
//
//    loadNextFrame();
//  }
//
//  private void stop() {
//    isRunning = false;
//  }
//
//  void clear() {
//    callbacks.clear();
//    recycleFirstFrame();
//    stop();
//    if (current != null) {
//      current = null;
//    }
//    if (next != null) {
//      next = null;
//    }
//    gifDecoder.clear();
//    isCleared = true;
//  }
//
//  Bitmap getCurrentFrame() {
//    return current != null ? current.getResource() : firstFrame;
//  }
//
//  private void loadNextFrame() {
//    if (!isRunning || isLoadPending) {
//      return;
//    }
//    isLoadPending = true;
//    // Get the delay before incrementing the pointer because the delay indicates the amount of time
//    // we want to spend on the current frame.
//    int delay = gifDecoder.getNextDelay();
//    long targetTime = SystemClock.uptimeMillis() + delay;
//
//    gifDecoder.advance();
//    next = new DelayTarget(handler, gifDecoder.getCurrentFrameIndex(), targetTime);
//  }
//
//  private void recycleFirstFrame() {
//    if (firstFrame != null) {
//      firstFrame = null;
//    }
//  }
//
//  // Visible for testing.
//  void onFrameReady(DelayTarget delayTarget) {
//    if (isCleared) {
//      handler.obtainMessage(FrameLoaderCallback.MSG_CLEAR, delayTarget).sendToTarget();
//      return;
//    }
//
//    if (delayTarget.getResource() != null) {
//      recycleFirstFrame();
//      DelayTarget previous = current;
//      current = delayTarget;
//      // The callbacks may unregister when onFrameReady is called, so iterate in reverse to avoid
//      // concurrent modifications.
//      for (int i = callbacks.size() - 1; i >= 0; i--) {
//        FrameCallback cb = callbacks.get(i);
//        cb.onFrameReady();
//      }
//      if (previous != null) {
//        handler.obtainMessage(FrameLoaderCallback.MSG_CLEAR, previous).sendToTarget();
//      }
//    }
//
//    isLoadPending = false;
//    loadNextFrame();
//  }
//
//  private class FrameLoaderCallback implements Handler.Callback {
//    public static final int MSG_DELAY = 1;
//    public static final int MSG_CLEAR = 2;
//
//    @Override
//    public boolean handleMessage(Message msg) {
//      if (msg.what == MSG_DELAY) {
//        GifFrameLoader.DelayTarget target = (DelayTarget) msg.obj;
//        onFrameReady(target);
//        return true;
//      } else if (msg.what == MSG_CLEAR) {
//        GifFrameLoader.DelayTarget target = (DelayTarget) msg.obj;
//      }
//      return false;
//    }
//  }
//
//  // Visible for testing.
//  static class DelayTarget {
//    private final Handler handler;
//    private final int index;
//    private final long targetTime;
//    private Bitmap resource;
//
//    DelayTarget(Handler handler, int index, long targetTime) {
//      this.handler = handler;
//      this.index = index;
//      this.targetTime = targetTime;
//    }
//
//    Bitmap getResource() {
//      return resource;
//    }
//
//    public void onResourceReady(Bitmap resource) {
//      this.resource = resource;
//      Message msg = handler.obtainMessage(FrameLoaderCallback.MSG_DELAY, this);
//      handler.sendMessageAtTime(msg, targetTime);
//    }
//  }
//
//  public static int getBitmapByteSize(int width, int height, Bitmap.Config config) {
//    return width * height * getBytesPerPixel(config);
//  }
//
//  private static int getBytesPerPixel(Bitmap.Config config) {
//    // A bitmap by decoding a gif has null "config" in certain environments.
//    if (config == null) {
//      config = Bitmap.Config.ARGB_8888;
//    }
//
//    int bytesPerPixel;
//    switch (config) {
//      case ALPHA_8:
//        bytesPerPixel = 1;
//        break;
//      case RGB_565:
//      case ARGB_4444:
//        bytesPerPixel = 2;
//        break;
//      case ARGB_8888:
//      default:
//        bytesPerPixel = 4;
//        break;
//    }
//    return bytesPerPixel;
//  }
//}
