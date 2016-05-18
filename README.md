# RecyclerViewPullRefresh
RecyclerView下拉刷新,上拉加载
完成（已经完成下拉刷新，上拉加载）

目前进度：

2016-05-11 仿照SwipeRefreshLayout(23.3.0)中的onInterceptionTouchEvent和onTouchEvent中的
逻辑重写RecyclerViewRefresh中对应的两个函数逻辑

2016-05-12上午：
修改5月11号Recyclerview不能随手指移动的总是，添加松开手指后返回原位动画。

2016-05-13：
昨天下午遇到一个总是，找不到原因，后来发现是使用offsetTopAndBottom()后，轻开手指位置会自动还原。现在还没有找到原因，但是
用setY()代替了offsetTopAndBottom()。
现在完成下拉刷新

2016-05-16：
开始写上拉加载。
第二次提交：修改滑到底部不能上划的问题。

2016-05-17：
开始添加上拉加载，但是遇到一个棘手的问题，用addView添加view之后，不显示。

2016-05-18:
完成上拉加载

使用如何使用：


    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==0)
            {
                recyclerViewRefresh.setRefreshing(false);
                demoAdapter.notifyDataSetChanged();
            }else if(msg.what==1)
            {
                recyclerViewRefresh.setLoading(false);
                demoAdapter.notifyDataSetChanged();
            }
            return false;
        }
    });
    RecyclerViewRefresh.OnPullToRefresh pullToRefresh=new RecyclerViewRefresh.OnPullToRefresh() {
        @Override
        public void onRefresh() {
            for(int i=0;i<10;++i){
                list.add(i,"add"+(topAdd++));
            }
            handler.sendEmptyMessageDelayed(0,3000);
        }
    };

    RecyclerViewRefresh.OnDragToLoad loadToRefresh=new RecyclerViewRefresh.OnDragToLoad() {
        @Override
        public void onLoad() {
            for(int i=0;i<10;++i)
            {
                list.add("load"+(endAdd++));
            }
            handler.sendEmptyMessageDelayed(1,1500);
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
        recyclerViewRefresh.setOnDragToLoad(loadToRefresh);
    }
