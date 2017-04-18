package cc.wulian.smarthomev5.utils;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.entity.GuideEntity;

public class GuideUtil {

	private FrameLayout rootLayout;
	
	private LinearLayout guidLayout;
	
	private Context context;
	
	private boolean isGuiding=false;
	
	private List<GuideEntity> guideEntitys;
	
	private int currentStep=0;
	
	private GuideCallback callback;
	
	public GuideUtil(Context context,FrameLayout rootLayout){
		this.rootLayout=rootLayout;
		this.context=context;
		createGuidLayout();
	}
	
	/**
	 * 创建引导页的根布局
	 */
	private void createGuidLayout(){
		
		this.guidLayout=new LinearLayout(context);
		LinearLayout.LayoutParams  params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		guidLayout.setOrientation(LinearLayout.VERTICAL);
		guidLayout.setBackgroundColor(Color.parseColor("#88323232"));
		guidLayout.setVisibility(View.INVISIBLE);
		rootLayout.addView(guidLayout,params);
	}
	
	/**
	 * 往引导页的 引导布局中添加View
	 * @param actionView 为那个View 添加引导
	 * @param view  引导要展示的view布局
	 */
	private void addNewGuidView(GuideEntity entity){
		//移除根布局的已有控件
		guidLayout.removeAllViews();
		guidLayout.setVisibility(View.VISIBLE);
		LinearLayout.LayoutParams  params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		//获取展示布局要摆放的位置
		Rect position =new Rect();
		entity.getActionView().getGlobalVisibleRect(position);
		int drawAreatop=((Activity)context).getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		if(entity.getGravity()==Gravity.BOTTOM){
			params.setMargins(position.left,position.top-drawAreatop, 0, 0);
		}else if(entity.getGravity()==Gravity.TOP){
			int[] size=measureView(entity.getTargetView());
			params.setMargins(position.left,position.bottom-drawAreatop-size[1], 0, 0);
		}
		//将展示布局展示出来
		guidLayout.addView(entity.getTargetView(),params);	
	}
	
	public void beginGuide(List<GuideEntity> entitys){
		this.guideEntitys = entitys;
		if(guideEntitys==null||isGuiding)return;
		currentStep=0;
		isGuiding=true;
		guidLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(currentStep<guideEntitys.size()){
					GuideEntity entity=guideEntitys.get(currentStep++);
					addNewGuidView(entity);
				}else{
					endGuid();
				}
			}
		});	
		final GuideEntity entity=guideEntitys.get(currentStep++);
		ViewTreeObserver vto= entity.getActionView().getViewTreeObserver();
		vto.addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				entity.getActionView().getViewTreeObserver().removeOnPreDrawListener(this);
				addNewGuidView(entity);
				return false;
			}
		});
		
	}
	
	private int[] measureView(View view){
		int[] size=new int[2];
		int w=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h=View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		view.measure(w, h);
		size[0]=view.getMeasuredWidth();
		size[1]=view.getMeasuredHeight();
		return size;
	}
	
	/**
	 * 结束引导
	 */
	public void endGuid(){
		guidLayout.removeAllViews();
		guidLayout.setVisibility(View.GONE);
		rootLayout.removeView(guidLayout);
		isGuiding=false;
		if(callback!=null)callback.onGuideOver();
	}		

	public void setCallback(GuideCallback callback) {
		this.callback = callback;
	}
	
	public interface GuideCallback{
		public void  onGuideOver();
	}
}
