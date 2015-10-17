package com.fmer.refresh;

import java.util.ArrayList;
import java.util.List;

import com.fmer.refresh.RefreshListView.OnUpdataStateListener;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

	private RefreshListView rlv_show;
	private List<String> mDatas = new ArrayList<String>();
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();

	}

	private Handler handler = new Handler();

	private void initEvent() {
		rlv_show.setOnUpdataStateListener(new OnUpdataStateListener() {

			@Override
			public void onUpdataState() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						rlv_show.refreshState();
					}
				}, 2000);
			}

			@Override
			public void onLoadMoreState() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						rlv_show.refreshState();
					}
				}, 2000);
			}
		});
	}

	/**
	 * 随机添加一些数据
	 * 
	 */
	private void initData() {
		int res = 11111;
		for (int i = 0; i < 30; i++) {
			res = 11111 + i;
			mDatas.add(res + "");
		}

		mAdapter = new MyAdapter();
		rlv_show.setAdapter(mAdapter);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.refresh_listview_item, null);

				holder = new ViewHolder();

				holder.testText = (TextView) convertView
						.findViewById(R.id.tv_test);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.testText.setText(mDatas.get(position));

			return convertView;
		}

	}

	public class ViewHolder {
		public TextView testText;
	}

	// 初始化页面
	private void initView() {
		setContentView(R.layout.activity_main);
		rlv_show = (RefreshListView) findViewById(R.id.rlv_refresh);

		rlv_show.setHeadRefreshShow(true);
		rlv_show.setFoorRefreshShow(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
