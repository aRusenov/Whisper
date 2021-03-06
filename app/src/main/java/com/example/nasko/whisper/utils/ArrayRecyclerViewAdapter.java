package com.example.nasko.whisper.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class ArrayRecyclerViewAdapter<E, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements Iterable<E> {

    private Context context;
    private LayoutInflater inflater;
    private OnItemClickListener listener;
    protected List<E> items;

    public ArrayRecyclerViewAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.items = new ArrayList<>();
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public OnItemClickListener getItemClickListener() {
        return this.listener;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }

        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public E getItem(int position) {
        return this.items.get(position);
    }

    public int size() {
        return items.size();
    }

    public void add(E item) {
        this.items.add(item);
        this.notifyItemInserted(this.getItemCount());
    }

    public void add(int position, E item) {
        this.items.add(position, item);
        this.notifyItemInserted(position);
    }

    public void addAll(Collection<E> elements) {
        this.items.addAll(elements);
        this.notifyDataSetChanged();
    }

    public void addAllAt(int position, Collection<E> elements) {
        this.items.addAll(position, elements);
        this.notifyItemRangeInserted(position, elements.size());
    }

    public void removeAt(int position) {
        this.items.remove(position);
        this.notifyItemRemoved(position);
    }

    public void clear() {
        this.items.clear();
        this.notifyDataSetChanged();
    }

    public E last() {
        return items.get(items.size() - 1);
    }

    @Override
    public Iterator<E> iterator() {
        return items.iterator();
    }
}
