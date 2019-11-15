package com.p3.bartheway.Browse;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.p3.bartheway.Database.Item;
import com.p3.bartheway.R;

import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.RecyclerViewAdapter> {

    private OnClickListener mOnClickListener;

    List<Item> mItemList;

    public ItemRecyclerAdapter(List<Item> itemList, OnClickListener onClickListener){
        this.mItemList = itemList;
        this.mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false);
        return new RecyclerViewAdapter(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter recyclerViewAdapter, int i) {
        Item item = mItemList.get(i);

        recyclerViewAdapter.mTextViewTitle.setText(item.getTitle());
        recyclerViewAdapter.mTextViewLanguage.setText(item.getLanguage());
        recyclerViewAdapter.mTextViewPlayers.setText(item.getMinPlayers()+"-"+item.getMaxPlayers());
        recyclerViewAdapter.mImageView.setImageResource(R.drawable.arduino_with_sensor);

    }

    public Item getItem(int position){
        return mItemList.get(position);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTextViewTitle, mTextViewLanguage, mTextViewPlayers;
        private ImageView mImageView;
        OnClickListener mOnClickListener;

        public RecyclerViewAdapter(@NonNull View itemView, OnClickListener onClickListener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.item_image);
            mTextViewTitle = itemView.findViewById(R.id.item_title);
            mTextViewLanguage = itemView.findViewById(R.id.item_language);
            mTextViewPlayers = itemView.findViewById(R.id.item_players);
            this.mOnClickListener = onClickListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mOnClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnClickListener{
        void onItemClick(int position);
    }

}
