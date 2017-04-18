package cc.wulian.smarthomev5.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnMenuVisibilityListener;
import com.actionbarsherlock.view.Window;

public class ActionBarCompat
{
	private final Context mContext;
	private final ActionBar mActionBar;
	private final boolean mNullBar;
	private ImageView leftIcon;
	private TextView leftText;
	private LinearLayout leftIconAndText;
	private TextView centerTitle;
	private ImageView rightIcon;
	private TextView rightText;
	private LinearLayout rightMenuCustomLayout;
	private LinearLayout rightIconAndText;
	private LayoutInflater inflater;
	private RelativeLayout customView;
	private OnLeftIconClickListener leftIconClickListener;
	private OnRightMenuClickListener rightMenuClickListener;
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == leftIconAndText){
				if(leftIconClickListener != null){
					leftIconClickListener.onClick(v);
				}
			}else if(v == rightIconAndText){
				if(rightMenuClickListener != null){
					rightMenuClickListener.onClick(v);
				}
			}
		}
	};
	
	public ActionBarCompat( Context context, ActionBar actionBar )
	{
		mContext = context;
		mActionBar = actionBar;
		mActionBar.setDisplayShowCustomEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayUseLogoEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setHomeButtonEnabled(false);
		inflater = LayoutInflater.from(context);
		mNullBar = false;
		customView = (RelativeLayout)inflater.inflate(R.layout.common_custom_action_bar, null);
		mActionBar.setCustomView(customView);
		leftIcon = (ImageView)customView.findViewById(R.id.common_action_bar_left_icon);
		leftText = (TextView)customView.findViewById(R.id.common_action_bar_left_icon_text);
		leftIconAndText = (LinearLayout)customView.findViewById(R.id.common_action_bar_left_icon_and_text);
		centerTitle = (TextView)customView.findViewById(R.id.common_action_bar_center_title);
		rightIcon = (ImageView)customView.findViewById(R.id.common_action_bar_right_icon);
		rightText = (TextView)customView.findViewById(R.id.common_action_bar_right_icon_text);
		rightMenuCustomLayout = (LinearLayout)customView.findViewById(R.id.common_action_bar_right_icon_custom);
		rightIconAndText = (LinearLayout)customView.findViewById(R.id.common_action_bar_right_icon_and_text);
		leftIconAndText.setOnClickListener(listener);
		rightIconAndText.setOnClickListener(listener);
	}

	/**
	 * Set the action bar into custom navigation mode, supplying a view for custom navigation.
	 * 
	 * Custom navigation views appear between the application icon and any action buttons and may use any space available there. Common use cases for custom
	 * navigation views might include an auto-suggesting address bar for a browser or other navigation mechanisms that do not translate well to provided
	 * navigation modes.
	 * 
	 * @param view
	 *          Custom navigation view to place in the ActionBar.
	 *//*
	public void setCustomView( View view ){
		if (mNullBar) return;

		mActionBar.setCustomView(view);
	}

	*//**
	 * Set the action bar into custom navigation mode, supplying a view for custom navigation.
	 * 
	 * <p>
	 * Custom navigation views appear between the application icon and any action buttons and may use any space available there. Common use cases for custom
	 * navigation views might include an auto-suggesting address bar for a browser or other navigation mechanisms that do not translate well to provided
	 * navigation modes.
	 * </p>
	 * 
	 * <p>
	 * The display option {@link #DISPLAY_SHOW_CUSTOM} must be set for the custom view to be displayed.
	 * </p>
	 * 
	 * @param view
	 *          Custom navigation view to place in the ActionBar.
	 * @param layoutParams
	 *          How this custom view should layout in the bar.
	 * 
	 * @see #setDisplayOptions(int, int)
	 *//*
	public void setCustomView( View view, LayoutParams layoutParams ){
		if (mNullBar) return;

		mActionBar.setCustomView(view, layoutParams);
	}
*/
	/**
	 * Set the action bar into custom navigation mode, supplying a view for custom navigation.
	 * 
	 * <p>
	 * Custom navigation views appear between the application icon and any action buttons and may use any space available there. Common use cases for custom
	 * navigation views might include an auto-suggesting address bar for a browser or other navigation mechanisms that do not translate well to provided
	 * navigation modes.
	 * </p>
	 * 
	 * <p>
	 * The display option {@link #DISPLAY_SHOW_CUSTOM} must be set for the custom view to be displayed.
	 * </p>
	 * 
	 * @param resId
	 *          Resource ID of a layout to inflate into the ActionBar.
	 * 
	 * @see #setDisplayOptions(int, int)
	 */
	/*public void setCustomView( int resId ){
		if (mNullBar) return;

		mActionBar.setCustomView(resId);
	}*/

	/**
	 * Set the icon to display in the 'home' section of the action bar. The action bar will use an icon specified by its style or the activity icon by default.
	 * 
	 * Whether the home section shows an icon or logo is controlled by the display option {@link #DISPLAY_USE_LOGO}.
	 * 
	 * @param resId
	 *          Resource ID of a drawable to show as an icon.
	 * 
	 * @see #setDisplayUseLogoEnabled(boolean)
	 * @see #setDisplayShowHomeEnabled(boolean)
	 */
	public void setIcon( int resId ){
		if (mNullBar) return;
		leftIcon.setImageResource(resId);
	}
	public void setIconText(String text){
		leftText.setText(text);
	}
	public void setIconText(int rsid){
		leftText.setText(rsid);
	}

	/**
	 * Set the icon to display in the 'home' section of the action bar. The action bar will use an icon specified by its style or the activity icon by default.
	 * 
	 * Whether the home section shows an icon or logo is controlled by the display option {@link #DISPLAY_USE_LOGO}.
	 * 
	 * @param icon
	 *          Drawable to show as an icon.
	 * 
	 * @see #setDisplayUseLogoEnabled(boolean)
	 * @see #setDisplayShowHomeEnabled(boolean)
	 */
	public void setIcon( Drawable icon ){
		if (mNullBar) return;

		leftIcon.setImageDrawable(icon);
	}

	/**
	 * Set the logo to display in the 'home' section of the action bar. The action bar will use a logo specified by its style or the activity logo by default.
	 * 
	 * Whether the home section shows an icon or logo is controlled by the display option {@link #DISPLAY_USE_LOGO}.
	 * 
	 * @param resId
	 *          Resource ID of a drawable to show as a logo.
	 * 
	 * @see #setDisplayUseLogoEnabled(boolean)
	 * @see #setDisplayShowHomeEnabled(boolean)
	 */
	public void setLogo( int resId ){
		if (mNullBar) return;

		leftIcon.setImageResource(resId);
	}

	/**
	 * Set the logo to display in the 'home' section of the action bar. The action bar will use a logo specified by its style or the activity logo by default.
	 * 
	 * Whether the home section shows an icon or logo is controlled by the display option {@link #DISPLAY_USE_LOGO}.
	 * 
	 * @param logo
	 *          Drawable to show as a logo.
	 * 
	 * @see #setDisplayUseLogoEnabled(boolean)
	 * @see #setDisplayShowHomeEnabled(boolean)
	 */
	public void setLogo( Drawable logo ){
		if (mNullBar) return;

		leftIcon.setImageDrawable(logo);
	}

	/**
	 * Get the position of the selected navigation item in list or tabbed navigation modes.
	 * 
	 * @return Position of the selected item.
	 */
	/*public int getSelectedNavigationIndex(){
		if (mNullBar) return 0;

		return mActionBar.getSelectedNavigationIndex();
	}

	*//**
	 * Get the number of navigation items present in the current navigation mode.
	 * 
	 * @return Number of navigation items.
	 *//*
	public int getNavigationItemCount(){
		if (mNullBar) return 0;

		return mActionBar.getNavigationItemCount();
	}*/

	/**
	 * Set the action bar's title. This will only be displayed if {@link #DISPLAY_SHOW_TITLE} is set.
	 * 
	 * @param title
	 *          Title to set
	 * 
	 * @see #setTitle(int)
	 * @see #setDisplayOptions(int, int)
	 */
	public void setTitle( CharSequence title ){
		if (mNullBar) return;

		centerTitle.setText(title);
	}

	/**
	 * Set the action bar's title. This will only be displayed if {@link #DISPLAY_SHOW_TITLE} is set.
	 * 
	 * @param resId
	 *          Resource ID of title string to set
	 * 
	 * @see #setTitle(CharSequence)
	 * @see #setDisplayOptions(int, int)
	 */
	public void setTitle( int resId ){
		if (mNullBar) return;

		centerTitle.setText(resId);
	}
	public void setRightIcon( Drawable drawable ){
		if (mNullBar) return;

		rightIcon.setImageDrawable(drawable);
	}

	public void setRightIcon( int resId ){
		if (mNullBar) return;

		rightIcon.setImageResource(resId);
	}
	public void setRightIconText( CharSequence title ){
		if (mNullBar) return;

		rightText.setText(title);
	}
	
	public void setRightGrayIconText( CharSequence title, int color ){
		if (mNullBar) return;
		rightText.setTextColor(color);
		rightText.setText(title);
	}

	public void setRightIconText( int resId ){
		if (mNullBar) return;

		rightText.setText(resId);
	}
	public LinearLayout getRightMenuCustomLayout(){
		return this.rightMenuCustomLayout;
	}
	/**
	 * Set the action bar's subtitle. This will only be displayed if {@link #DISPLAY_SHOW_TITLE} is set. Set to null to disable the subtitle entirely.
	 * 
	 * @param subtitle
	 *          Subtitle to set
	 * 
	 * @see #setSubtitle(int)
	 * @see #setDisplayOptions(int, int)
	 *//*
	public void setSubtitle( CharSequence subtitle ){
		if (mNullBar) return;

		mActionBar.setSubtitle(subtitle);
	}

	*//**
	 * Set the action bar's subtitle. This will only be displayed if {@link #DISPLAY_SHOW_TITLE} is set.
	 * 
	 * @param resId
	 *          Resource ID of subtitle string to set
	 * 
	 * @see #setSubtitle(CharSequence)
	 * @see #setDisplayOptions(int, int)
	 *//*
	public void setSubtitle( int resId ){
		if (mNullBar) return;

		mActionBar.setSubtitle(resId);
	}*/


	/**
	 * Set whether to include the application home affordance in the action bar. Home is presented as either an activity icon or logo.
	 * 
	 * <p>
	 * To set several display options at once, see the setDisplayOptions methods.
	 * 
	 * @param showHome
	 *          true to show home, false otherwise.
	 * 
	 * @see #setDisplayOptions(int)
	 * @see #setDisplayOptions(int, int)
	 */
	public void setDisplayIconEnabled( boolean showHome ){
		if (mNullBar) return;

		if(showHome){
			leftIcon.setVisibility(View.VISIBLE);
		}else{
			leftIcon.setVisibility(View.GONE);
		}
	}
	public void setDisplayIconTextEnabled( boolean showHome ){
		if (mNullBar) return;
		if(showHome){
			leftText.setVisibility(View.VISIBLE);
		}else{
			leftText.setVisibility(View.GONE);
		}
	}
	public void setDisplayShowMenuEnabled( boolean showMenu ){
		if (mNullBar) return;
		if(showMenu){
			rightIcon.setVisibility(View.VISIBLE);
		}else{
			rightIcon.setVisibility(View.GONE);
			
		}
	}
	public void setDisplayShowCustomMenuEnable( boolean showMenu ){
		if (mNullBar) return;
		if(showMenu){
			rightMenuCustomLayout.setVisibility(View.VISIBLE);
		}else{
			rightMenuCustomLayout.setVisibility(View.GONE);
			
		}
	}
	public void setDisplayShowMenuTextEnabled( boolean showMenu ){
		if (mNullBar) return;
		if(showMenu){
			rightText.setVisibility(View.VISIBLE);
		}else{
			rightText.setVisibility(View.GONE);
			
		}
	}

	/**
	 * Set whether home should be displayed as an "up" affordance. Set this to true if selecting "home" returns up by a single level in your UI rather than back
	 * to the top level or front page.
	 * 
	 * <p>
	 * To set several display options at once, see the setDisplayOptions methods.
	 * 
	 * @param showHomeAsUp
	 *          true to show the user that selecting home will return one level up rather than to the top level of the app.
	 * 
	 * @see #setDisplayOptions(int)
	 * @see #setDisplayOptions(int, int)
	 */
	public void setDisplayHomeAsUpEnabled( boolean showHomeAsUp ){
		if (mNullBar) return;
		if(showHomeAsUp){
			setIcon(R.drawable.icon_back);
			setIconText(mContext.getString(R.string.nav_home_title));
			setLeftIconClickListener(new OnLeftIconClickListener() {
				
				@Override
				public void onClick(View v) {
					((Activity)mContext).finish();
				}
			});
		}
	}

	/**
	 * Set whether an activity title/subtitle should be displayed.
	 * 
	 * <p>
	 * To set several display options at once, see the setDisplayOptions methods.
	 * 
	 * @param showTitle
	 *          true to display a title/subtitle if present.
	 * 
	 * @see #setDisplayOptions(int)
	 * @see #setDisplayOptions(int, int)
	 */
	public void setDisplayShowTitleEnabled( boolean showTitle ){
		if (mNullBar) return;
		if(showTitle){
			centerTitle.setVisibility(View.VISIBLE);
		}else{
			centerTitle.setVisibility(View.INVISIBLE);
		}
	}


	/**
	 * Set the ActionBar's background. This will be used for the primary action bar.
	 * 
	 * @param d
	 *          Background drawable
	 * @see #setStackedBackgroundDrawable(Drawable)
	 * @see #setSplitBackgroundDrawable(Drawable)
	 */
	@SuppressLint("NewApi")
	public void setBackgroundDrawable( Drawable d ){
		if (mNullBar) return;
		if(Build.VERSION.SDK_INT >= 16)
			customView.setBackground(d);
		else{
			customView.setBackgroundDrawable(d);
		}
	}


	/**
	 * @return The current custom view.
	 */
	public View getCustomView(){
		if (mNullBar) return null;

		return customView;
	}

	/**
	 * Returns the current ActionBar title in standard mode. Returns null if {@link #getNavigationMode()} would not return {@link #NAVIGATION_MODE_STANDARD}.
	 * 
	 * @return The current ActionBar title or null.
	 */
	public CharSequence getTitle(){
		if (mNullBar) return null;

		return centerTitle.getText();
	}

	public void show(){
		if (mNullBar) return;

		mActionBar.show();
	}

	/**
	 * Hide the ActionBar if it is currently showing. If the window hosting the ActionBar does not have the feature {@link Window#FEATURE_ACTION_BAR_OVERLAY} it
	 * will resize application content to fit the new space available.
	 */
	public void hide(){
		if (mNullBar) return;

		mActionBar.hide();
	}

	/**
	 * @return <code>true</code> if the ActionBar is showing, <code>false</code> otherwise.
	 */
	public boolean isShowing(){
		if (mNullBar) return false;

		return mActionBar.isShowing();
	}

	/**
	 * Add a listener that will respond to menu visibility change events.
	 * 
	 * @param listener
	 *          The new listener to add
	 */
	public void addOnMenuVisibilityListener( OnMenuVisibilityListener listener ){
		if (mNullBar) return;

		mActionBar.addOnMenuVisibilityListener(listener);
	}

	/**
	 * Remove a menu visibility listener. This listener will no longer receive menu visibility change events.
	 * 
	 * @param listener
	 *          A listener to remove that was previously added
	 */
	public void removeOnMenuVisibilityListener( OnMenuVisibilityListener listener ){
		if (mNullBar) return;

		mActionBar.removeOnMenuVisibilityListener(listener);
	}

	/**
	 * Enable or disable the "home" button in the corner of the action bar. (Note that this is the application home/up affordance on the action bar, not the
	 * systemwide home button.)
	 * 
	 * <p>
	 * This defaults to true for packages targeting &lt{ } API 14. For packages targeting API 14 or greater, the application should call this method to enable
	 * interaction with the home/up affordance.
	 * 
	 * <p>
	 * Setting the {@link #DISPLAY_HOME_AS_UP} display option will automatically enable the home button.
	 * 
	 * @param enabled
	 *          true to enable the home button, false to disable the home button.
	 */
	/*public void setHomeButtonEnabled( boolean enabled ){
		if (mNullBar) return;

		mActionBar.setHomeButtonEnabled(enabled);
	}*/

	/**
	 * Returns a {@link Context} with an appropriate theme for creating views that will appear in the action bar. If you are inflating or instantiating custom
	 * views that will appear in an action bar, you should use the Context returned by this method. (This includes adapters used for list navigation mode.) This
	 * will ensure that views contrast properly against the action bar.
	 * 
	 * @return A themed Context for creating views
	 */
	 
	/*public Context getThemedContext(){
		if (mNullBar) return mContext;

		return mActionBar.getThemedContext();
	}*/
	public OnLeftIconClickListener getLeftIconClickListener() {
		return leftIconClickListener;
	}

	public void setLeftIconClickListener(
			OnLeftIconClickListener leftIconClickListener) {
		this.leftIconClickListener = leftIconClickListener;
	}

	public OnRightMenuClickListener getRightMenuClickListener() {
		return rightMenuClickListener;
	}

	public void setRightMenuClickListener(
			OnRightMenuClickListener rightMenuClickListener) {
		this.rightMenuClickListener = rightMenuClickListener;
	}
	public interface OnLeftIconClickListener{
		public void onClick(View v);
	}
	public interface OnRightMenuClickListener{
		public void onClick(View v);
	}
	public int getHeight(){
		return mActionBar.getHeight();
	}
}
