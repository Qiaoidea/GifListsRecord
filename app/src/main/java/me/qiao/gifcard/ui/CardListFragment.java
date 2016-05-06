package me.qiao.gifcard.ui;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import me.qiao.gifcard.R;
import me.qiao.gifcard.api.GifDataApi;
import me.qiao.gifcard.bean.GifBean;
import me.qiao.gifcard.ui.view.PacmanFooter;

/**
 * Created by Qiao on 2016/5/4.
 * function：
 */
public class CardListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View mRootView;
    private SwipeRefreshLayout mSwipelayout;
    private RecyclerView mRcyclerView;
    private List<GifBean> mData;
    private int pageNo = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mRootView = inflater.inflate(R.layout.content_main,container,false);
    }

    protected <T extends View> T findView(int resId) {
        return (T) mRootView.findViewById(resId);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipelayout = findView(R.id.swipelayout);
        mSwipelayout.setOnRefreshListener(this);

        mRcyclerView = findView(R.id.recycler);
        mRcyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRcyclerView.setHasFixedSize(true);

        mRcyclerView.addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh();
    }

    /**
     * 异步加载第pageNo页数据
     * @param pageNo
     */
    private void loadData(final int pageNo){
        new AsyncTask<Integer,Void,List<GifBean>>(){

            @Override
            protected List<GifBean> doInBackground(Integer ... params) {
                return  GifDataApi.getGifOnline(params[0]);
            }

            @Override
            protected void onPostExecute(List<GifBean> lists) {
                super.onPostExecute(lists);
                bindAdapter(lists,pageNo);
            }
        }.execute(pageNo);
    }

    @Override
    public void onRefresh() {
        mSwipelayout.setRefreshing(true);
        loadData(pageNo=1);
    }

    void loadMore(){
        loadData(++pageNo);
    }

    /**
     * 绑定加载更多的 吃豆人 尾部
     */
    private PacmanFooter footer;
    private void bindAdapter(List<GifBean> list,int pageNo){
        RecyclerView.Adapter adapter = mRcyclerView.getAdapter();
        if(adapter != null){
            if(pageNo==1) {
                mData.clear();
                mData.addAll(list);
                adapter.notifyDataSetChanged();
                refreshComplete();
            }else{
                footer.stopAnim();
                int size = mData.size();
                mData.addAll(list);
                adapter.notifyItemRangeInserted(size, list.size());
            }
        }else{
            mData = list;
            mRcyclerView.setAdapter(new RecyclerView.Adapter() {
                private final int FOOTER=-1;
                private final int ITEM = -2;

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    switch (viewType){
                        case FOOTER:
                            return new RecyclerView.ViewHolder(footer = new PacmanFooter(getContext())){};
//                        case ITEM:
                        default:
                            return new ItemViewHolder(
                                    LayoutInflater.from(getContext())
                                            .inflate(R.layout.gifs_item_layout,parent,false));
                    }
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                    if(holder instanceof ItemViewHolder){
                        final GifBean bean = mData.get(position);
                        final ItemViewHolder qViewHolder = (ItemViewHolder)holder;
                        qViewHolder.textView.setText(bean.getDesc());
                        qViewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
                    }else{
                        footer.startAnim();
                        loadMore();
                    }
                }

                @Override
                public int getItemCount() {
                    return mData.size()+1;
                }

                @Override
                public int getItemViewType(int position) {
                    return (position == mData.size())? FOOTER:ITEM;
                }
            });
            refreshComplete();
        }
    }

    /**
     * 刷新完成，等待RecyclerView 重新layout完成后显示当前页面的几张图片
     */
    private void refreshComplete(){
        mSwipelayout.setRefreshing(false);
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

    /**
     * 滑动停止后加载当前显示图片】
     */
    private void loadImages(){
        final LinearLayoutManager layoutManager = ((LinearLayoutManager) mRcyclerView.getLayoutManager());
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        for(int i=first;i<=last;i++){
            final RecyclerView.ViewHolder holder = mRcyclerView.findViewHolderForAdapterPosition(i);
            if(! (holder instanceof ItemViewHolder)) continue;
            final GifBean bean = mData.get(i);
            final ItemViewHolder qViewHolder = (ItemViewHolder)holder;
            Glide.with(CardListFragment.this)
                    .load(bean.getUrl())
                    .asGif()
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(qViewHolder.imageView);
            Log.i("loadUrl.... ", bean.getUrl());
        }
    }

    /**
     * 滑动事件监听
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            switch (newState){
                case RecyclerView.SCROLL_STATE_IDLE:
                    Glide.with(CardListFragment.this)
                            .resumeRequests();
                    loadImages();
                    break;
                default:
                    Glide.with(CardListFragment.this)
                            .pauseRequests();
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     * 列表
     */
    private class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        ItemViewHolder(View view){
            super(view);
            imageView = findView(R.id.gif_img);
            textView = findView(R.id.gif_text);
        }

        protected <T extends View> T findView(int resId) {
            return (T) itemView.findViewById(resId);
        }
    }

}
