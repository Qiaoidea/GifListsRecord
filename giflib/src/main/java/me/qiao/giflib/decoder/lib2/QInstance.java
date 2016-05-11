package me.qiao.giflib.decoder.lib2;

import me.qiao.giflib.decoder.lib2.bitmap.recycle.ArrayPool;
import me.qiao.giflib.decoder.lib2.bitmap.recycle.BitmapPool;
import me.qiao.giflib.decoder.lib2.bitmap.recycle.LruArrayPool;
import me.qiao.giflib.decoder.lib2.bitmap.recycle.LruBitmapPool;

/**
 * Created by Qiao on 2016/5/11.
 * function：
 */
public class QInstance {
    private static BitmapPool bitmapPool = new LruBitmapPool(10*1024*1024);
    private static ArrayPool arrayPool = new LruArrayPool();

    public static BitmapPool getBitmapPool(){
        return bitmapPool;
    }

    public static ArrayPool getArrayPool(){
        return arrayPool;
    }
}
