package cc.wulian.smarthomev5.service.html5plus.plugins;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.smarthomev5.R;

public class CustomKeyPopupWindow extends PopupWindow {

	private View rootView;
	private TextView tvRename, tvBinding, tvCanel;
	private JSONArray array;
	private JSONArray data;
	private String callbackId;

	public CustomKeyPopupWindow(final H5PlusWebView pWebview, String result) {
		super(pWebview.getContext());
		rootView = View.inflate(pWebview.getContext(), R.layout.pop_custom_key, null);
		this.setContentView(rootView);

		tvRename = (TextView) rootView.findViewById(R.id.tv_rename);
		tvBinding = (TextView) rootView.findViewById(R.id.tv_binding);
		tvCanel = (TextView) rootView.findViewById(R.id.tv_cancel_custom);

		try {
			array = new JSONArray(result);
			callbackId = array.getString(0);
			data = array.getJSONArray(1);
			tvRename.setText(data.get(0).toString());
			tvBinding.setText(data.get(1).toString());
			tvCanel.setText(data.get(2).toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		tvRename.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
				JsUtil.getInstance().execCallback(pWebview, callbackId, 0 + "", JsUtil.OK, false);
			}
		});
		tvBinding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
				JsUtil.getInstance().execCallback(pWebview, callbackId, 1 + "", JsUtil.OK, false);
			}
		});
		tvCanel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
				JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.ERROR, false);
			}
		});

		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.popwin_anim_style);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);

		rootView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int heightTop = v.findViewById(R.id.lin_selct_above).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < heightTop) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

}
