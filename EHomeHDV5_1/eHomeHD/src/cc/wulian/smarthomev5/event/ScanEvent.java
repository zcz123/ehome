package cc.wulian.smarthomev5.event;


public class ScanEvent{

	public static final int CODE_REQUEST_SCAN = 0;
	public static final int CODE_RESULT_OK = 1;
	public static final int CODE_RESULT_CANCLE = 2;

	private int code;
	private String result;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public ScanEvent(int code) {
		super();
		this.code = code;
	}

	public ScanEvent(int code, String result) {
		super();
		this.code = code;
		this.result = result;
	}

	@Override
	public String toString() {
		return "ScanEvent [code=" + code + ", result=" + result + "]";
	}
}
