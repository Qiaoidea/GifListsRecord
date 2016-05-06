package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import me.qiao.giflib.decoder.drawable.GifAnimationDrawable;

/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class ImageHolder extends QViewHolder<File> {

    public ImageHolder(Context context) {
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

    public void bindData(){
        try {
            GifAnimationDrawable drawable = new GifAnimationDrawable(mData);
            ((ImageView)itemView).setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
