package cc.wulian.app.model.device.impls.controlable.toc;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.dao.TOCDao;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class DeviceTwoOutputFragment extends WulianFragment {

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";

	public static final String DEVICE_TWO_OUTPUT = "DEVICE_TWO_OUTPUT";
	private String gwID;
	private String deviceID;
	private static WulianDevice twoDevice;

	private static TwoOutputConverterAddAdapter mAdapter;
	private ImageView defaultSetBtn;
	private ListView mListView;
	private ImageView mButton;
	private static TOCDao tocDao = TOCDao.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		gwID = (String) bundle.getString(GWID);
		deviceID = (String) bundle.getString(DEVICEID);
		twoDevice = DeviceCache.getInstance(mActivity).getDeviceByID(mActivity,
				gwID, deviceID);
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_type_A2));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.device_two_output_converter_setting,
				null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		defaultSetBtn = (ImageView) view
				.findViewById(R.id.device_two_output_converter_default_setting);
		defaultSetBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = DeviceTwoOutputFragment.this.getActivity()
						.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				TwoOutputDefaultSetFragment.showDefaultSetFragment(fm, ft,
						DeviceTwoOutputFragment.this, twoDevice);
			}
		});

		mListView = (ListView) view
				.findViewById(R.id.device_two_output_converter_seeting_item);

		loaderTwoOutputListInfo();

		mButton = (ImageView) view
				.findViewById(R.id.device_two_output_converter_setting_add);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = DeviceTwoOutputFragment.this.getActivity()
						.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				TwoOutputCreateFragment.showDeviceDialog(fm, ft,
						DeviceTwoOutputFragment.this, twoDevice);
			}
		});

	}

	// 二路转换器中相关数据存储在本地数据库,主要对数据库一些操作
	public void loaderTwoOutputListInfo() {
		mAdapter = new TwoOutputConverterAddAdapter(getActivity(),
				tocDao.findTwoOutputGridInfo());
		mListView.setAdapter(mAdapter);
	}

	public static class TwoOutputConverterAddAdapter extends
			WLBaseAdapter<TwoOutputEntity> {

		private DeviceTwoOutputFragment mTwoOutputAddFragment = new DeviceTwoOutputFragment();
		private static TextView mButton;
		private ImageView modifyname;
		private ImageView edit;
		private ImageView delete;
		private int mCurrentPos = -1;

		public TwoOutputConverterAddAdapter(Context context,
				List<TwoOutputEntity> data) {
			super(context, data);
		}

		public int setSelectionPos(int pos) {
			this.mCurrentPos = pos;
			notifyDataSetChanged();
			return mCurrentPos;
		}

		public int setSelectionPosByKeyID(String KeyID) {
			final List<TwoOutputEntity> records = getData();
			int pos = -1;
			if (getCount() == 0)
				return setSelectionPos(-1);

			int size = records.size();
			for (int i = 0; i < size; i++) {
				TwoOutputEntity record = records.get(i);
				if (TextUtils.equals(record.keyID, KeyID)) {
					pos = i;
					break;
				}
			}
			return setSelectionPos(pos);

		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_two_output_converter_item,
					null);

		}

		@Override
		protected void bindView(Context context, View view, int pos,
				TwoOutputEntity item) {
			mButton = (TextView) view
					.findViewById(R.id.device_two_output_converter_btn);
			modifyname = (ImageView) view
					.findViewById(R.id.device_two_output_converter_modify_keyname);
			edit = (ImageView) view
					.findViewById(R.id.device_two_output_converter_edit);
			delete = (ImageView) view
					.findViewById(R.id.device_two_output_converter_delete);

			modifyname.setOnClickListener(new ModifyNameListener(item, pos));
			edit.setOnClickListener(new EditListener(item, pos));
			delete.setOnClickListener(new DeleteListener(item, pos));
			mButton.setText(item.getKeyName());

		}

		private class ModifyNameListener implements OnClickListener {
			final TwoOutputEntity records;
			final int pos;

			public ModifyNameListener(TwoOutputEntity records, int position) {
				this.records = records;
				pos = position;
			}

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = ((BaseActivity) mContext)
						.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				TwoOutputRenameFragment.showRenameDialog(fm, ft,
						mTwoOutputAddFragment, records, pos);
			}

		}

		// 根据listview对应的item跳转对应的fragment
		private class EditListener implements OnClickListener {

			final TwoOutputEntity records;
			final int pos;

			public EditListener(TwoOutputEntity records, int position) {
				this.records = records;
				pos = position;
			}

			@Override
			public void onClick(View arg0) {
				FragmentManager fm = ((BaseActivity) mContext)
						.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				TwoOutputEditFragment.showEditDialog(fm, ft,
						mTwoOutputAddFragment, records, pos);
			}

		}

		private class DeleteListener implements OnClickListener {
			final TwoOutputEntity records;
			final int pos;

			public DeleteListener(TwoOutputEntity records, int position) {
				this.records = records;
				pos = position;
			}

			@Override
			public void onClick(View arg0) {
				tocDao.delete(records);
				getData().remove(records);
				if (mCurrentPos == pos)
					mCurrentPos = -1;
				notifyDataSetChanged();
			}
		}

		public void addItemData(TwoOutputEntity entity) {
			tocDao.insert(entity);
			notifyDataSetChanged();
		}

		public void modifyItemName(TwoOutputEntity entity) {
			tocDao.update(entity);
			mAdapter.notifyDataSetChanged();
		}

		public void updateItemData(TwoOutputEntity entity) {
			tocDao.update(entity);
			notifyDataSetChanged();
		}

	}

}
