# RefreshListView
A RefreshListView for pull up and pull down refresh with the animations;

界面预览:
    
    

使用步骤:
1.在布局中引用

     <com.fmer.refresh.RefreshListView
        android:id="@+id/rlv_refresh"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        />
2.初始化控件
      
      
      RefreshListView rlv_show = (RefreshListView) findViewById(R.id.rlv_refresh);
      
3.设置是否显示刷新头和刷新尾,默认为不显示

      /**
  		 * 设置刷新头界面的显示
  		 * 		true 显示
  		 * 		false 不显示
  		 */
  		rlv_show.setHeadRefreshShow(true);
  		/**
  		 * 设置尾部加载界面的显示
  		 * 		true 显示
  		 * 		false 不显示
  		 */
  		rlv_show.setFoorRefreshShow(true);
4.设置适配器
      
       MyAdapter mAdapter = new MyAdapter();
	    rlv_show.setAdapter(mAdapter);
