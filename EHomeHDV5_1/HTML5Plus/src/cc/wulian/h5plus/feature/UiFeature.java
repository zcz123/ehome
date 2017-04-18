package cc.wulian.h5plus.feature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Gravity;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.LinearLayout;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;

public class UiFeature {
	

	@JavascriptInterface
	public void prompt(final H5PlusWebView webView, final String data) {

		webView.post(new Runnable() {

			@Override
			public void run() {
				try {
					JSONArray array = new JSONArray(data);
					final String callBackId = array.getString(0);
					final JSONObject obj = array.getJSONObject(1);
					LinearLayout layout = new LinearLayout(webView.getContext());
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT, 400);
					layoutParams.gravity = Gravity.CENTER;
					layout.setLayoutParams(layoutParams);
					final EditText edit = new EditText(webView.getContext());
					edit.setHint(obj.getString("hint"));
					if(!obj.getString("name").equals("")){
						edit.setText(obj.getString("name"));
					}
					LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT, 100);
					editParams.gravity = Gravity.CENTER;
					editParams.topMargin = 50;
					editParams.bottomMargin = 50;
					editParams.leftMargin = 20;
					editParams.rightMargin = 20;
					layout.addView(edit, editParams);
					//
					new AlertDialog.Builder(webView.getContext())
							.setView(layout)
							.setTitle(obj.getString("title"))
							.setPositiveButton(obj.getString("ok"),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											
											if(edit.getText().toString().length()!=0){
												JsUtil.getInstance().execCallback(
														webView,
														callBackId,
														edit.getText().toString()
																.trim(), JsUtil.OK,
														true);
											}else{
												try {
													JsUtil.getInstance().execCallback(
															webView,
															callBackId,obj.getString("hint"), JsUtil.OK,
															true);
												} catch (JSONException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												
											}
											
										}
									})
							.setNegativeButton(obj.getString("cancel"),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											JsUtil.getInstance().execCallback(
													webView, callBackId, "",
													JsUtil.ERROR, true);
										}
									}).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@JavascriptInterface
	public void confirm(final H5PlusWebView webView, final String data) {
		webView.post(new Runnable() {

			@Override
			public void run() {
				try {
					JSONArray array = new JSONArray(data);
					final String callBackId = array.getString(0);
					JSONObject obj = array.getJSONObject(1);
					new AlertDialog.Builder(webView.getContext())
							.setMessage(obj.getString("message"))
							.setTitle(obj.getString("title"))
							.setPositiveButton(obj.getString("ok"),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											JsUtil.getInstance().execCallback(
													webView, callBackId, "",
													JsUtil.OK, true);
										}
									})
							.setNegativeButton(obj.getString("cancel"),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											JsUtil.getInstance().execCallback(
													webView, callBackId, "",
													JsUtil.ERROR, true);
										}
									}).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@JavascriptInterface
	public void alert(final H5PlusWebView webView, final String data) {
		webView.post(new Runnable() {

			@Override
			public void run() {
				try {
					JSONArray array = new JSONArray(data);
					final String callBackId = array.getString(0);
					JSONObject obj = array.getJSONObject(1);
					new AlertDialog.Builder(webView.getContext())
							.setMessage(obj.getString("message"))
							.setTitle(obj.getString("title"))
							.setPositiveButton(obj.getString("ok"),
									new OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											JsUtil.getInstance().execCallback(
													webView, callBackId, "",
													JsUtil.OK, true);
										}
									})
							.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


}
