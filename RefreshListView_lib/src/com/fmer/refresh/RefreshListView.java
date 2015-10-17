package com.fmer.refresh;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.SystemClock;
import android.provider.ContactsContract.Contacts.Data;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RefreshListView extends ListView {

	private LinearLayout updataHeadView;
	private LinearLayout updataFootView;
	private float downY = -1;
	private int updataHeadViewHeight;
	private int updataFootViewHeight;
	private View mLunbo;

	private static final int MOVEDOWN_STATE = 1;
	private static final int REFRUSH_STATE = 2;
	private static final int MOVEUP_STATE = 3;

	private int updata_state;
	private ProgressBar headprogress;
	private ImageView headArrow;
	private TextView headDesc;
	private TextView headTime;
	private ProgressBar footprogress;
	private TextView footDesc;
	private TextView footTime;
	private int move;
	private RotateAnimation upAni;
	private RotateAnimation downAni;

	private boolean isLoadMore = false;

	private boolean mIsHeadShow = false;
	private boolean mIsFootShow = false;

	/**
	 * 设置下拉刷新头是否显示,默认为不显示
	 * 
	 * @param isHeadShow
	 *            true 显示 false 不显示
	 */
	public void setHeadRefreshShow(boolean isHeadShow) {
		this.mIsHeadShow = isHeadShow;
	}

	/**
	 * 设置上拉加载更多是否显示,默认为不显示
	 * 
	 * @param isFoorShow
	 *            true 显示 false 不显示
	 */
	public void setFoorRefreshShow(boolean isFoorShow) {
		this.mIsFootShow = isFoorShow;
	}

	private void initEvent() {
		this.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (!mIsFootShow) {
					// 如果不显示上拉加载更多则不执行下拉滑动事件操作
					return;
				}
				if (scrollState == SCROLL_STATE_IDLE) {
					if (getLastVisiblePosition() == getCount() - 1
							&& !isLoadMore) {
						// 显示下拉刷新
						isLoadMore = true;
						updataFootView.setPadding(0, 0, 0, 0);
						// getItemAtPosition(getCount());
						setSelection(getCount());
						if (mOnUpdataStateListener != null) {
							mOnUpdataStateListener.onLoadMoreState();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mIsHeadShow) {
			return super.onTouchEvent(ev);// 如果不显示下拉刷新,则不执行以下点击代码
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (updata_state == REFRUSH_STATE) {
				return true;
			}

			// if (!isShowLunboAll()) {
			// break;
			// }
			if (downY == -1) {
				downY = ev.getY();
			}

			float moveY = ev.getY();
			float dy = moveY - downY;

			if (dy > 0 && getFirstVisiblePosition() == 0) {
				move = (int) (dy - updataHeadViewHeight);
				if (move < 0 && updata_state != MOVEDOWN_STATE) {// 下拉刷新
					updata_state = MOVEDOWN_STATE;
					processUpdataState();
				} else if (move >= 0 && updata_state != MOVEUP_STATE) {// 松开刷新
					updata_state = MOVEUP_STATE;
					processUpdataState();
				}
				updataHeadView.setPadding(0, move, 0, 0);
				return true;
			}
			// updataHeadView.setPadding(0, move, 0, 0);
			break;
		case MotionEvent.ACTION_UP:
			if (updata_state == MOVEDOWN_STATE) {
				updataHeadView.setPadding(0, -updataHeadViewHeight, 0, 0);
			} else if (updata_state == MOVEUP_STATE) {
				updata_state = REFRUSH_STATE;

				processUpdataState();

				if (mOnUpdataStateListener != null) {
					mOnUpdataStateListener.onUpdataState();
				}
				updataHeadView.setPadding(0, 0, 0, 0);
			}
			break;
		default:
			break;
		}

		return super.onTouchEvent(ev);
	}

	private void processUpdataState() {
		headTime.setText(getCurrentTime());
		switch (updata_state) {
		case MOVEDOWN_STATE:
			// 显示下拉刷新
			// headprogress.startAnimation(upAni);
			headArrow.startAnimation(downAni);
			headDesc.setText("下拉刷新");
			break;
		case MOVEUP_STATE:
			headArrow.startAnimation(upAni);
			headDesc.setText("松开刷新");

			break;

		case REFRUSH_STATE:

			headArrow.clearAnimation();
			headArrow.setVisibility(View.GONE);
			headprogress.setVisibility(View.VISIBLE);
			headDesc.setText("正在刷新");

			break;

		default:
			break;
		}
	}

	public void refreshState() {
		if (isLoadMore) {
			// 加载更多数据
			isLoadMore = false;
			updataFootView.setPadding(0, -updataHeadViewHeight, 0, 0);
			// System.out.println("隐藏加载更多数据");
		} else {
			updata_state = MOVEDOWN_STATE;
			headArrow.setVisibility(View.VISIBLE);
			headprogress.setVisibility(View.GONE);
			headDesc.setText("下拉刷新");
			updataHeadView.setPadding(0, -updataHeadViewHeight, 0, 0);
		}
	}

	// 暴露一个接口显示当前刷新的状态
	public interface OnUpdataStateListener {
		public void onUpdataState();

		public void onLoadMoreState();
	};

	private OnUpdataStateListener mOnUpdataStateListener;

	public void setOnUpdataStateListener(
			OnUpdataStateListener onUpdataStateListener) {
		this.mOnUpdataStateListener = onUpdataStateListener;
	}

	// 获取当前时间
	public String getCurrentTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date());
	}

	// 初始化点击动画
	private void initAnimation() {
		upAni = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAni.setDuration(500);
		upAni.setFillAfter(true);

		downAni = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		downAni.setDuration(500);
		downAni.setFillAfter(true);
	}

	// 判断轮播图是否显示完全
	public boolean isShowLunboAll() {
		int[] location = new int[2];
		this.getLocationInWindow(location);
		int ly_win = location[1];

		mLunbo.getLocationInWindow(location);
		int ly_lunbo = location[1];

		return ly_lunbo > ly_win;
	}

	private void initView() {
		initHead();
		initFoot();
	}

	/**
	 * 初始化加载尾巴的布局
	 */
	private void initFoot() {
		updataFootView = (LinearLayout) View.inflate(getContext(),
				R.layout.updata_foot_layout, null);

		footprogress = (ProgressBar) updataFootView
				.findViewById(R.id.pb_newsupdata_footprogress);
		footDesc = (TextView) updataFootView
				.findViewById(R.id.tv_newsupdata_footdesc);
		footTime = (TextView) updataFootView
				.findViewById(R.id.tv_newsupdata_foottime);

		updataFootView.measure(0, 0);
		updataFootViewHeight = updataFootView.getMeasuredHeight();
		updataFootView.setPadding(0, -updataFootViewHeight, 0, 0);
		addFooterView(updataFootView);
	}

	/**
	 * 初始化刷新头的布局
	 */
	private void initHead() {
		updataHeadView = (LinearLayout) View.inflate(getContext(),
				R.layout.updata_head_layout, null);

		headprogress = (ProgressBar) updataHeadView
				.findViewById(R.id.pb_newsupdata_headprogress);
		headArrow = (ImageView) updataHeadView
				.findViewById(R.id.iv_newsupdata_arrow);
		headDesc = (TextView) updataHeadView
				.findViewById(R.id.tv_newsupdata_headdesc);
		headTime = (TextView) updataHeadView
				.findViewById(R.id.tv_newsupdata_headtime);

		updataHeadView.measure(0, 0);
		updataHeadViewHeight = updataHeadView.getMeasuredHeight();

		updataHeadView.setPadding(0, -updataHeadViewHeight, 0, 0);
		addHeaderView(updataHeadView);

	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initAnimation();
		initEvent();
	}

	// 加载轮播图的界面
	public void addLunboView(View lunbo) {
		addHeaderView(lunbo);
		this.mLunbo = lunbo;
	}

	public RefreshListView(Context context) {
		this(context, null);
	}
}
