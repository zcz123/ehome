package com.yuantuo.customview.ui;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yuantuo.customview.R;


public class WLListViewBuilder{

	private ScrollView scrollView;
	private ListAdapter listAdapter;
	private Context context;
	private Resources resources;
	private LayoutInflater inflater;
	private LinearLayout headLineLayout;
	private ImageView headImageView;
	private TextView headTextView;
	private LinearLayout contentLineLayout;
	private LinearLayout footLineLayout;
	private ImageView footImageView;
	private TextView footTextView;
	public WLListViewBuilder(Context context) {
		this.context = context;
		this.resources = this.context.getResources();
		this.inflater = LayoutInflater.from(context);
		this.scrollView = (ScrollView)this.inflater.inflate(R.layout.custom_scroll_view_content, null);
		this.headLineLayout = (LinearLayout)this.scrollView.findViewById(R.id.custom_scroll_view_head_ll);
		this.headImageView = (ImageView)this.headLineLayout.findViewById(R.id.custom_scroll_view_head_iv);
		this.headTextView = (TextView)this.headLineLayout.findViewById(R.id.custom_scroll_view_head_tv);
		this.contentLineLayout = (LinearLayout)this.scrollView.findViewById(R.id.custom_scroll_view_content_ll);
		this.footLineLayout = (LinearLayout)this.scrollView.findViewById(R.id.custom_scroll_view_foot_ll);
		this.footImageView = (ImageView)this.footLineLayout.findViewById(R.id.custom_scroll_view_foot_iv);
		this.footTextView = (TextView)this.footLineLayout.findViewById(R.id.custom_scroll_view_foot_tv);
	}
	public void setHeadEnable(boolean isEnable){
		if(isEnable)
			this.headLineLayout.setVisibility(View.VISIBLE);
		else
			this.headLineLayout.setVisibility(View.GONE);
	}
	public void setFootEnable(boolean isEnable){
		if(isEnable)
			this.footLineLayout.setVisibility(View.VISIBLE);
		else
			this.footLineLayout.setVisibility(View.GONE);
	}
	public ListAdapter getAdapter() {
		return listAdapter;
	}

	public void setAdapter(ListAdapter listAdapter) {
		this.listAdapter = listAdapter;
		this.listAdapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				dataChanaged();
			}
		});
	}

	public ScrollView create(){
		if(listAdapter == null)
			return scrollView;
		for(int i=0 ; i<listAdapter.getCount();i++){
			this.contentLineLayout.addView(listAdapter.getView(i, null, contentLineLayout));
		}
		return scrollView;
	}
	private void dataChanaged() {
		this.contentLineLayout.removeAllViews();
		this.scrollView = create();
	}

}
