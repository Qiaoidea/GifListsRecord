package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import me.qiao.gifcard.util.FileUtil;
import me.qiao.giflib.decoder.lib2.GifDrawable;

/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class GifDecoder2Holder extends QViewHolder<File>{
    ImageView imageView;
    GifDrawable gifDrawable;

    public GifDecoder2Holder(Context context) {
        super(new ImageView(context));
        imageView = (ImageView)itemView;
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(10, 10, 10, 10);
        imageView.setMinimumHeight(400);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
    }

    @Override
    public void bindViewHolder(File data) {
        super.bindViewHolder(data);
        if(gifDrawable!=null){
            gifDrawable.clear();
        }
    }

    @Override
    public void bindData(){
        try {
            imageView.setImageDrawable(
                    gifDrawable = new GifDrawable(FileUtil.readFileToByteArray(mData))
            );
            gifDrawable.startAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
