package me.qiao.giflib.decoder.lib2;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

final class SimpleBitmapProvider implements GifDecoder.BitmapProvider {
  Bitmap bitmap;

  @NonNull @Override public Bitmap obtain(int width, int height, Bitmap.Config config) {
    if(bitmap==null || bitmap.isRecycled() ){
      bitmap = Bitmap.createBitmap(width, height, config);
    }
    return bitmap;
  }

  @Override public void release(Bitmap bitmap) {
    bitmap.recycle();
  }

  @Override public byte[] obtainByteArray(int size) {
    return new byte[size];
  }

  @Override public void release(byte[] bytes) {
    // no-op
  }

  @Override public int[] obtainIntArray(int size) {
    return new int[size];
  }

  @Override public void release(int[] array) {
    // no-op
  }
}
