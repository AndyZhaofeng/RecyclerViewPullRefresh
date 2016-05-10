package com.example.zhaofeng.recyclerviewpullrefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by zhaofeng on 16/5/9.
 */
public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.DemoViewHolder>
{
    private Context context;
    private ArrayList<String> list;
    public DemoAdapter(ArrayList<String> list,Context context)
    {
        checkNotNull(list);
        this.context=context;
        this.list=list;
    }

    class DemoViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv;
        public DemoViewHolder(View itemView)
        {
            super(itemView);
            tv=(TextView)itemView.findViewById(R.id.item_tv);
        }
        public void addDetail(int position)
        {
            tv.setText(list.get(position));
        }
    }

    @Override
    public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.itemview,null);
        return new DemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DemoViewHolder holder, int position) {
            holder.addDetail(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
