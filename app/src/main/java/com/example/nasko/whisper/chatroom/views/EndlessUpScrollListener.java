package com.example.nasko.whisper.chatroom.views;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessUpScrollListener extends RecyclerView.OnScrollListener {

    private static final int LOAD_THRESHOLD = 5;

    private boolean loading = true;
    private int previousTotalItemCount;

    private LinearLayoutManager linearLayoutManager;
    private int loadThreshold;

    private EndlessUpScrollListener(LinearLayoutManager linearLayoutManager, int loadThreshold) {
        this.linearLayoutManager = linearLayoutManager;
        this.loadThreshold = loadThreshold;
    }

    protected EndlessUpScrollListener(LinearLayoutManager linearLayoutManager) {
        this(linearLayoutManager, LOAD_THRESHOLD);
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
        if (!loading && itemsAboveFirstVisibleItem <= loadThreshold) {
            loading = true;
            onLoadMore();
        }
    }

    public abstract void onLoadMore();
}