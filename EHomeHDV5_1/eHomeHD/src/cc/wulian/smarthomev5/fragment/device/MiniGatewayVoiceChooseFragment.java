package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MiniGatewayVoiceChooseActivity;
import cc.wulian.smarthomev5.adapter.MiniGatewayVoiceChooseAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class MiniGatewayVoiceChooseFragment extends WulianFragment {

	private ListView voiceChooseListView;
	private MiniGatewayVoiceChooseAdapter voiceChooseAdapter;
	private List<String> voiceTypeList;
	private String deviceEp;
	private String deviceEpType;
	private String voiceSize;
	private String voiceMode;
	private String gwID;
	private String devID;
	private TextView textView;
	public String choose_voice_name;
	
	private static final String DATA_CTRL_MINI_VOICE = "3";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(
				R.layout.device_mini_gateway_voice_choose_content, container,
				false);
		return v;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		initBar();
		initDate();
		deviceEp = getActivity().getIntent().getStringExtra(
				MiniGatewayVoiceChooseActivity.DEVICE_EP);
		deviceEpType = getActivity().getIntent().getStringExtra(
				MiniGatewayVoiceChooseActivity.DEVICE_EPTYPE);
		voiceSize = getActivity().getIntent().getStringExtra(
				MiniGatewayVoiceChooseActivity.VOICE_SIZE);
		gwID = getActivity().getIntent().getStringExtra(
				MiniGatewayVoiceChooseActivity.GWID);
		devID = getActivity().getIntent().getStringExtra(
				MiniGatewayVoiceChooseActivity.DVID);
		voiceChooseAdapter = new MiniGatewayVoiceChooseAdapter(mActivity);
		voiceChooseAdapter.swapData(voiceTypeList);
		voiceChooseListView = (ListView) mActivity
				.findViewById(R.id.voice_choose_lv);
		voiceChooseListView.setAdapter(voiceChooseAdapter);
		voiceChooseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				initDate();
				voiceTypeList.set(arg2, "1");
				textView = (TextView) arg1.findViewById(R.id.voice_choose_tv);
				choose_voice_name = textView.getText().toString();
				//选择声音名称存入本地
				Preference.getPreferences().saveVoiceChooseName(choose_voice_name);
				voiceChooseAdapter.swapData(voiceTypeList);
				int position = arg2 + 1;
//				//选择声音位置存入本地
				Preference.getPreferences().saveVoiceChooseNum(position);
				if (position < 10) {
					voiceMode = "0" + position;
				} else {
					voiceMode = position+"";
				}

				MiniGatewayVoiceChooseActivity.intent.putExtra("arg", choose_voice_name);
				getActivity().setResult(211, MiniGatewayVoiceChooseActivity.intent);
				NetSDK.sendControlDevMsg(gwID, devID, deviceEp, deviceEpType,
						DATA_CTRL_MINI_VOICE + voiceMode + voiceSize);
			}
		});
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(false);
//		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIcon(R.drawable.icon_back);
//		getSupportActionBar().setRightIconText(mApplication.getResources().getString(R.string.set_save));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.miniGW_Device_Sound_Tracks));
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {
					@Override
					public void onClick(View v) {
						MiniGatewayVoiceChooseActivity.intent.putExtra("arg", choose_voice_name);
						getActivity().setResult(211, MiniGatewayVoiceChooseActivity.intent); 						
						getActivity().finish();
					}
				});
//		getSupportActionBar().setRightMenuClickListener(
//				new OnRightMenuClickListener() {
//					@Override
//					public void onClick(View v) {
//						MiniGatewayVoiceChooseActivity.intent.putExtra("arg", choose_voice_name);
//						getActivity().setResult(211, MiniGatewayVoiceChooseActivity.intent);
//						NetSDK.sendControlDevMsg(gwID, devID, deviceEp, deviceEpType,
//								DATA_CTRL_MINI_VOICE + voiceMode + voiceSize);
//						getActivity().finish();
//					}
//				});
		
	}

	private void initDate() {
		if (voiceTypeList == null) {
			voiceTypeList = new ArrayList<String>();
		}
		voiceTypeList.clear();
		for (int i = 0; i < 11; i++) {
			voiceTypeList.add("0");
		}
	}
}
