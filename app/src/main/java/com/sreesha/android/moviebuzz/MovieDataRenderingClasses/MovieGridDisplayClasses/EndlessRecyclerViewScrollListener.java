package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by Sreesha on 23-07-2016.
 */
public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;


    private int previousTotal = 0;
    private static boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    int[] mLastVisibleItemArray;
    private int current_page = 1;
    int lastVisibleItemPosition;
    OnMoreDataRequestedListener mOnMoreDataRequestedListener;
    RecyclerView mRecyclerView;
    public EndlessRecyclerViewScrollListener(
            StaggeredGridLayoutManager staggeredGridLayoutManager
            , OnMoreDataRequestedListener onMoreDataRequestedListener
            , RecyclerView recyclerView) {
        this.mStaggeredGridLayoutManager = staggeredGridLayoutManager;
        this.mOnMoreDataRequestedListener = onMoreDataRequestedListener;
        mRecyclerView = recyclerView;
        mLastVisibleItemArray = new int[mStaggeredGridLayoutManager.getSpanCount()];
        visibleThreshold = visibleThreshold * mStaggeredGridLayoutManager.getSpanCount();
    }
    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (mStaggeredGridLayoutManager != null) {
            visibleItemCount = mRecyclerView.getChildCount();
            totalItemCount = mStaggeredGridLayoutManager.getItemCount();
            mLastVisibleItemArray = new int[mStaggeredGridLayoutManager.getSpanCount()];
            mLastVisibleItemArray = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItem(mLastVisibleItemArray);
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (
                    (totalItemCount - visibleItemCount)
                            <= (mLastVisibleItemArray[0] + visibleThreshold)
                            /*|| (totalItemCount - visibleItemCount)
                            <= (mLastVisibleItemArray[1] + visibleThreshold)*/
            )) {
                current_page++;

                mOnMoreDataRequestedListener.onMoreDataRequested(current_page);

                loading = true;
            }
        }
    }
    public static void setLoadingToFalse() {
        loading = false;
    }
}
