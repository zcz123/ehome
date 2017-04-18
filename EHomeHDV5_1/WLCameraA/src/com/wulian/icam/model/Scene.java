package com.wulian.icam.model;

import java.util.ArrayList;
import java.util.List;

public class Scene {
	public interface OnSelectionLisenter {
		public boolean onSeleted(int idx, String tag, String status);
	}

	public interface OnResultLisenter {
		public void onResultChanged(boolean success);
	}

	public interface OnDataChangedLisenter {
		public void OnDateChanged(List<SData> sDataList);
	}

	public static class SData {
		public String title;
		public int iconNO;
		public String tag;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String status;

		public SData(String title, int iconNO, String tag, String status) {
			this.title = title;
			this.iconNO = iconNO;
			this.tag = tag;
			this.status = status;
		}
	}

	private static Scene mInstance;
	private OnSelectionLisenter mSelectionListener;
	private OnResultLisenter mOnResultLisenter;
	private OnDataChangedLisenter mOnDateChangedLisenter;
	private List<SData> mDataList;
	private int mSelectedIdx;
	private boolean isInPlayVideoUI;//视频界面是否打开

	public static Scene getInstance() {
		if (mInstance == null) {
			mInstance = new Scene();
		}
		return mInstance;
	}

	private Scene() {
	}

	public void setDataList(List<SData> data) {
		if(mDataList == null){
			mDataList = new ArrayList<Scene.SData>();
		}
		if(mDataList.size()>0){
			mDataList.clear();
		}
		mDataList.addAll(data);
	}

	public List<SData> getDataList() {
		return mDataList;
	}

	public void setOnSelectionLisenter(OnSelectionLisenter listener) {
		mSelectionListener = listener;
	}

	public OnSelectionLisenter getOnSelectionLisenter() {
		return mSelectionListener;
	}

	public void setOnResultLisenter(OnResultLisenter listener) {
		mOnResultLisenter = listener;
	}

	public OnResultLisenter getOnResultLisenter() {
		return mOnResultLisenter;
	}

	public void setOnDataResultLisenter(OnDataChangedLisenter listener) {
		mOnDateChangedLisenter = listener;
	}

	public OnDataChangedLisenter getOnDateChangedLisenter() {
		return mOnDateChangedLisenter;
	}

	public int getSelectdIdx() {
		return mSelectedIdx;
	}

	public void setSelectdIdx(int idx) {
		this.mSelectedIdx = idx;
	}

	public void setResult(boolean success) {
		if (mOnResultLisenter != null)
			mOnResultLisenter.onResultChanged(success);
	}

	/**
	 * @MethodName: setDataChanged
	 * @Function: 用于v5端scene数据改变之后调用该方法
	 * @date: 2015年11月11日
	 * @param data
	 *            数据源
	 */
	public void setDataChanged(List<SData> data) {
		if ( null != mOnDateChangedLisenter) {
			mOnDateChangedLisenter.OnDateChanged(data);
		}
		setDataList(data);
	}

	public boolean isInPlayVideoUI() {
		return isInPlayVideoUI;
	}

	public void setInPlayVideoUI(boolean isInPlayVideoUI) {
		this.isInPlayVideoUI = isInPlayVideoUI;
	}
}
