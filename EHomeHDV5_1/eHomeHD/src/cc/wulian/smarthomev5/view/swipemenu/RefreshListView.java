package cc.wulian.smarthomev5.view.swipemenu;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.view.DropDownListView;

/**
 * Created by mabo on 2017/1/6 0006.
 */

public class RefreshListView extends SwipeMenuListView implements AbsListView.OnScrollListener {

    private final static int RELEASE_To_REFRESH = 0;
    private final static int PULL_To_REFRESH = 1;
    private final static int REFRESHING = 2;
    private final static int DONE = 3;

    // 实际的padding的距离与界面上偏移距离的比例
    private final static int RATIO = 5;
    private LayoutInflater inflater;
    private LinearLayout headView;
    private TextView tipsTextview;
    private boolean isFirstMove = true;
    private boolean isRecored;
    private int headContentHeight;
    private int startY;
    private int state;
    private DropDownListView.OnRefreshListener refreshListener;
    private long delay = 2000;
    private boolean isRefreshable;
    public RefreshListView(Context context) {
        super(context);
    }
    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflater = LayoutInflater.from(getContext());
        headView = (LinearLayout) inflater.inflate(R.layout.drop_down_listview_head, null);
        tipsTextview = (TextView) headView.findViewById(R.id.config_listview_head_text_tv);
        measureView(headView);
        headContentHeight = headView.getMeasuredHeight();
        headView.invalidate();
        this.addHeaderView(headView);
        setOnScrollListener(this);
        this.state = DONE;
        isRefreshable = false;
        changeHeaderViewByState(this.state);
    }

    public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if(firstVisibleItem == 0){
            isRecored = true;
        }else{
            isRecored = false;
        }
    }

    public void onScrollStateChanged(AbsListView arg0, int arg1) {
    }

    public boolean onTouchEvent(MotionEvent event) {

        if (isRefreshable && isRecored) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    isFirstMove = true;
                    if (state != REFRESHING) {
                        if(state == DONE || state == PULL_To_REFRESH){
                            state = DONE;
                            changeHeaderViewByState(DONE);
                        }else if(state == RELEASE_To_REFRESH){
                            state = REFRESHING;
                            changeHeaderViewByState(REFRESHING);
                            onRefresh();
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();
                    if(isFirstMove){
                        startY = tempY;
                        isFirstMove = false;
                    }
                    if (state != REFRESHING) {
                        if(state == DONE){
                            if ((tempY - startY ) > headContentHeight/3) {
                                this.state = PULL_To_REFRESH;
                                changeHeaderViewByState(this.state);
                                return true;
                            }
                        }
                        // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
                        // 可以松手去刷新了

                        // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                        else if (state == PULL_To_REFRESH) {
                            headView.setPadding(0, ((tempY - startY)- headContentHeight)/RATIO, 0, 0);
                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
                            if ((tempY - startY) >= headContentHeight) {
                                state = RELEASE_To_REFRESH;
                                changeHeaderViewByState(this.state);
                            }
                            // 上推到顶了
                            else if ((tempY - startY) <= 0) {
                                state = DONE;
                                changeHeaderViewByState(this.state);
                            }
                            return true;
                        }
                        else if (state == RELEASE_To_REFRESH) {
                            // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                            headView.setPadding(0, ((tempY - startY)-headContentHeight)/RATIO, 0, 0);
                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if ((tempY - startY)< headContentHeight
                                    && (tempY  - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState(this.state);
                            }
                            // 一下子推到顶了
                            else if ((tempY - startY ) <= 0) {
                                state = DONE;
                                changeHeaderViewByState(this.state);
                            }
                            return true;
                        }
                    }
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState(int state) {
        switch (state) {
            case PULL_To_REFRESH:
                // 是由RELEASE_To_REFRESH状态转变来的
                headView.setVisibility(View.VISIBLE);
                tipsTextview.setText(R.string.device_config_pull_down_refresh);
                break;
            case RELEASE_To_REFRESH:
                headView.setVisibility(View.VISIBLE);
                tipsTextview.setText(R.string.device_config_release_down_refresh);
                break;

            case REFRESHING:
                headView.setVisibility(View.VISIBLE);
                headView.setPadding(0, 0, 0, 0);
                tipsTextview.setText(R.string.device_config_pull_down_refresh_prompt);
                break;
            case DONE:
                headView.setVisibility(View.GONE);
                headView.setPadding(0, -1 * headContentHeight, 0, 0);
                tipsTextview.setText(R.string.device_config_pull_down_refresh);
                this.setSelection(0);
                break;
        }
    }

    public void setOnRefreshListener(DropDownListView.OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        isRefreshable = true;
    }

    public interface OnRefreshListener {
        public void onRefresh();
    }


    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    state = DONE;
                    isRecored = false;
                    changeHeaderViewByState(state);
                }
            },delay);
        }

    }

    // 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }


}
