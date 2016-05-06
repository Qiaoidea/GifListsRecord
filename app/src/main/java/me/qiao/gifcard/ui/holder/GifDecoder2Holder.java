package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import me.qiao.gifcard.util.FileUtil;
import me.qiao.giflib.decoder.lib2.GifImageView;

/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class GifDecoder2Holder extends QViewHolder<File>{
    GifImageView imageView;

    public GifDecoder2Holder(Context context) {
        super(new GifImageView(context));
        imageView = (GifImageView)itemView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setPadding(10, 10, 10, 10);
        imageView.setMinimumHeight(400);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
    }

    @Override
    public void bindViewHolder(File data) {
        super.bindViewHolder(data);
        imageView.clear();
    }

    @Override
    public void bindData(){
        try {
            imageView.setBytes(
                    FileUtil.readFileToByteArray(getData())
            );
            imageView.startAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
