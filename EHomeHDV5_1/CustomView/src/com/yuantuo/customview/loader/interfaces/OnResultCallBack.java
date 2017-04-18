package com.yuantuo.customview.loader.interfaces;

import android.content.DialogInterface;
import android.view.View;

/**
 * 搜索返回助手接口
 * 
 * @author wing
 * 
 * @param <Type>
 *          结果类型
 */
public interface OnResultCallBack<Type> extends Searcher<Type>
{
	/**
	 * 结果对话框标题
	 * 
	 * @return
	 */
	public String getResultDialogTilte();

	/**
	 * 结果对话框图标
	 * 
	 * @return
	 */
	public int getResultDialogIcon();

	/**
	 * 结果对话框按钮文字
	 * 
	 * @return
	 */
	public int getResultDialogButton();

	/**
	 * 点击结果对话框按钮
	 * 
	 * @param dialog
	 */
	public void onResultDialogButtonClicked( DialogInterface dialog );

	/**
	 * 当{@link #isCustomResultView()} <code><b>FALSE</b></code>,显示结果列表
	 * 
	 * @param results
	 * @return
	 */
	public CharSequence[] setResultDialogContent( Type results );

	/**
	 * 是否自定义搜索界面
	 * 
	 * @return
	 */
	public boolean isCustomResultView();

	/**
	 * 当{@link #isCustomResultView()} <code><b>TRUE</b></code>,显示自定义界面列表
	 * 
	 * @param results
	 * @return
	 */
	public View setResultDialogViewContent( Type results );

	/**
	 * 点击结果列表某一项
	 * 
	 * @param dialog
	 * @param results
	 * @param which
	 */
	public void onResultDialogSelected( DialogInterface dialog, Type results, int which );

	/**
	 * 当搜索的结果只有一项时，是否自动选择该项
	 * 
	 * @return
	 */
	public boolean isSingleResultAutoChoose();
	
	/**
	 * 是否搜索完毕显示结果对话框
	 * 
	 * @return
	 */
	public boolean isWantShowResult();
}
