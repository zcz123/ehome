package cc.wulian.smarthomev5.fragment.uei;


import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.uei.AirStateStandard;
import cc.wulian.smarthomev5.tools.DownUpMenuList;
import cc.wulian.smarthomev5.tools.DownUpMenuList.DownUpMenuItem;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ACTypeAdapter extends BaseAdapter{
	
	private Context context;
	int width =0;
	List<AirStateStandard> airStatusList=null;
	GridView.LayoutParams gvParams=null;
	String deviceCode;
	public ACTypeAdapter(Context context,String title,String deviceCode,List<AirStateStandard> airStatusList){
		this.context = context;
		this.airStatusList=airStatusList; 
		this.deviceCode=deviceCode;
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth()-60;
		gvParams=new GridView.LayoutParams(width/2, width/2);
	}
	private int viewMode=0;
	public void SetViewMode(int viewMode){
		this.viewMode=viewMode;
	}
	public ACTypeAdapter(Context context,List<AirStateStandard> airStatusList){
		this.context = context;
		
		this.airStatusList=airStatusList;
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth()-60;
		gvParams=new GridView.LayoutParams(width/2, width/2);
	}
	@Override
	public int getCount() {
		return this.airStatusList.size();
	}

	@Override
	public Object getItem(int index) {
		return this.airStatusList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return 0;
	}
	public synchronized void swapData( List<AirStateStandard> newData ){
		if (newData == null){
			if (airStatusList != null){
				airStatusList.clear();
				notifyDataSetInvalidated();
			}
		}
		else{
			airStatusList = newData;
			notifyDataSetChanged();
		}
	}
	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		AirStatusHolderView holderView=null;
//		if(view==null){
			view=LayoutInflater.from(this.context).inflate(R.layout.item_aircondmode, null);
			AbsListView.LayoutParams params=(LayoutParams) view.getLayoutParams();
			if(params==null){
				view.setLayoutParams(gvParams);
			}else{
				params.width=width;
				params.height=width;
			}
			holderView=new AirStatusHolderView(this.context);
			holderView.modecommlayout=(LinearLayout) view.findViewById(R.id.modecommlayout);
			holderView.modecommimage=(ImageButton) view.findViewById(R.id.modecommimage);
			holderView.modecommName1=(TextView) view.findViewById(R.id.modecommName1);
			holderView.modecommName2=(TextView) view.findViewById(R.id.modecommName2);
			holderView.modecomplexlayout=(LinearLayout) view.findViewById(R.id.modecomplexlayout);
			holderView.windspeedImage=(ImageButton) view.findViewById(R.id.windspeedImage);
			holderView.airmodeimage=(ImageButton) view.findViewById(R.id.airmodeimage);
			holderView.temperaturetv=(TextView) view.findViewById(R.id.temperaturetv);
			holderView.temperatureunittv=(TextView) view.findViewById(R.id.temperatureunittv);
			holderView.wind_left_rightimage=(ImageButton) view.findViewById(R.id.wind_left_rightimage);
			holderView.wind_up_downimage=(ImageButton) view.findViewById(R.id.wind_up_downimage);
			holderView.customernametv=(TextView) view.findViewById(R.id.customernametv);
			holderView.airspeedflagimage=(ImageButton) view.findViewById(R.id.airspeedflagimage);
			holderView.line_top=view.findViewById(R.id.line_top);
			holderView.line_buttom=view.findViewById(R.id.line_buttom);
			holderView.line_left=view.findViewById(R.id.line_left);
			holderView.line_right=view.findViewById(R.id.line_right);
			view.setTag(holderView);
//		}else{
//			holderView=(AirStatusHolderView) view.getTag();
//		}
		if(this.viewMode==0){
			view.setOnClickListener(viewOnclick_fordevice);
			view.setBackgroundResource(R.drawable.item_bgd);

			view.setOnLongClickListener(viewOnLongclick);
		}else if(this.viewMode==1){
			view.setOnClickListener(viewOnclick_forhourse);
		}
		holderView.tag=position;
		AirStateStandard airStatus=airStatusList.get(position);
		holderView.FillData(airStatus);
		return view;
	}
	
	View.OnClickListener viewOnclick_fordevice=new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			AirStatusHolderView holderView=(AirStatusHolderView) view.getTag();
			AirStateStandard airStatus=airStatusList.get((int)holderView.tag);
			if(ACTypeAdapter.this.onClickPopupWidowItem!=null){
				ACTypeAdapter.this.onClickPopupWidowItem.ItemShortTimeClick(airStatus);
			}
		}
	};
	private View curSelectedView=null;
	View.OnClickListener viewOnclick_forhourse=new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			
			if(curSelectedView!=null){
				AirStatusHolderView last_holderView=(AirStatusHolderView) curSelectedView.getTag();
				last_holderView.line_top.setVisibility(View.GONE);
				last_holderView.line_buttom.setVisibility(View.GONE);
				last_holderView.line_left.setVisibility(View.GONE);
				last_holderView.line_right.setVisibility(View.GONE);
			}
			AirStatusHolderView holderView=(AirStatusHolderView) view.getTag();
			holderView.line_top.setVisibility(View.VISIBLE);
			holderView.line_buttom.setVisibility(View.VISIBLE);
			holderView.line_left.setVisibility(View.VISIBLE);
			holderView.line_right.setVisibility(View.VISIBLE);
			AirStateStandard airStatus=airStatusList.get((int)holderView.tag);
			curSelectedView=view;
			if(ACTypeAdapter.this.onClickPopupWidowItem!=null){
				ACTypeAdapter.this.onClickPopupWidowItem.ItemShortTimeClick(airStatus);
			}
		}
	};
	View.OnLongClickListener viewOnLongclick=new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View view) {

			iniPopupWidow(view);
			return true;
		}		
	};
	private void iniPopupWidow(View v) {
		final DownUpMenuList downMenu = new DownUpMenuList(this.context);
		AirStatusHolderView holderView=(AirStatusHolderView) v.getTag();
		final  AirStateStandard airStatus=airStatusList.get((int)holderView.tag);
		DownUpMenuItem editItem = new DownUpMenuItem(this.context) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(this.context.getResources().getString(
						R.string.device_config_edit_dev_area_create_item_rename_success));
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_topcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
					return;
				}else {
					if(ACTypeAdapter.this.onClickPopupWidowItem!=null){
						ACTypeAdapter.this.onClickPopupWidowItem.ItemUpdateClick(airStatus);
					}
				}
				downMenu.dismiss();
			}
		};
		DownUpMenuItem deleteItem = new DownUpMenuItem(this.context) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(this.context.getResources().getString(
						R.string.device_config_edit_dev_area_create_item_delete));
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_topcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
					return;
				}else{
					if(ACTypeAdapter.this.onClickPopupWidowItem!=null){
						ACTypeAdapter.this.onClickPopupWidowItem.ItemDeleteClick(airStatus);
					}
				}
				downMenu.dismiss();
			}
		};

		DownUpMenuItem cancelItem = new DownUpMenuItem(this.context) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(this.context.getResources().getString(
						R.string.cancel));
				linearLayout.setPadding(0, 30, 0, 0);
				mTitleTextView.setBackgroundResource(R.drawable.downup_menu_item_allcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				downMenu.dismiss();
			}
		};
		
		ArrayList<DownUpMenuItem> menuItems = new ArrayList<DownUpMenuList.DownUpMenuItem>();
		menuItems.add(editItem);
		menuItems.add(deleteItem);
		menuItems.add(cancelItem);
		downMenu.setMenu(menuItems);
		downMenu.showBottom(v);
	}
	
	public interface OnClickPopupWidowItem{
		void ItemDeleteClick(AirStateStandard airStates);
		void ItemUpdateClick(AirStateStandard airStates);
		void ItemShortTimeClick(AirStateStandard airStates);
	}
	
	private OnClickPopupWidowItem onClickPopupWidowItem;
	public void SetOnClickPopupWidowItem(OnClickPopupWidowItem OnItemClick){
		this.onClickPopupWidowItem=OnItemClick;
	}
}
