package com.example.zhaofeng.recyclerviewpullrefresh;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.common.collect.Lists;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.refreshpull)
    RecyclerViewRefresh recyclerViewRefresh;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;
    DemoAdapter demoAdapter;
    ArrayList<String> list;
    ViewPager viewPager;

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==0)
            {
                recyclerViewRefresh.setRefreshing(false);
                demoAdapter.notifyDataSetChanged();
            }
            return false;
        }
    });
    RecyclerViewRefresh.OnPullToRefresh pullToRefresh=new RecyclerViewRefresh.OnPullToRefresh() {
        @Override
        public void onRefresh() {
            for(int i=0;i<10;++i){
                list.add(i,"add"+i);
            }
            handler.sendEmptyMessageDelayed(0,3000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        list= Lists.newArrayList("test1","test2","test3","test4","test5",
                "test6","test7","test8","test9","test10","test11");
        demoAdapter=new DemoAdapter(list,this);
        recyclerView.setAdapter(demoAdapter);
        recyclerViewRefresh.setOnPullToRefresh(pullToRefresh);
    }
}
