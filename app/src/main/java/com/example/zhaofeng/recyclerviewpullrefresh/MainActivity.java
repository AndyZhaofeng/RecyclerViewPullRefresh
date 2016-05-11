package com.example.zhaofeng.recyclerviewpullrefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    }
}
