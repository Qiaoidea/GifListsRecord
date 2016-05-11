package me.qiao.giflib.decoder.lib2.bitmap.recycle;

import java.util.Arrays;

/**
 * Adapter for handling primitive int arrays.
 */
@SuppressWarnings("PMD.UseVarargs")
public final class IntegerArrayAdapter implements ArrayAdapterInterface<int[]> {
  private static final String TAG = "IntegerArrayPool";

  @Override
  public String getTag() {
    return TAG;
  }

  @Override
  public int getArrayLength(int[] array) {
    return array.length;
  }

  @Override
  public void resetArray(int[] array) {
    Arrays.fill(array, 0);
  }

  @Override
  public int[] newArray(int length) {
    return new int[length];
  }

  @Override
  public int getElementSizeInBytes() {
    return 4;
  }
}
