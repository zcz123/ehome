package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.adapter.SearchDeviceListAdapter;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.InputMethodUtils;
import cc.wulian.smarthomev5.utils.Trans2PinYin;
import cc.wulian.smarthomev5.view.WLEditText;
import cc.wulian.smarthomev5.view.WLEditText.WLInputTextWatcher;

import com.yuantuo.customview.ui.ScreenSize;

public class SearchPopuWindow {

	private ListView searchListview;
	private SearchDeviceListAdapter searchAdapter;
	private BaseActivity activity;
	private PopupWindow popupWindow;
	private List<WulianDevice> deviceListData;
	private int statusBarHeight;
	public List<WulianDevice> getDeviceListData() {
		return deviceListData;
	}
	public void setDeviceListData(List<WulianDevice> deviceListData) {
		this.deviceListData = deviceListData;
	}
	public SearchPopuWindow(BaseActivity mActivity) {
		this.activity = mActivity;
		View contentView = View.inflate(mActivity, R.layout.device_list_search_edittext, null);
		final WLEditText editTextView = (WLEditText) contentView.findViewById(R.id.device_list_search_pop_edit);
		searchListview = (ListView) contentView.findViewById(R.id.device_list_listview);
		searchAdapter = new SearchDeviceListAdapter(mActivity);
		searchListview.setAdapter(searchAdapter);
		editTextView.registWLIputTextWatcher(new EditTextWatcher());
		
		popupWindow = new PopupWindow();
		
		//获取状态栏高度，适配显示置顶所有手机
		Rect rect= new Rect();  
		mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
		statusBarHeight = rect.top;
		popupWindow.setWidth(ScreenSize.screenWidth);
		popupWindow.setHeight(ScreenSize.screenHeight - statusBarHeight);
		popupWindow.setContentView(contentView);
		
		popupWindow.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setOutsideTouchable(true);	
		popupWindow.setFocusable(true);
		contentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodUtils.hide(activity,editTextView);
				dismiss();
			}
		});
	}
	
	public void setOnDismissListener(OnDismissListener listener){
		popupWindow.setOnDismissListener(listener);
	}
	public void show(View view){
		popupWindow.showAtLocation(view, Gravity.TOP, 0, statusBarHeight);
//		popupWindow.showAsDropDown(view);
//		popupWindow.showAsDropDown(view, 0, 0);
	}
	public void dismiss(){
		popupWindow.dismiss();
	}
	public boolean isShown(){
		return popupWindow.isShowing();
	}
	private void getSearchDevice(final String searchKey,final int pageSize){
		final List<WulianDevice> result = new ArrayList<WulianDevice>();
		if (StringUtil.isNullOrEmpty(searchKey) || deviceListData == null) {
			searchListview.setVisibility(View.GONE);
		} else {
			TaskExecutor.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					Set<WulianDevice> allSet = new LinkedHashSet<WulianDevice>();
					String key = searchKey.toLowerCase().trim();
					boolean isOver = false;
					for (int i = 0; i < deviceListData.size(); i++) {
						WulianDevice device = deviceListData.get(i);
						String deviceName = DeviceTool.getDeviceShowName(device).toLowerCase().trim();
						if (StringUtil.isNullOrEmpty(deviceName))
							continue;
						if(Trans2PinYin.isFirstCharacter(key, deviceName)){
							allSet.add(device);
						}
						if(allSet.size() >= pageSize){
							isOver = true;
							break;
						}

					}
					if(!isOver){
						for (int i = 0; i < deviceListData.size(); i++) {
							WulianDevice device = deviceListData.get(i);
							String deviceName = DeviceTool.getDeviceShowName(device).toLowerCase().trim();
							if (StringUtil.isNullOrEmpty(deviceName))
								continue;
							if(Trans2PinYin.isStartPinYin(key, deviceName)){
								allSet.add(device);
							}
							if(allSet.size() >= pageSize){
								isOver = true;
								break;
							}
						}
					}
					if(!isOver){
						for (int i = 0; i < deviceListData.size(); i++) {
							WulianDevice device = deviceListData.get(i);
							String deviceName = DeviceTool.getDeviceShowName(device).toLowerCase().trim();
							if (StringUtil.isNullOrEmpty(deviceName))
								continue;
							if(Trans2PinYin.isContainsPinYin(key, deviceName)){
								allSet.add(device);
							}
							if(allSet.size() >= pageSize){
								isOver = true;
								break;
							}

						}
					}
					result.addAll(allSet);
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							searchAdapter.swapData(result);
							if(result != null && result.size() != 0){
								searchListview.setVisibility(View.VISIBLE);
							}
						}
					});
				}
			});

		}
	}
	private class EditTextWatcher implements WLInputTextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			getSearchDevice(s.toString(),10);
		}
	}
}
