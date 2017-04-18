package cc.wulian.smarthomev5.activity.uei;


import java.util.List;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.uei.UeiVirtualKey;
import cc.wulian.smarthomev5.fragment.uei.VirtualKeyButton;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class KeyboardPopupWindow extends PopupWindow{

	private View rootView; 
	private GridView gvNumber;
	private LinearLayout linback,linzero,linselect;
	private LinearLayout pop_layout_key;
	private VirtualKeyButton virkeyBtn;
	private Context mcontext;
	NumberAdapter nadapter =null;
	public KeyboardPopupWindow (Activity context,VirtualKeyButton virkeyBtn){
    	super(context);
    	this.mcontext=context.getBaseContext();
    	this.virkeyBtn=virkeyBtn;
    	rootView = View.inflate(context,R.layout.keyboard_popupwindow, null);
    	pop_layout_key=(LinearLayout) rootView.findViewById(R.id.pop_layout_key);
        gvNumber = (GridView) rootView.findViewById(R.id.gv_keyborder);
        nadapter= new NumberAdapter(context);
        gvNumber.setAdapter(nadapter);
        
        this.setContentView(rootView);
        this.setWidth(LayoutParams.MATCH_PARENT);  
        this.setHeight(LayoutParams.MATCH_PARENT); 
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);  
        this.setBackgroundDrawable(dw); 
        // 设置SelectPicPopupWindow弹出窗体动画效果
        
        this.setAnimationStyle(R.style.keyboardanimstyle);
        
        
        rootView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				int heightTop = v.findViewById(R.id.pop_layout_key).getTop();
				int heightBottom = v.findViewById(R.id.pop_layout_key).getBottom();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<heightTop||y>heightBottom){
						dismiss();
					}
				}				
				return true;
			}
		});
        int viewMode=this.virkeyBtn.getArgs().getViewMode();
        this.virkeyBtn.RegiestVirtualKeyEvent(pop_layout_key);
        if(viewMode==0){
            gvNumber.setOnItemClickListener(itemListener_fordevice);
        }else if(viewMode==1){
        	gvNumber.setOnItemClickListener(itemListener_forhouse);
        }
    }
	
	private OnItemClickListener itemListener_fordevice = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int location,long arg3) {
			UeiVirtualKey virKey=(UeiVirtualKey) KeyboardPopupWindow.this.nadapter.getItem(location);
			virkeyBtn.vibratorLinght();
			KeyboardPopupWindow.this.virkeyBtn.SendUeiKey(virKey.getVirKey());
		}
	};
	private OnItemClickListener itemListener_forhouse = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int location,long arg3) {
			UeiVirtualKey virKey=(UeiVirtualKey) KeyboardPopupWindow.this.nadapter.getItem(location);
			if(KeyboardPopupWindow.this.virkeyBtn.curSelectedView!=null){
				KeyboardPopupWindow.this.virkeyBtn.curSelectedView.setSelected(false);
			}
			view.setSelected(true);//virKey
			String epData=KeyboardPopupWindow.this.virkeyBtn.getEpdata(virKey.getVirKey());
			KeyboardPopupWindow.this.virkeyBtn.setCurrEpData(epData);
			KeyboardPopupWindow.this.virkeyBtn.curSelectedView=view;
			if(StringUtil.isNullOrEmpty(epData)){
				view.setSelected(false);
				Toast.makeText(KeyboardPopupWindow.this.mcontext, "未学习按键!", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private class NumberAdapter extends BaseAdapter{
		
		private Context context;
		List<UeiVirtualKey> virKeyList=null;
		public NumberAdapter(Context context){
			virKeyList=UeiVirtualKey.getDigitKey();
			this.context=context;
		}
		@Override
		public int getCount() {
			return 9;
		}

		@Override
		public Object getItem(int location) {
			return virKeyList.get(location);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int loaction, View view, ViewGroup arg2) {
			ViewHolder viewHolder=null;
	    	if(view==null){
	    		viewHolder=new ViewHolder();
	    		view  = View.inflate(context, R.layout.keyboard_grid_item, null);
	    		viewHolder.tv=(TextView) view.findViewById(R.id.tv);
	    		view.setTag(viewHolder);
	    	}else{
	    		viewHolder=(ViewHolder) view.getTag();
	    	}
	    	
	    	UeiVirtualKey virkey=virKeyList.get(loaction);
	    	viewHolder.tv.setText(virkey.getUiText());
	    	viewHolder.tv.setTag(virkey.getVirKey());
			return view;
		}
		
		class ViewHolder{
			TextView tv;
		}
	}
}
