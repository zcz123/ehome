package com.yuantuo.customview.loader.interfaces;

/**
 * 搜索助手回调接口
 * 
 * @author wing
 * 
 * @param <Type>
 *          结果类型
 */
public interface OnSearchCallBack<Type> extends Searcher<Type>
{
	/**
	 * 是否显示进度条对话框
	 * 
	 * @return
	 */
	public boolean setProgressDialogShown();

	/**
	 * 搜索时进度条的提示文字
	 * 
	 * @return
	 */
	public String getSearchHint();

	/**
	 * 搜索对话框停留时间
	 * 
	 * @return
	 */
	public int getSearchShowTime();

	/**
	 * 当{@link #wantHandleSearchNoResult()}为<code><b>FALSE</b></code> 搜索结果为空时显示的文字
	 * 
	 * @return
	 */
	public String getSearchNoResultContent();

	/**
	 * 搜索过程
	 * 
	 * @return
	 */
	public Type onSearching();

	/**
	 * 搜索成功
	 */
	public void onSearchSuccess( Type results );

	/**
	 * 搜索失败
	 */
	public void onSearchFail();

	/**
	 * 当{@link #wantHandleSearchNoResult()}为<code><b>TRUE</b></code>,自己处理空结果事件
	 */
	public void onSearchNoResult();

	/**
	 * 是否自己处理搜索的结果为空，默认弹出对话框提示为空(default <code><b>FALSE</b></code>)
	 * 
	 * @return
	 */
	public boolean wantHandleSearchNoResult();
}
