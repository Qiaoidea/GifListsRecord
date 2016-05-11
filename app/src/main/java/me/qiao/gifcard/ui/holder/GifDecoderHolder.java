package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;


/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class GifDecoderHolder extends QViewHolder<File> {

    public GifDecoderHolder(Context context) {
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
        ((ImageView)itemView).setImageDrawable(null);
    }

    @Override
    public void bindData() {
        super.bindData();
        try {
            ((ImageView)itemView).setImageDrawable(new GifDrawable(mData));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
