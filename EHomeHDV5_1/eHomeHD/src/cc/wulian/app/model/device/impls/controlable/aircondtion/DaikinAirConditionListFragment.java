package cc.wulian.app.model.device.impls.controlable.aircondtion;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.DaiKinAirConditionSetActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

import com.yuantuo.customview.ui.WLDialog;

public class DaikinAirConditionListFragment extends WulianFragment {

	public static final String GWID = "gwid";
	public static final String DEVICE_ID = "device_id";
	public static final String EPDATA = "epData";
	private String gwID;
	private String devID;
	private String epData;
	private ListView lvaircondition;
	private DaikinConditionItemAdapter adapter;
	private AirConditionManager airConditionManager = AirConditionManager
			.getInstance();
	private static AirCondition mCondition;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		gwID = bundle.getString(GWID);
		devID = bundle.getString(DEVICE_ID);
		epData = bundle.getString(EPDATA);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_daikinac_list, container,
				false);
	}

	@Override
	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);

		initView(paramView);

	}

	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIconText(R.string.nav_device_title);
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.daikin_airconditioner));
	}

	private void initView(View paramView) {
		lvaircondition = (ListView) paramView.findViewById(R.id.lv_daikin_ac);
		adapter = new DaikinConditionItemAdapter(mActivity, null);
		lvaircondition.setAdapter(adapter);
		analysisEpDataToFindAddress(epData);
	}

	public void analysisEpDataToFindAddress(String data) {
		// 011900000104120000000000000000000000000000000000004735
		if (StringUtil.isNullOrEmpty(data) || data.length() < 54) {
			return;
		} else if (data.startsWith("01") && data.substring(4, 8).equals("0000")) {

			String back_30001 = data.substring(14, 18);
			if (!(DaikinChangeDataAndAddress.getAddressIndex(
					DaikinChangeDataAndAddress
							.hexString2binaryString(back_30001)).get(0) == 0)) {
				Toast.makeText(mActivity, "适配器未准备好!", Toast.LENGTH_SHORT)
						.show();

			} else {

			}
			int index = 18;
			int groupIndex = 0;
			while (index < 34) {
				String str = data.substring(index, index + 4);
				initGroupDaiKins(
						DaikinChangeDataAndAddress.getGroupName(groupIndex),
						str);
				index += 4;
				groupIndex++;
			}
			adapter.swapData(airConditionManager.getAllAirConditions());
		}

	}

	public void initGroupDaiKins(String groupName, String groupData) {
		if (StringUtil.isNullOrEmpty(groupData)
				|| StringUtil.isNullOrEmpty(groupData)) {
			return;
		}
		List<Integer> existIndexs = DaikinChangeDataAndAddress
				.getAddressIndex(DaikinChangeDataAndAddress
						.hexString2binaryString(groupData));
		int groupIndex = DaikinChangeDataAndAddress.getGroupIndex(groupName);
		for (Integer index : existIndexs) {
			String str = groupIndex + "-"
					+ StringUtil.appendLeft(index + "", 2, '0');
			Daikin_AirCondition condition = new Daikin_AirCondition();
			condition.setCurID(str);
			condition.setCurDevName(str);
			airConditionManager.addAriCondition(condition);

			// 存入数据库
			condition.setGwID(gwID);
			condition.setDevID(devID);
			condition.setKeyID(str);
			condition.setKeyName(str);
//			MainApplication.getApplication().mDataBaseHelper
//					.insertDaikinAirConditionRecords(condition);
			// 从数据库取数据 如果该ID已存在则取出
//			List<AirCondition> existKeyID = getExistDaikinConditionInfo(str);
//			for (AirCondition conditions : existKeyID) {
//				condition.setCurDevName(conditions.getKeyName());
//			}

		}

	}

	private List<AirCondition> getExistDaikinConditionInfo(String onlineKeyID) {
//		List<AirCondition> list = Lists.newArrayList();
//		Cursor cursor = MainApplication.getApplication().mDataBaseHelper
//				.queryDaikinAirConditionRecords();
//		if (cursor != null) {
//			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
//					.moveToNext()) {
//				Daikin_AirCondition mCondition = new Daikin_AirCondition();
//				mCondition.setGwID(cursor
//						.getString(DaikinAirConditionRecord.POS_GW_ID));
//				mCondition.setDevID(cursor
//						.getString(DaikinAirConditionRecord.POS_DEV_ID));
//				mCondition.setKeyID(cursor
//						.getString(DaikinAirConditionRecord.POS_KEY_ID));
//				mCondition.setKeyName(cursor
//						.getString(DaikinAirConditionRecord.POS_KEY_NAME));
//
//				if (cursor.getString(DaikinAirConditionRecord.POS_KEY_ID)
//						.equals(onlineKeyID)) {
//					list.add(mCondition);
//				}
//			}
//			return list;
//		} else {
//			return null;
//		}
		return null;
	}

	public class DaikinConditionItemAdapter extends WLBaseAdapter<AirCondition> {

		public DaikinConditionItemAdapter(Context context,
				List<AirCondition> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.daikin_ac_list_item, null);
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				final AirCondition item) {
			mCondition = item;
			TextView listNameTextView = (TextView) view
					.findViewById(R.id.device_daikin_item_name);
			ImageView itemRename = (ImageView) view
					.findViewById(R.id.device_daikin_item_rename);
			listNameTextView.setText(item.getCurDevName());
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Bundle bundler = new Bundle();
					bundler.putString(DaiKinAirConditionSetFragment.DEVICE_ID,
							devID);
					bundler.putString(DaiKinAirConditionSetFragment.GWID, gwID);
					bundler.putString(
							DaiKinAirConditionSetFragment.CUR_AIR_CONDITION_ID,
							item.getCurID());
					Intent intent = new Intent(mActivity,
							DaiKinAirConditionSetActivity.class);
					intent.putExtras(bundler);
					mActivity.startActivity(intent);
				}
			});
			itemRename.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WLDialog.Builder builder = new WLDialog.Builder(mActivity);
					View rootView = View.inflate(mActivity,
							R.layout.setname_dialog, null);
					final EditText edit = (EditText) rootView
							.findViewById(R.id.et_newname);
					edit.setText(mCondition.getCurDevName());
					builder.setContentView(rootView)
							.setPositiveButton(R.string.common_ok)
							.setNegativeButton(R.string.cancel)
							.setTitle(R.string.daikin_airconditioner_modifyname)
							.setListener(new WLDialog.MessageListener() {
								@Override
								public void onClickPositive(
										View contentViewLayout) {
//									String str = edit.getText().toString();
//									if (!StringUtil.isNullOrEmpty(str)) {
//										mCondition.setCurDevName(str);
//										mCondition.setKeyName(str);
//										modifyName(mCondition);
//
//									} else {
//										WLToast.showToast(
//												mActivity,
//												mApplication
//														.getResources()
//														.getString(
//																R.string.device_key_name_not_null),
//												WLToast.TOAST_SHORT);
//									}
								}

								@Override
								public void onClickNegative(
										View contentViewLayout) {

								}
							});

					WLDialog mACRenameDialog = builder.create();
					mACRenameDialog.show();
				}
			});
		}

	}

	public void modifyName(AirCondition condition) {
		MainApplication app = MainApplication.getApplication();
		app.mDataBaseHelper.updataDaikinAirConditionRecords(condition);
		adapter.notifyDataSetChanged();
	}
}
