package com.p3.bartheway.Browse;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.p3.bartheway.Database.Item;
import com.p3.bartheway.R;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.RecyclerViewAdapter> implements Filterable {

    private OnClickListener mOnClickListener;
    private Context context;
    int selected_position = -1;

    List<Item> mItemList;
    List<Item> mItemListFilter;

    public ItemRecyclerAdapter(List<Item> itemList, OnClickListener onClickListener, Context context){
        this.mItemList = itemList;
        this.mOnClickListener = onClickListener;
        this.context = context;
        this.mItemListFilter = new ArrayList<>(itemList);
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
        if(item.getCardUid() < 0){
            recyclerViewAdapter.itemView.setBackgroundColor(selected_position == i ? Color.GREEN : Color.TRANSPARENT);
        } else {
            recyclerViewAdapter.itemView.setBackgroundColor(selected_position != i ? Color.RED : Color.TRANSPARENT);
        }

        recyclerViewAdapter.mTextViewTitle.setText(item.getTitle());
        recyclerViewAdapter.mTextViewLanguage.setText(item.getLanguage());
        recyclerViewAdapter.mTextViewPlayers.setText(item.getMinPlayers()+"-"+item.getMaxPlayers());
        String title = item.getTitle();
        title = title.replaceAll(" ", "_");
        title = title.replaceAll("'", "");
        title = title.toLowerCase();
        Resources res = context.getResources();
        String mDrawableName = title;
        int resID = res.getIdentifier(mDrawableName , "drawable", context.getPackageName());
        recyclerViewAdapter.mImageView.setImageResource(resID);

    }

    public Item getItem(int position){
        return mItemList.get(position);
    }

    public void setSelected_position(int selected_position) {
        this.selected_position = selected_position;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public Filter getFilter() {
        return filterList;
    }

    private Filter filterList = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Item> filteredList = new ArrayList<>();

            if(constraint== null || constraint.length() == 0){
                filteredList.addAll(mItemListFilter);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Item item : mItemListFilter){
                    if(item.getTitle().toLowerCase().startsWith(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mItemList.clear();
            mItemList.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

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
            if (getAdapterPosition() == RecyclerView.NO_POSITION || getItem(getAdapterPosition()).getCardUid() > 0) return;
            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);
        }
    }

    public interface OnClickListener{
        void onItemClick(int position);
    }

    public void updateList(ArrayList<Item> items){
        mItemList = new ArrayList<>();
        mItemList.addAll(items);
        notifyDataSetChanged();
    }

}
