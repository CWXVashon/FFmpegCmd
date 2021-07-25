package com.example.ffmpegcmd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapterRV extends RecyclerView.Adapter {

    private static final int ITEM_TYPE_TITLE = 435;
    private static final int ITEM_TYPE_ITEM = 436;
    private static final int ITEM_TYPE_DIVIDER = 437;

    private List mList;
    private OnItemClickListener mItemClickListener;

    public void setList(List list) {
        mList = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onItemClick(String name);
    }

    public MainAdapterRV(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            case ITEM_TYPE_TITLE:
                return new TitleHolder(inflater.inflate(R.layout.item_main_title, viewGroup, false));
            case ITEM_TYPE_ITEM:
                return new ItemHolder(inflater.inflate(R.layout.item_main_item, viewGroup, false));
            case ITEM_TYPE_DIVIDER:
                return new DividerHolder(inflater.inflate(R.layout.item_main_divider, viewGroup, false));
            default:
                return new TitleHolder(inflater.inflate(R.layout.item_main_title, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_TITLE:
                TitleHolder titleHolder = (TitleHolder) viewHolder;
                titleHolder.setData((MainTitleBean) mList.get(position));
                break;
            case ITEM_TYPE_ITEM:
                ItemHolder itemHolder = (ItemHolder) viewHolder;
                itemHolder.setData((ArrayList<MainItemBean>)mList.get(position));
                itemHolder.setListener();
                break;
            case ITEM_TYPE_DIVIDER:
                DividerHolder dividerHolder = (DividerHolder) viewHolder;
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object o = mList.get(position);
        if (o instanceof MainTitleBean)
            return ITEM_TYPE_TITLE;
        else if (o instanceof ArrayList)
            return ITEM_TYPE_ITEM;
        else if (o instanceof MainDividerBean)
            return ITEM_TYPE_DIVIDER;
        return super.getItemViewType(position);
    }

    private class TitleHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTv;

        TitleHolder(View itemView) {
            super(itemView);
            mTitleTv = itemView.findViewById(R.id.title_tv);
        }

        private void setData(MainTitleBean bean) {
            mTitleTv.setText(bean.getTitle());
        }
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mItem1Tv;
        private TextView mItem2Tv;
        private TextView mItem3Tv;
        private TextView mItem4Tv;

        ItemHolder(View itemView) {
            super(itemView);
            mItem1Tv = itemView.findViewById(R.id.item1_tv);
            mItem2Tv = itemView.findViewById(R.id.item2_tv);
            mItem3Tv = itemView.findViewById(R.id.item3_tv);
            mItem4Tv = itemView.findViewById(R.id.item4_tv);
        }

        private void setData(ArrayList<MainItemBean> list) {
            int size = list.size();
            mItem1Tv.setText(list.get(0).getName());
            mItem1Tv.setCompoundDrawablesWithIntrinsicBounds(0, list.get(0).getResId(), 0, 0);
            switch (size) {
                case 1:
                    mItem2Tv.setVisibility(View.INVISIBLE);
                    mItem3Tv.setVisibility(View.INVISIBLE);
                    mItem4Tv.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    mItem2Tv.setVisibility(View.VISIBLE);
                    mItem2Tv.setText(list.get(1).getName());
                    mItem2Tv.setCompoundDrawablesWithIntrinsicBounds(0, list.get(1).getResId(), 0, 0);
                    mItem3Tv.setVisibility(View.INVISIBLE);
                    mItem4Tv.setVisibility(View.INVISIBLE);
                    break;
                case 3:
                    mItem2Tv.setVisibility(View.VISIBLE);
                    mItem2Tv.setText(list.get(1).getName());
                    mItem2Tv.setCompoundDrawablesWithIntrinsicBounds(0, list.get(1).getResId(), 0, 0);
                    mItem3Tv.setVisibility(View.VISIBLE);
                    mItem3Tv.setText(list.get(2).getName());
                    mItem3Tv.setCompoundDrawablesWithIntrinsicBounds(0, list.get(2).getResId(), 0, 0);
                    mItem4Tv.setVisibility(View.INVISIBLE);
                    break;
                case 4:
                    mItem2Tv.setVisibility(View.VISIBLE);
                    mItem2Tv.setText(list.get(1).getName());
                    mItem2Tv.setCompoundDrawablesWithIntrinsicBounds(0, list.get(1).getResId(), 0, 0);
                    mItem3Tv.setVisibility(View.VISIBLE);
                    mItem3Tv.setText(list.get(2).getName());
                    mItem3Tv.setCompoundDrawablesWithIntrinsicBounds(0, list.get(2).getResId(), 0, 0);
                    mItem4Tv.setVisibility(View.VISIBLE);
                    mItem4Tv.setText(list.get(3).getName());
                    mItem4Tv.setCompoundDrawablesWithIntrinsicBounds(0, list.get(3).getResId(), 0, 0);
                    break;
            }
        }

        private void setListener() {
            mItem1Tv.setOnClickListener(this);
            mItem2Tv.setOnClickListener(this);
            mItem3Tv.setOnClickListener(this);
            mItem4Tv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item1_tv:
                    if (mItemClickListener != null)
                        mItemClickListener.onItemClick(mItem1Tv.getText().toString());
                    break;
                case R.id.item2_tv:
                    if (mItemClickListener != null)
                        mItemClickListener.onItemClick(mItem2Tv.getText().toString());
                    break;
                case R.id.item3_tv:
                    if (mItemClickListener != null)
                        mItemClickListener.onItemClick(mItem3Tv.getText().toString());
                    break;
                case R.id.item4_tv:
                    if (mItemClickListener != null)
                        mItemClickListener.onItemClick(mItem4Tv.getText().toString());
                    break;
            }
        }
    }

    private class DividerHolder extends RecyclerView.ViewHolder {

        DividerHolder(View itemView) {
            super(itemView);
        }
    }
}
