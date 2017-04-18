package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;

public class AutoRefreshListView extends ListView implements OnScrollListener {


	private LayoutInflater inflater;
	private View footer;

	private TextView noData;
	private TextView loadMoreData;
	private TextView loading;
	private ProgressBar progressBar;

	private int firstVisibleItem;
	private int scrollState;

	private boolean isLoading;// 判断是否正在加载
	private boolean loadEnable;// 开启或者关闭加载更多功能
	private boolean isLoadFull;
	private int pageSize = 10;

	private OnLoadListener onLoadListener;

	public AutoRefreshListView(Context context) {
		super(context);
		initView(context);
	}

	public AutoRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public AutoRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	// 加载更多监听
	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.loadEnable = true;
		this.onLoadListener = onLoadListener;
	}

	public boolean isLoadEnable() {
		return loadEnable;
	}

	// 这里的开启或者关闭加载更多，并不支持动态调整
	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
		this.removeFooterView(footer);
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// 初始化组件
	private void initView(Context context) {

		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.home_alarm_item_refresh_listview,
				null);
		loadMoreData = (TextView) footer.findViewById(R.id.home_item_refresh_loadmoredata);
		noData = (TextView) footer.findViewById(R.id.home_item_refresh_noData);
		loading = (TextView) footer.findViewById(R.id.home_item_refresh_loding);
		progressBar = (ProgressBar) footer.findViewById(R.id.progressBar1);

		loadMoreData.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		noData.setVisibility(View.GONE);
		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}

	public void onLoad() {
		if (onLoadListener != null) {
			onLoadListener.onLoad();
		}
	}

	// 用于加载更多结束后的回调
	public void onLoadComplete() {
		isLoading = false;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		ifNeedLoad(view, scrollState);
	}

	// 根据listview滑动的状态判断是否需要加载更多
	private void ifNeedLoad(AbsListView view, int scrollState) {
		try {
			showLoosenLoad();
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& !isLoading
					&& view.getLastVisiblePosition() == view
							.getPositionForView(footer) && !isLoadFull) {
				onLoad();
				if (!loadEnable) {
					showWhioutData();
				} else {
					showLoad();
				}
				isLoading = false;
			}
		} catch (Exception e) {
		}
	}

	public void setResultSize(int resultSize) {
		if (resultSize == 0) {
			isLoadFull = true;
			loadEnable = false;
			// showWhioutData();
		} else if (resultSize > 0 && resultSize < pageSize) {
			isLoadFull = true;
			loadEnable = false;
		} else if (resultSize == pageSize) {
			isLoadFull = false;
		}

	}

	public void showLoosenLoad() {
		isLoadFull = false;
		loadMoreData.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		noData.setVisibility(View.GONE);
	}

	public void showLoad() {
		isLoadFull = false;
		loadMoreData.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		loading.setVisibility(View.VISIBLE);
		noData.setVisibility(View.GONE);
	}

	public void showWhioutData() {
		loadMoreData.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		noData.setVisibility(View.VISIBLE);
	}

	public void closeAllLoad() {
		loadMoreData.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		loading.setVisibility(View.GONE);
		noData.setVisibility(View.GONE);
	}

	/*
	 * 定义加载更多接口
	 */
	public interface OnLoadListener {
		public void onLoad();
	}

}
