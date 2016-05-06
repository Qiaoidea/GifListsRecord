package me.qiao.gifcard.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class QViewHolder<T> extends RecyclerView.ViewHolder {
    T mData;

    public QViewHolder(View itemView) {
        super(itemView);
    }

    public void bindViewHolder(T data){
        this.mData = data;
    }

    public T getData() {
        return mData;
    }

    public void bindData(){}
}
