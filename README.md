# RecyclerViewPullRefresh
RecyclerView下拉刷新
还未完成（已经完成下拉刷新，开始写上拉加载）
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
