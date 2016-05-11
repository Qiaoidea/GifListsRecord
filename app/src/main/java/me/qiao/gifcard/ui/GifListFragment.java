package me.qiao.gifcard.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.io.File;

import me.qiao.gifcard.api.GifDataApi;
import me.qiao.gifcard.ui.holder.GifDecoder0Holder;
import me.qiao.gifcard.ui.holder.GifDecoder1Holder;
import me.qiao.gifcard.ui.holder.GifDecoder2Holder;
import me.qiao.gifcard.ui.holder.GifDecoderDrawableHolder;
import me.qiao.gifcard.ui.holder.GifDecoderHolder;
import me.qiao.gifcard.ui.holder.GifMovie0Holder;
import me.qiao.gifcard.ui.holder.GifMovie1Holder;
import me.qiao.gifcard.ui.holder.GlideDecoderHolder;
import me.qiao.gifcard.ui.holder.QViewHolder;

/**
 * Created by Qiao on 2016/5/5.
 * function：
 */
public class GifListFragment extends Fragment{
    private RecyclerView mRcyclerView;
    private int type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRcyclerView = new RecyclerView(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRcyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
//        mRcyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRcyclerView.setHasFixedSize(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        type = getArguments().getInt("type",0);

        final File[] files = GifDataApi.getGifOffline();
        mRcyclerView.setAdapter(new RecyclerView.Adapter() {

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (type){
                    case 0:
                        return new GifDecoderHolder(getContext());
                    case 1:
                        return new GifMovie0Holder(getContext());
                    case 2:
                        return new GifMovie1Holder(getContext());
                    case 3:
                        return new GifDecoder0Holder(getContext());
                    case 4:
                        return new GifDecoder1Holder(getContext());
                    case 5:
                        return new GifDecoder2Holder(getContext());
//                        return new GifDrawableDecoder2Holder(getContext());
                    case 6:
                        return new GifDecoderDrawableHolder(getContext());
                    default:
                        return new GlideDecoderHolder(getContext());
                }
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    ((QViewHolder<File>)holder).bindViewHolder(files[position]);
            }

            @Override
            public int getItemCount() {
                return files.length;
            }
        });

        mRcyclerView.addOnScrollListener(mOnScrollListener);
        mRcyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mRcyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else{
                    mRcyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                loadImages();
            }
        });
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            switch (newState){
                case RecyclerView.SCROLL_STATE_IDLE:
                    loadImages();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    int mFirst=0,mLast=0;
    private void loadImages(){
        final RecyclerView.LayoutManager layoutManager = mRcyclerView.getLayoutManager();
        int first=0,last=0;
        if(layoutManager instanceof LinearLayoutManager) {
            first =  ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            last = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }else if(layoutManager instanceof GridLayoutManager){
            first =  ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            last = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            first =  ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null)[0];
            last = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null)[1];
        }

        if(mFirst == first && mLast == last) return;
        mFirst = first;
        mLast = last;
        for(int i=first;i<=last;i++){
            final RecyclerView.ViewHolder holder = mRcyclerView.findViewHolderForAdapterPosition(i);
            if(! (holder instanceof QViewHolder)) continue;
            ((QViewHolder) holder).bindData();
        }
    }
}
