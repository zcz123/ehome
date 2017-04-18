package cc.wulian.smarthomev5.fragment.setting.minigateway;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.MiniGatewayTimePeroidAdapter;
import cc.wulian.smarthomev5.event.CommondDeviceConfigurationEvent;

import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class MiniSetTimePeroidFragment extends WulianFragment {

	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();

	private String MINI_GATEWAY_TIME_PEROID_KEY = "mini_gateway_time_peroid_key";

	@ViewInject(R.id.mini_gateway_time_peroid_lv_first)
	private ListView firstListView;
	@ViewInject(R.id.mini_gateway_time_peroid_lv_second)
	private ListView secondListView;
	private List<String> firstDataList;
	private List<String> secondDataList;
	private MiniGatewayTimePeroidAdapter firstAdapter;
	private MiniGatewayTimePeroidAdapter secondAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initBar();
		View rootView = inflater
				.inflate(R.layout.time_peroid, container, false);
		ViewUtils.inject(this, rootView);
		return rootView;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initData();
		firstAdapter = new MiniGatewayTimePeroidAdapter(mActivity);
		firstAdapter.adapterType = "first";
		firstListView.setAdapter(firstAdapter);
		secondAdapter = new MiniGatewayTimePeroidAdapter(mActivity);
		secondListView.setAdapter(secondAdapter);
		firstAdapter.swapData(firstDataList);
		secondAdapter.swapData(secondDataList);
		getServerDate();
		listener();

	}

	private void getServerDate() {
		mDialogManager.showDialog(MINI_GATEWAY_TIME_PEROID_KEY, mActivity,
				null, null);
		NetSDK.sendCommonDeviceConfigMsg(info.getGwID(), "self", "3", null,
				"clock_hours", null);
	}

	private void listener() {
		firstListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String stringType = firstDataList.get(arg2);
				if (stringType.equals("0")) {
					firstDataList.set(arg2, "1");
				} else {
					firstDataList.set(arg2, "0");
				}
				firstAdapter.swapData(firstDataList);
			}
		});
		secondListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String stringType = secondDataList.get(arg2);
				if (stringType.equals("0")) {
					secondDataList.set(arg2, "1");
				} else {
					secondDataList.set(arg2, "0");
				}
				secondAdapter.swapData(secondDataList);
			}
		});
	}

	private void initData() {
		// TODO Auto-generated method stub
		if (firstDataList == null) {
			firstDataList = new ArrayList<String>();
			secondDataList = new ArrayList<String>();
		}
		firstDataList.clear();
		secondDataList.clear();
		for (int i = 0; i < 12; i++) {
			firstDataList.add("0");
		}
		for (int i = 12; i < 24; i++) {
			secondDataList.add("0");
		}
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(R.string.set_save));
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.about_back));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.miniGW_Timekeeping_period));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {
						String data = getDate(firstDataList)
								+ getDate(secondDataList);
						String reversalString = reversalString(data);

						int AlgorismDate = binaryToAlgorism(reversalString);
						String HEXStringDate = algorismToHEXString(AlgorismDate);
						String jsonDate = "{\"h\":" + "\"" + HEXStringDate
								+ "\"" + "}";
						// String jsonStringDate = CompressUtil
						// .compressString(jsonDate);
						mDialogManager.showDialog(MINI_GATEWAY_TIME_PEROID_KEY,
								mActivity, null, null);
						NetSDK.sendCommonDeviceConfigMsg(info.getGwID(),
								"self", "2", null, "clock_hours", jsonDate);
						mActivity.finish();

					}
				});
	}

	// 获取数据
	protected String getDate(List<String> DataList) {
		String data = "";
		for (int i = 0; i < DataList.size(); i++) {
			data += DataList.get(i);
		}
		return data;
	}

	public void onEventMainThread(CommondDeviceConfigurationEvent event) {
		mDialogManager.dimissDialog(MINI_GATEWAY_TIME_PEROID_KEY, 0);
		JSONObject object = JSON.parseObject(event.data);
		String convension_str2 = object.getString("h");
		String convension_str1 = hexStringToBinary(convension_str2);

		// 字符串的逆序
		String Stringreversal = reversalString(convension_str1);
		int size = Stringreversal.length();
		if (Stringreversal.length() <= 24) {
			for (int i = 0; i < 24 - size; i++) {
				Stringreversal += "0";

			}
		}
		String firstString = Stringreversal.substring(0,
				Stringreversal.length() / 2);
		String secondString = Stringreversal
				.substring(Stringreversal.length() / 2);

		for (int i = 0; i < firstString.length(); i++) {
			firstDataList.set(i, firstString.charAt(i) + "");

		}
		for (int i = 0; i < secondString.length(); i++) {
			secondDataList.set(i, secondString.charAt(i) + "");
		}

		firstAdapter.swapData(firstDataList);
		secondAdapter.swapData(secondDataList);
	}

	private String reversalString(String convension_str1) {
		String reversalString = "";
		for (int i = convension_str1.length() - 1; i >= 0; i--) {
			reversalString += convension_str1.charAt(i) + "";
		}
		return reversalString;
	}

	public static String hexStringToBinary(String hex) {
		hex = hex.toUpperCase();
		String result = "";
		int max = hex.length();
		for (int i = 0; i < max; i++) {
			char c = hex.charAt(i);
			switch (c) {
			case '0':
				result += "0000";
				break;
			case '1':
				result += "0001";
				break;
			case '2':
				result += "0010";
				break;
			case '3':
				result += "0011";
				break;
			case '4':
				result += "0100";
				break;
			case '5':
				result += "0101";
				break;
			case '6':
				result += "0110";
				break;
			case '7':
				result += "0111";
				break;
			case '8':
				result += "1000";
				break;
			case '9':
				result += "1001";
				break;
			case 'A':
				result += "1010";
				break;
			case 'B':
				result += "1011";
				break;
			case 'C':
				result += "1100";
				break;
			case 'D':
				result += "1101";
				break;
			case 'E':
				result += "1110";
				break;
			case 'F':
				result += "1111";
				break;
			}
		}
		return result;
	}

	/**
	 * 二进制字符串转十进制
	 * 
	 * @param binary
	 *            二进制字符串
	 * @return 十进制数值
	 */
	public static int binaryToAlgorism(String binary) {
		int max = binary.length();
		int result = 0;
		for (int i = max; i > 0; i--) {
			char c = binary.charAt(i - 1);
			int algorism = c - '0';
			result += Math.pow(2, max - i) * algorism;
		}
		return result;
	}

	/**
	 * 十进制转换为十六进制字符串
	 * 
	 * @param algorism
	 *            int 十进制的数字
	 * @return String 对应的十六进制字符串
	 */
	public static String algorismToHEXString(int algorism) {
		String result = "";
		result = Integer.toHexString(algorism);

		if (result.length() % 2 == 1) {
			result = "0" + result;
		}
		result = result.toUpperCase();
		return result;
	}
}
