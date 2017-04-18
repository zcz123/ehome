package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.adapter.SigninRecordsAdapterV5;

import com.yuantuo.customview.ui.ListPopupWindow;
import com.yuantuo.customview.ui.ScreenSize;

public class AccountMenu
{
	private static final int INVALID_POSITION = -1;

	private final BaseActivity mActivity;
	private final ListPopupWindow mListPopupWindow;
	private final SigninRecordsAdapterV5 mRecordsAdapter;
	private List<GatewayInfo> mAccountData = new ArrayList<GatewayInfo>();

	private int mLastSelectionIndex = INVALID_POSITION;

	@SuppressLint("NewApi")
	public AccountMenu( BaseActivity activity, View anchorView )
	{
		mActivity = activity;

//		Drawable popupBackground = activity.getDrawable(R.drawable.scene_task_dropdown_background);
//		Drawable listSelector = activity.getDrawable(R.drawable.list_selector_holo_light);

		mRecordsAdapter = new SigninRecordsAdapterV5(activity);

		mListPopupWindow = new ListPopupWindow(activity);
		mListPopupWindow.setModal(true);
		mListPopupWindow.setAnchorView(anchorView);
//		mListPopupWindow.setBackgroundDrawable(popupBackground);
//		mListPopupWindow.setListSelector(listSelector);
		mListPopupWindow.setOnItemClickListener(mSigninRecordsItemClickListener);
		mListPopupWindow.setAdapter(mRecordsAdapter);
	}


	public void setBackgroundDrawable( Drawable background ){
		mListPopupWindow.setBackgroundDrawable(background);
	}

	public void showDeleteButton( boolean isDel ){
		mRecordsAdapter.showDeleteButton(isDel);
	}

	public void showMenu(){
		if (mListPopupWindow.isShowing()){
			mListPopupWindow.dismiss();
		}
		else{
			mListPopupWindow.setHeight(ScreenSize.screenHeight / 3);
			mListPopupWindow.setWidth(ScreenSize.screenWidth / 3);
			mListPopupWindow.show();
			mListPopupWindow.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			mListPopupWindow.setSelection(mLastSelectionIndex);
		}
	}

	public void dismissMenu(){
		mListPopupWindow.dismiss();
	}

	public List<GatewayInfo> getRecordsData(){
		return mAccountData;
	}

	public void setCurrentAccountSelection( String gwID ){
		final List<GatewayInfo> infos = mAccountData;
		int size = infos.size();

		for (int i = 0; i < size; i++){
			GatewayInfo info = infos.get(i);
			String infoID = info.getGwID();
			if (!TextUtils.isEmpty(infoID) && TextUtils.equals(infoID, gwID)){
				mLastSelectionIndex = i;
				break;
			}
		}
	}


	private final AdapterView.OnItemClickListener mSigninRecordsItemClickListener = new AdapterView.OnItemClickListener()
	{
		@Override
		public void onItemClick( AdapterView<?> parent, View view, int position, long id ){
			mListPopupWindow.dismiss();

			GatewayInfo gatewayInfo = (GatewayInfo) parent.getItemAtPosition(position);
			AccountManager accountManger = mActivity.getAccountManager();
			
			// switch choosed account
			// TODO did mCurrentInfo will be null????
			if (TextUtils.equals(gatewayInfo.getGwID(),
					accountManger.getmCurrentInfo().getGwID())){
				SendMessage.sendRefreshDevListMsg(accountManger.getmCurrentInfo().getGwID());
			}
			else{
				accountManger.switchAccount( gatewayInfo);
			}
		}
	};
}
