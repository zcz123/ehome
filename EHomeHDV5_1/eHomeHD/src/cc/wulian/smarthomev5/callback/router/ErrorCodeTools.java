package cc.wulian.smarthomev5.callback.router;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodeTools {
	private Map<Integer, String> errorCodeMaps = new HashMap<Integer, String>();
	private static ErrorCodeTools instance = null;

	private ErrorCodeTools() {
		initErrorCode();
	}

	public static ErrorCodeTools getInatance() {
		if (instance == null) {
			instance = new ErrorCodeTools();
		}
		return instance;
	}

	public Map<Integer, String> getErrorCodeMaps() {
		return errorCodeMaps;
	}

	private void initErrorCode() {
		// 对非法操作返回信息的提醒,但此未做对应处理和提醒
		errorCodeMaps.put(1000, "MAC地址不合法");
		errorCodeMaps.put(1001, "密码不能为空");
		errorCodeMaps.put(1002, "原密码不正确");
		errorCodeMaps.put(1003, "密码长度需要在 5-64 位之间");
		errorCodeMaps.put(1004, "IP 地址格式不正确");

	}

}
