package com.p3.bartheway.Browse;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.p3.bartheway.Item;
import com.p3.bartheway.R;

import java.util.ArrayList;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.RecyclerViewAdapter> {

    private OnClickListener mOnClickListener;

    ArrayList<Item> mItemArrayList;

    public ItemRecyclerAdapter(ArrayList<Item> itemArrayList, OnClickListener onClickListener){
        this.mItemArrayList = itemArrayList;
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
        Item item = mItemArrayList.get(i);

        recyclerViewAdapter.mTextViewTitle.setText(item.getTitle());
        recyclerViewAdapter.mTextViewLanguage.setText(item.getLanguage());
        recyclerViewAdapter.mTextViewPlayers.setText(""+item.getMaxPlayers());
        recyclerViewAdapter.mImageView.setImageResource(R.drawable.arduino_with_sensor);

    }

    public Item getItem(int position){
        return mItemArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return mItemArrayList.size();
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
