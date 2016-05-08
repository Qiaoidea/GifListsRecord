package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.io.File;

import me.qiao.giflib.decoder.lib1.GifView;

/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class GifDecoder1Holder extends QViewHolder<File>{

    public GifDecoder1Holder(Context context) {
        super(new GifView(context));
        GifView imageView = (GifView)itemView;
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
        ((GifView)itemView).setGifFile(mData);
    }
}
