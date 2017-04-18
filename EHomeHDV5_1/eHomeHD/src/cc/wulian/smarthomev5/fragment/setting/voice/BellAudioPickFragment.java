package cc.wulian.smarthomev5.fragment.setting.voice;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.RingtonSetAdapter;
import cc.wulian.smarthomev5.entity.AudioEntity;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.MediaPlayerTool;
import cc.wulian.smarthomev5.tools.Preference;

public class BellAudioPickFragment extends WulianFragment {
	private Resources resources;
	private RingtonSetAdapter mAdapter;
	private ListView mListViewShowAudios;
	private Preference preference = Preference.getPreferences();
	private static String  BASENAME;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resources = mApplication.getResources();
		Bundle bundle = getArguments();
		if (bundle == null) {
			return;
		}
		BASENAME = getActivity().getIntent().getStringExtra("BASENAME");
		mAdapter = new RingtonSetAdapter(mActivity, getAvailableAudioEntites(),
				BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO);
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
						AudioEntity entity = mAdapter.getSelectedAudioEntity();
						if (entity == null) {
							mActivity.finish();
							return ;
						}
						String exitURL = entity.getmAudioPath();
						preference.putString(
								BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO, exitURL);
						preference.putString(
								BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO_NAME,
								entity.getmAudioName());
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
		mAdapter.setSelectedPath(preference.getString(BASENAME + "_" + IPreferenceKey.P_KEY_ALARM_NOTE_TYPE_AUDIO, ""));
	}

	private List<AudioEntity> getAvailableAudioEntites() {
		ArrayList<AudioEntity> audioEntites = new ArrayList<AudioEntity>();
		try{
			AudioEntity defaultHead = new AudioEntity(
					resources
							.getString(R.string.set_sound_notification_bell_prompt_choose_default),
					"");
			audioEntites.add(defaultHead);
			RingtoneManager rm = new RingtoneManager(mActivity);
			rm.setType(RingtoneManager.TYPE_NOTIFICATION);
			Cursor cursor = rm.getCursor();
			while (cursor.moveToNext()) {
				String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
				String url = cursor.getString(RingtoneManager.URI_COLUMN_INDEX);
				String id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
				audioEntites.add(new AudioEntity(title, url + "/" + id));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return audioEntites;
	}
	@Override
	public void onDetach() {
		super.onDetach();
		MediaPlayerTool.stop();
	}

}
