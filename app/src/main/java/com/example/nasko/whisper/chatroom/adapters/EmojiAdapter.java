package com.example.nasko.whisper.chatroom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nasko.whisper.R;
import com.example.nasko.whisper.utils.ArrayRecyclerViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmojiAdapter extends ArrayRecyclerViewAdapter<String, EmojiAdapter.EmojiViewHolder> {

    class EmojiViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btn_visual) Button tvVisual;

        EmojiViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(view -> {
                if (getItemClickListener() != null) {
                    getItemClickListener().onItemClick(getAdapterPosition());
                }
            });
        }
    }

    public EmojiAdapter(Context context) {
        super(context);
    }

    @Override
    public EmojiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getInflater().inflate(R.layout.item_emoji, parent, false);
        return new EmojiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EmojiViewHolder holder, int position) {
        String emojiUnicode = getItem(position);
        holder.tvVisual.setText(emojiUnicode);
    }
}
