package com.example.nasko.whisper.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class AbstractDataBinder<T extends RecyclerView.ViewHolder> {

    private MessageAdapter adapter;

    public AbstractDataBinder(MessageAdapter adapter) {
        this.adapter = adapter;
    }

    abstract public T newViewHolder(ViewGroup parent);

    abstract public void bindViewHolder(T holder, int position);

    abstract public int getItemCount();
}
