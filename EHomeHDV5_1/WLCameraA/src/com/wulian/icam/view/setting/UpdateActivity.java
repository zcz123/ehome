package com.wulian.icam.view.setting;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wulian.extraroutelibrary.controller.DocDownloadCallBackListener;
import com.wulian.extraroutelibrary.controller.RouteLibraryExtraController;
import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.routelibrary.common.ErrorCode;

public class UpdateActivity extends BaseFragmentActivity {
	private TextView update_tv;
	private ProgressBar mProgressBar;
	private TextView mUpdateTv;
	private Button mBtnCancle;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_progressbar);
		if (Build.VERSION.SDK_INT >= 11) {
			doOnTouch();
		} else {

		}
		initView();
		initData();
		setListener();
	};

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void doOnTouch() {
		this.setFinishOnTouchOutside(false);
	}

	private void initData() {
		String DocURL = getIntent().getStringExtra("url");
		String filePath = ICamGlobal.getInstance().getRootPath()
				+ "/ICam.apk";
		RouteLibraryExtraController.getInstance().doUpgrade(UpdateActivity.this,
				filePath, DocURL, mProgressBar, mUpdateTv,
				new DocDownloadCallBackListener() {
					@Override
					public void DocCallBack(int position, ProgressBar pb,
							int result, ErrorCode code) {
						// TODO Auto-generated method stub
						if (result == 1 && code == ErrorCode.SUCCESS) {
							setResult(RESULT_OK);
						} else {
							setResult(RESULT_CANCELED);
						}
						UpdateActivity.this.finish();
					}
				}, true);
	}

	private void initView() {
		update_tv = (TextView) findViewById(R.id.tv_title);
		mProgressBar = (ProgressBar) findViewById(R.id.update_progress);
		mUpdateTv = (TextView) findViewById(R.id.update_progress_text);
		mBtnCancle = (Button) findViewById(R.id.btn_cancle);
		mBtnCancle.setVisibility(View.GONE);
	}

	private void setListener() {

		findViewById(R.id.ll_custom_progressbar).setOnTouchListener(
				new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						return false;
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
