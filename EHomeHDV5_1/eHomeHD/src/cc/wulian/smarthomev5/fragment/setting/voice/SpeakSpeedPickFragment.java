package cc.wulian.smarthomev5.fragment.setting.voice;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.TTSManager;

public class SpeakSpeedPickFragment extends WulianFragment {
	private SpeakSpeedAdapter mAdapter;
	private ListView mListViewShowAudios;
	private Preference preference = Preference.getPreferences();
	private TTSManager ttsManager = TTSManager.getInstance();
	private static String  BASENAME;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		BASENAME = getActivity().getIntent().getStringExtra("BASENAME");
		resources = mApplication.getResources();
		mAdapter = new SpeakSpeedAdapter(mActivity);
		mAdapter.setCurrentSpeed(preference.getInt(BASENAME + "_" + IPreferenceKey.P_KEY_VOICE_SPEED, 5));
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		initBar();
		return inflater.inflate(R.layout.setting_pick_bell_ring, null);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar()
				.setTitle(
						mApplication.getResources()
								.getString(
										R.string.set_sound_notification_bell_prompt_choose_titel));
		getSupportActionBar()
				.setIconText(
						mApplication.getResources()
								.getString(
										R.string.cancel));
		getSupportActionBar()
				.setRightIconText(
						mApplication.getResources()
								.getString(
										R.string.set_sound_notification_bell_prompt_choose_complete));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						int currentSpeed = mAdapter.getCurrentSpeed();
						preference.putInt(BASENAME + "_" + IPreferenceKey.P_KEY_VOICE_SPEED, currentSpeed);
						mActivity.finish();
					}
				});
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mListViewShowAudios = (ListView) view
				.findViewById(R.id.setting_bell_reminder_lv);
		mListViewShowAudios.setAdapter(mAdapter);
	}
	public class SpeakSpeedAdapter extends WLBaseAdapter<Integer>{

		private int currentSpeed = 0;
		public SpeakSpeedAdapter(Context context) {
			super(context, new ArrayList<Integer>());
			for(int i= 1; i <= 9 ;i++){
				this.getData().add(i);
			}
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.setting_select_bell_reminder, null);
		}

		public void setCurrentSpeed(int currentSpeed) {
			this.currentSpeed = currentSpeed;
		}

		public int getCurrentSpeed() {
			return currentSpeed;
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				final Integer item) {
			TextView ringNameTextView = (TextView)view.findViewById(R.id.setting_select_image_tv);
			final String speedStr  = mContext.getString(R.string.set_sound_notification_speed)+item;
			ringNameTextView.setText(speedStr);
			ImageView ringSelectedImageView = (ImageView)view.findViewById(R.id.setting_select_image_btn);
			if(this.currentSpeed == item.intValue()){
				ringSelectedImageView.setVisibility(View.VISIBLE);
			}else{
				ringSelectedImageView.setVisibility(View.INVISIBLE);
			}
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					currentSpeed = item;
					notifyDataSetChanged();
					TaskExecutor.getInstance().execute(new Runnable() {
						
						@Override
						public void run() {
							ttsManager.speak(item,speedStr);
						}
					});
				}
			});
		}
		
	}

}
