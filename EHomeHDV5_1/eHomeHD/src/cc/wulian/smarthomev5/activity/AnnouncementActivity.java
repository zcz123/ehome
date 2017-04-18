package cc.wulian.smarthomev5.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.tools.AnnouncementManager;
import cc.wulian.smarthomev5.tools.AnnouncementManager.Announcement;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;

import com.alibaba.fastjson.JSONObject;

public class AnnouncementActivity extends EventBusActivity {
	private AnnouncementAdapter mAdapter;
	private ListView mListView;
	private AnnouncementManager manager = AnnouncementManager.getInstance();
	private static final Handler mHandler = new Handler(Looper.getMainLooper());
	private Map<String, Bitmap> FileBitMap = new HashMap<String, Bitmap>();
	private Bitmap bitMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus_company_notice);
		initBar();
		mListView = (ListView) findViewById(R.id.aboutus_company_notice_listview);
		mAdapter = new AnnouncementAdapter(this, null);
		mListView.setAdapter(mAdapter);
		getLoaderData();
	}

	public void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.about_us));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.about_function_announcement));
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}

	private void getLoaderData() {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				List<Announcement> list = manager.loadNoties(mAccountManager.getRegisterInfo().getAppID(),mAccountManager.getmCurrentInfo().getGwID());
				final List<Announcement> mEntites = new ArrayList<Announcement>(list);
				getPictureForList(mEntites);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						mAdapter.swapData(mEntites);
					}
				});
			}
		});
	}

	public void getPictureForList(List<Announcement> list) {
		for (Announcement entity : list) {
			getCurPicture(entity);
		}
	}

	private class AnnouncementAdapter extends WLBaseAdapter<Announcement> {
		private ImageView activePicture;
		private TextView activeName;
		private TextView activeTime;
		private TextView activeDetail;
		private TextView activeUrl;

		public AnnouncementAdapter(Context context, List<Announcement> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.aboutus_company_notice_item, null);
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				final Announcement item) {
			super.bindView(context, view, pos, item);

			activeName = (TextView) view
					.findViewById(R.id.aboutus_company_notice_active_name);
			activeTime = (TextView) view
					.findViewById(R.id.aboutus_company_notice_active_time);
			activePicture = (ImageView) view
					.findViewById(R.id.aboutus_company_notice_active_picture);
			activeDetail = (TextView) view
					.findViewById(R.id.aboutus_company_notice_active_detail);
			activeUrl = (TextView) view
					.findViewById(R.id.aboutus_company_notice_active_url);
			activeName.setText(item.getActiveName());
			activeTime.setText(getCurActiveTime(item.getActiveDeployTime()));
			if (FileBitMap.get(item.getActivePictureUrl()) == null) {
				activePicture.setImageDrawable(getResources().getDrawable(
						R.drawable.announcement_default_picture_wl));
			} else {
				activePicture.setImageBitmap(FileBitMap.get(item
						.getActivePictureUrl()));
			}
			activeDetail.setText(item.getActiveDetail());
			activeUrl.setText(getResources().getString(
					R.string.about_function_announcement_look_original));
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String url = item.getActiveUrl();
					if(StringUtil.isNullOrEmpty(url)){
						try {
							JSONObject data=new JSONObject();
							data.put("deployTime", item.getActiveDeployTime());
							data.put("detail", item.getActiveDetail());
							data.put("name", item.getActiveName());
							data.put("pictureUrl", item.getActivePictureUrl());
							url="file:///android_asset/aboutus/announcement.html?data="+URLEncoder.encode(data.toJSONString(),"utf-8");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						if(!url.startsWith("http://")){
							url = "http://"+url;
						}
					}
					IntentUtil.startCustomBrowser(mContext,url, item.getActiveName(),getResources().getString(R.string.about_back));
				}
			});
		}
	}

	// 时间戳转换
	public String getCurActiveTime(String activeDeployTime) {
		long timeStamp = Long.parseLong(activeDeployTime);
		String curActiveTime = DateUtil.getFormatMiddleTime(timeStamp);
		return curActiveTime;
	}

	public String getFileName(Announcement item) {
		String fileName = null;
		try {
			fileName = URLEncoder.encode(
					item.getVersion() + item.getActivePictureUrl(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return fileName;

	}

	private void getCurPicture(final Announcement item) {
		if (!StringUtil.isNullOrEmpty(item.getActivePictureUrl())) {
			TaskExecutor.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					String fileName = getFileName(item);
					try {
						fileName = URLEncoder.encode(
								item.getVersion() + item.getActivePictureUrl(),
								"UTF-8");

						String floder = FileUtil.getAnnouncementPath();
						if (!FileUtil.checkFileExistedAndAvailable(floder + "/"
								+ fileName)) {
							byte[] bPucture = HttpUtil.getPicture(item
									.getActivePictureUrl());
							if (bPucture != null) {
								bitMap = FileUtil.Bytes2Bitmap(bPucture);
								FileUtil.saveBitmapToPng(bitMap, floder,
										fileName);

							}
						}
						bitMap = BitmapFactory.decodeFile(floder + "/"
								+ fileName);
						FileBitMap.put(item.getActivePictureUrl(), bitMap);

						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if (FileBitMap != null) {
									mAdapter.notifyDataSetChanged();
								}

							}
						});
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
