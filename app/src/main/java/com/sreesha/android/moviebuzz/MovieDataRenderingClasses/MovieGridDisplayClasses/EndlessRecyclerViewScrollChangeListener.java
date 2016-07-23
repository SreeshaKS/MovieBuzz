package com.sreesha.android.moviebuzz.MovieDataRenderingClasses.MovieGridDisplayClasses;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

/**
 * Created by Sreesha on 14-04-2016.
 */
@TargetApi(Build.VERSION_CODES.M)
public class EndlessRecyclerViewScrollChangeListener implements RecyclerView.OnScrollChangeListener {
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

    public EndlessRecyclerViewScrollChangeListener(
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
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (mStaggeredGridLayoutManager != null) {
            Log.e("RecycEndlessDebug", "Calculating");
            visibleItemCount = mRecyclerView.getChildCount();
            totalItemCount = mStaggeredGridLayoutManager.getItemCount();
            mLastVisibleItemArray = new int[mStaggeredGridLayoutManager.getSpanCount()];
            mLastVisibleItemArray = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItem(mLastVisibleItemArray);
            if (loading) {
                Log.e("RecycEndlessDebug", "Loading is true");
                if (totalItemCount > previousTotal) {
                    Log.e("RecycEndlessDebug", "Setting loading to false");
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
                Log.e("RecycEndlessDebug", "Requesting for more data");
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
