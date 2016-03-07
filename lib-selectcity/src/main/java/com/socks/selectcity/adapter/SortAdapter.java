package com.socks.selectcity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.socks.selectcity.R;
import com.socks.selectcity.bean.CityBean;

import java.util.List;


/**
 * 排序adapter
 */
public class SortAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int TYPE_TAG = 0;
    private final int TYPE_ITEM = 1;
    private List<CityBean> citys;
    private OnItemClickListener onItemClickListener = null;

    public SortAdapter(List<CityBean> _citys){
        this.citys = _citys;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_TAG){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag,parent,false);
            return new TagViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city,parent,false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof TagViewHolder){
            TagViewHolder tagViewHolder = (TagViewHolder)holder;
            tagViewHolder.setData(citys.get(position));
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            itemViewHolder.setData(citys.get(position));
        }

    }

    /**
     * 判断算法 第一位必须带tag，之后若是与前一位不同tag将将视为所有的开头
     */
    @Override
    public int getItemViewType(int position) {
        if(position == 0 || !citys.get(position).getSortLetters().equals(citys.get(position-1).getSortLetters())){
            return TYPE_TAG;
        }
        return TYPE_ITEM;
    }

    /** 判断是否第一个 这里有问题每次都是判断一下会出现卡顿 */
    public int getPositionForSection(int section) {
        int i = 0;
        for (CityBean cityBean:citys) {
            String sortStr = cityBean.getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return citys.size();
    }

    class TagViewHolder extends RecyclerView.ViewHolder{

        TextView tagText;
        TextView cityName;

        public TagViewHolder(View view) {
            super(view);
            tagText = (TextView)view.findViewById(R.id.tv_catagory);
            cityName = (TextView)view.findViewById(R.id.tv_city_name);
        }

        public void setData(final CityBean cityBean){
            tagText.setText(cityBean.getSortLetters().toUpperCase());
            cityName.setText(cityBean.getName());
            cityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(cityBean);
                    }
                }
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView cityName;

        public ItemViewHolder(View view) {
            super(view);
            cityName = (TextView)view.findViewById(R.id.tv_city_name);
        }

        public void setData(final CityBean cityBean){
            cityName.setText(cityBean.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(cityBean);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(CityBean cityBean);
    }
}
