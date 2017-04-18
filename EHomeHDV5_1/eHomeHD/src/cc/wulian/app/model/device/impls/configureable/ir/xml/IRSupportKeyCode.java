package cc.wulian.app.model.device.impls.configureable.ir.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.KeyCodeParse;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.KeyCodeParse.KeyCode;

public final class IRSupportKeyCode {
	private static IRSupportKeyCode mInstance = null;

	public static IRSupportKeyCode getInstance(Context context) {
		if (mInstance == null) {
			synchronized (IRSupportKeyCode.class) {
				if (mInstance == null) {
					mInstance = new IRSupportKeyCode(context);
				}
			}
		}
		return mInstance;
	}

	private List<KeyCode> mKeyCodes;

	private IRSupportKeyCode(Context context) {
		InputStream in = null;
		try {
			KeyCodeParse mKeyCodeParse = new KeyCodeParse();
			in = context.getResources().openRawResource(R.raw.keycode);
			mKeyCodes = mKeyCodeParse.startParse(in);
		} catch (Exception e) {

		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * return all KeyCode, can not add,addAll,remove,clear,set
	 */
	public List<KeyCode> getSupportKeyCodeList() {
		if (mKeyCodes == null)
			mKeyCodes = new ArrayList<KeyCode>();
		// return Collections.unmodifiableList(mKeyCodes);
		return mKeyCodes;
	}

	/**
	 * return special code KeyCode
	 */
	public KeyCode getSupportKeyCodeByCode(String code) {
		List<KeyCode> keyCodes = getSupportKeyCodeList();
		KeyCode find = null;
		for (KeyCode keyCode : keyCodes) {
			if (TextUtils.equals(keyCode.getCode(), code)) {
				find = keyCode;
				break;
			}
		}
		return find;
	}
}