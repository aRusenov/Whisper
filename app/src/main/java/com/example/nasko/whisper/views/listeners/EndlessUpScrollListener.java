package com.example.nasko.whisper.views.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessUpScrollListener extends RecyclerView.OnScrollListener {

    private static final int LOAD_THRESHOLD = 5;

    private boolean loading = true;
    private int previousTotalItemCount;
    private int lastLoadedItemId = -1;

    private LinearLayoutManager linearLayoutManager;
    private int loadThreshold;

    public EndlessUpScrollListener(LinearLayoutManager linearLayoutManager, int loadThreshold) {
        this.linearLayoutManager = linearLayoutManager;
        this.loadThreshold = loadThreshold;
    }

    public EndlessUpScrollListener(LinearLayoutManager linearLayoutManager) {
        this(linearLayoutManager, LOAD_THRESHOLD);
    }

    public void setLastLoadedItemId(int itemId) {
        this.lastLoadedItemId = itemId;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        // Return if scrolling down
        if (dy > 0) {
            return;
        }

        int totalItemCount = linearLayoutManager.getItemCount();
        int itemsAboveFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

        // Check if it's still loading
        if (loading && totalItemCount > previousTotalItemCount)  {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it's no longer loading and the threshold is reached -> load more data
        if (!loading && itemsAboveFirstVisibleItem - loadThreshold <= 0) {
            loading = true;
            Log.v("LOADING", "Loading fresh messages");
            onLoadMore(lastLoadedItemId);
        }
    }

    public abstract void onLoadMore(int lastLoadedItemId);
}