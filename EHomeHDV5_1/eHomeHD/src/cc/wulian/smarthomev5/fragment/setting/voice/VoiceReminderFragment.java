package cc.wulian.smarthomev5.fragment.setting.voice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BellAudioPickActivity;
import cc.wulian.smarthomev5.activity.ShakeActivity;
import cc.wulian.smarthomev5.activity.SpeakSpeedPickActivity;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class VoiceReminderFragment extends WulianFragment {

	@ViewInject(R.id.setting_voice_lv)
	private ListView voiceListView;
	private TextView titleHitText;
	private Preference preference = Preference.getPreferences();
	protected RingtoneManager ringtoneManager;
	protected String defaultName ;
	protected String defaultPath ;
	private SettingManagerAdapter settingManagerAdapter;
	private static String  BASENAME;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        BASENAME = getActivity().getIntent().getStringExtra("BASENAME");
		ringtoneManager = new RingtoneManager(mActivity);
		ringtoneManager.setType(RingtoneManager.TYPE_ALL);
		defaultName = mApplication.getResources().getString(
				R.string.set_sound_notification_default);
		defaultPath = ringtoneManager.getDefaultUri(
				RingtoneManager.TYPE_NOTIFICATION).toString();
		settingManagerAdapter = new SettingManagerAdapter(mActivity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = (LinearLayout) inflater.inflate(
				R.layout.setting_voice_reminder, container, false);
		ViewUtils.inject(this, contentView);
		initBar();
		return contentView;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIconText(
                mApplication.getResources().getString(
                        R.string.about_back));
        if ("A".equals(BASENAME))
            getSupportActionBar().setTitle(
                    mApplication.getResources().getString(
                            R.string.scene_alarm));
        else if ("P".equals(BASENAME))
		    getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.gateway_settings_alarm_push_hint));
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		voiceListView.setAdapter(settingManagerAdapter);
        titleHitText = (TextView) view.findViewById(R.id.title_hint_text);
        if ("A".equals(BASENAME)){
            titleHitText.setText(mApplication.getResources().getString(R.string.gateway_settings_alarm_hint));
        }else if ("P".equals(BASENAME)){
            titleHitText.setText(mApplication.getResources().getString(R.string.gateway_settings_push_hint));
        }
	}
	@Override
	public void onResume() {
		super.onResume();
		initItems();
	}
	private void initItems() {
		List<AbstractSettingItem> items = new ArrayList<AbstractSettingItem>();
		SpeakItem speakItem = new SpeakItem(mActivity);
		speakItem.initSystemState();

        ShockItem shockItem = new ShockItem(mActivity);
        shockItem.initSystemState();

        HintAudioItem hintAudioItem = new HintAudioItem(mActivity);
        hintAudioItem.initSystemState();

		items.add(speakItem);
        items.add(hintAudioItem);
		items.add(shockItem);
		settingManagerAdapter.swapData(items);
	}

	public class SpeakItem extends AbstractSettingItem {

		public SpeakItem(Context context) {
			super(context, R.drawable.icon_speak_reminder, mApplication.getResources().getString(
							R.string.set_sound_notification_voice_prompt));
		}

		@Override
		public void initSystemState() {
			super.initSystemState();
			infoTextView.setVisibility(View.VISIBLE);
			chooseToggleButton.setVisibility(View.VISIBLE);
			String speedStr  = mContext.getString(R.string.set_sound_notification_speed)+preference.getInt(BASENAME + "_" + IPreferenceKey.P_KEY_VOICE_SPEED,5);
			infoTextView.setText(speedStr);
			infoTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					pickSpeakSpeed();
				}
			});
			chooseToggleButton.setChecked(preference.getBoolean(
                    BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_TTS_ENABLE, true));
			chooseToggleButton
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							preference
									.putBoolean(
                                            BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_TTS_ENABLE,
											isChecked);
						}
					});
		}

		@Override
		public void doSomethingAboutSystem() {
		}
		public void pickSpeakSpeed(){
			Intent alarmActivity = new Intent(getActivity(),
					SpeakSpeedPickActivity.class);
            alarmActivity.putExtra("BASENAME" , BASENAME);
			startActivity(alarmActivity);
		}
	}

	public class HintAudioItem extends AbstractSettingItem{

	
		public HintAudioItem(Context context) {
			super(context);
		}

		@Override
		public void initSystemState() {
			super.initSystemState();
			boolean alarmEnable = preference.getBoolean(BASENAME + "_" +
                    IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO_ENABLE, true);
			String alarmName = preference.getString(
                    BASENAME + "_" +
                            IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO_NAME, defaultName);
			infoTextView.setVisibility(View.VISIBLE);
			chooseToggleButton.setVisibility(View.VISIBLE);
			if (defaultName.equals(alarmName)) {
				preference.putString(BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO,
						defaultPath);
			}
			iconImageView.setImageResource(R.drawable.icon_voice_remind);
			nameTextView.setText(mApplication.getResources().getString(R.string.gateway_settings_tone));
			infoTextView.setText(alarmName);
			chooseToggleButton.setChecked(alarmEnable);
			infoTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					pickAlarmAudio();
				}
			});
			chooseToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					preference.putBoolean(
                            BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO_ENABLE,
							isChecked);
				}
			});
		}

		@Override
		public void doSomethingAboutSystem() {
			
		}
		public void pickAlarmAudio() {
			Intent alarmActivity = new Intent(getActivity(),
					BellAudioPickActivity.class);
            alarmActivity.putExtra("BASENAME" , BASENAME);
            startActivity(alarmActivity);
		}
	}

    public class ShockItem extends AbstractSettingItem {

        public ShockItem(Context context) {
            super(context, R.drawable.icon_vibrate_remind, context.getResources()
                    .getString(R.string.device_shake_notification));
        }

        @Override
        public void initSystemState() {
            super.initSystemState();
            chooseToggleButton.setVisibility(View.VISIBLE);
            chooseToggleButton.setChecked(getBoolean(BASENAME+"_"+
                    IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_VIBRATE_ENABLE, true));
            chooseToggleButton
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            putBoolean(BASENAME+"_"+
                                    IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_VIBRATE_ENABLE,
                                    isChecked);
                        }
                    });
        }

        @Override
        public void doSomethingAboutSystem() {

        }

    }
}
