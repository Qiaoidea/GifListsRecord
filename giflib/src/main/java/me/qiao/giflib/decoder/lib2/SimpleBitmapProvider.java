package me.qiao.giflib.decoder.lib2;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.qiao.giflib.decoder.lib2.bitmap.recycle.ArrayPool;
import me.qiao.giflib.decoder.lib2.bitmap.recycle.LruArrayPool;

final class SimpleBitmapProvider implements GifDecoder.BitmapProvider {
  Bitmap bitmap;
  @Nullable
  private ArrayPool arrayPool;

  public SimpleBitmapProvider(){
     arrayPool = new LruArrayPool();
  }

  @NonNull @Override public Bitmap obtain(int width, int height, Bitmap.Config config) {
    if(bitmap==null || bitmap.isRecycled() ){
      bitmap = Bitmap.createBitmap(width, height, config);
    }
    return bitmap;
  }

  @Override public void release(Bitmap bitmap) {
    bitmap.recycle();
  }

  @Override
  public byte[] obtainByteArray(int size) {
    if (arrayPool == null) {
      return new byte[size];
    }
    return arrayPool.get(size, byte[].class);
  }

  @SuppressWarnings("PMD.UseVarargs")
  @Override
  public void release(byte[] bytes) {
    if (arrayPool == null) {
      return;
    }
    arrayPool.put(bytes, byte[].class);
  }

  @Override
  public int[] obtainIntArray(int size) {
    if (arrayPool == null) {
      return new int[size];
    }
    return arrayPool.get(size, int[].class);
  }

  @SuppressWarnings("PMD.UseVarargs")
  @Override
  public void release(int[] array) {
    if (arrayPool == null) {
      return;
    }
    arrayPool.put(array, int[].class);
  }
}
