package com.wulian.iot.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class SimpleAdapter<T> extends BaseAdapter{

	protected Context context = null;
	protected List<T> eList = null;
	protected LayoutInflater layoutInflater = null;
	public SimpleAdapter(){
		
	}
	public SimpleAdapter(Context context,List<T> eList){
         this.context = context;
         this.eList   = eList == null?new ArrayList<T>():eList;
         this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		return eList.size();
	}

	@Override
	public T getItem(int position) {
		return eList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return this.view(position,convertView,parent);
	}
	/**添加数据及时刷新*/
	public void add(List<T> eList,boolean what){
		if(eList!=null&&eList.size()>0){
			if(what){
				this.eList.clear();
			}
			this.eList.addAll(eList);
		}
		this.notifyDataSetChanged();
	}
	public List<T> add(List<T> eList){
		if(eList!=null&&eList.size()>0)
		  if(this.eList.addAll(eList)){
			 this.notifyDataSetChanged();
		     return eList;	  
		  }
		  return eList;
	}
	public void add(T t,int position){
		if(t!=null){
		if(position != -1){
			this.eList.add(position, t);
		}	
		this.notifyDataSetChanged();
		}
	}
	public int delete(int position){
		if(position!=-1){
			this.eList.remove(position);
			return position;
		}
		return -1;
	}
	public void clear(){
		if(this.eList.size()>0){
			this.eList.clear();
		}
	}
    //add syf
	public void swapData(List<T>newData){
		if(eList!=null){
			if(newData!=null){
				eList.clear();
				eList.addAll(newData);
				this.notifyDataSetChanged();
			}
		}
}
	/**
	 * 抽象接口
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public abstract View view(int position, View convertView,ViewGroup parent);
    		
}
