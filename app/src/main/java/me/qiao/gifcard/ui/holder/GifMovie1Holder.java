package me.qiao.gifcard.ui.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.io.File;

import me.qiao.gifcard.R;
import me.qiao.giflib.movie.lib1.MovieImageView;

/**
 * Created by Qiao on 2016/5/6.
 * function：
 */
public class GifMovie1Holder extends QViewHolder<File> {

    public GifMovie1Holder(Context context) {
        super(new MovieImageView(context));
        MovieImageView imageView = (MovieImageView)itemView;
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
        ((ImageView)itemView).setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public void bindData() {
        super.bindData();
        ((MovieImageView)itemView).setGifFile(mData);
    }
}
