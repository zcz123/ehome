package cc.wulian.smarthomev5.activity.uei;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cc.wulian.smarthomev5.R;
;

public class ExpandPopupWindow extends PopupWindow{

	private View rootView; 
	private GridView gvNumber;
	private TextView tvTitle;
	private LinearLayout linOther;
//	,OnItemClickListener itemListener
	public ExpandPopupWindow (Activity context){
		super(context);
    	rootView = View.inflate(context,R.layout.keyboard_popupwindow, null);
        gvNumber =(GridView) rootView.findViewById(R.id.gv_keyborder);
        //tvTitle = (TextView) rootView.findViewById(R.id.tv_key_title);
        linOther = (LinearLayout) rootView.findViewById(R.id.lin_otherkey);
        
        ExpandAdapter nadapter = new ExpandAdapter(context);
        gvNumber.setAdapter(nadapter);
//        gvNumber.setOnItemClickListener(itemListener);
        linOther.setVisibility(View.GONE);
        
        this.setContentView(rootView);
        this.setWidth(LayoutParams.MATCH_PARENT);  
        this.setHeight(LayoutParams.MATCH_PARENT); 
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);  
        this.setBackgroundDrawable(dw); 
        setOutsideTouchable(true);
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
    }
	
	private class ExpandAdapter extends BaseAdapter{
		
		private Context context;
		private String[] data={"制式","九宫格","画面静止","......"};
		
		public ExpandAdapter(Context context){
			this.context=context;
			
		}
		
		
		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int arg0) {
			return data[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
	    	if(arg1==null){
	    		arg1  = View.inflate(context, R.layout.expand_grid_item, null);
	    	}
	    	TextView tv=(TextView) arg1.findViewById(R.id.tv_expand);
	    	tv.setText(data[arg0]);
			return arg1;
		}
		
	}
}
