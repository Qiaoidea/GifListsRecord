package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import me.qiao.gifcard.R;

/**
 * Created by Qiao on 2016/5/9.
 * function：
 */
public class GlideDecoderHolder extends QViewHolder<File> {

    public GlideDecoderHolder(Context context) {
        super(new ImageView(context));
        ImageView imageView = (ImageView)itemView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(10, 10, 10, 10);
        imageView.setMinimumHeight(400);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
    }

    @Override
    public void bindViewHolder(File data) {
        super.bindViewHolder(data);
    }

    @Override
    public void bindData() {
        super.bindData();
        Glide.with(itemView.getContext())
                .load(mData)
                .asGif()
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into((ImageView)itemView);

//        int frameWidth = itemView.getWidth();
//        int frameHeight = itemView.getHeight();
//        Bitmap firstFrame = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.RGB_565);
//        me.qiao.giflib.decoder.glide.GifDrawable drawable = new GifDrawable(itemView.getContext(),
//                new GifDecoder(),frameWidth,frameHeight,firstFrame),
//                bitmapPool, paint);
//        when(frameLoader.getWidth()).thenReturn(frameWidth);
//        when(frameLoader.getHeight()).thenReturn(frameHeight);
//        when(frameLoader.getCurrentFrame()).thenReturn(firstFrame);
//        when(frameLoader.getCurrentIndex()).thenReturn(0);
//        drawable.setCallback(cb);
    }
}
