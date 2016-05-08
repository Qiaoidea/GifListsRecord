package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.io.File;

import me.qiao.giflib.decoder.lib0.TypegifView;

/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class GifDecoder0Holder extends QViewHolder<File>{

    public GifDecoder0Holder(Context context) {
        super(new TypegifView(context));
        TypegifView imageView = (TypegifView)itemView;
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
        ((TypegifView)itemView).setFile(mData);
    }
}
