package cc.wulian.smarthomev5.fragment.setting;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.view.BadgeView;

public abstract class AbstractSettingItem 
{
	protected View view;
	protected Drawable icon;
	protected String name;
	protected Context mContext;
	protected LayoutInflater inflater;
	protected ImageView iconImageView;
	protected TextView nameTextView;
	protected ToggleButton chooseToggleButton;
	protected BadgeView remindBadgeView;
	protected TextView infoTextView;
	protected ImageView infoImageView;
	protected TextView descriptionTextView;
	protected LinearLayout upLinearLayout;
	protected LinearLayout middleLinearLayout;
	protected LinearLayout downLineLayout;
	protected MainApplication mApp = MainApplication.getApplication();
	protected Preference preference = Preference.getPreferences();
	
	public AbstractSettingItem( Context context, Drawable icon, String name )
	{
		this.mContext = context;
		inflater = LayoutInflater.from(this.mContext);
		this.icon =icon;
		this.name = name;
	}
	public AbstractSettingItem( Context context, int icon, String name )
	{
		this(context,context.getResources().getDrawable(icon),name);
	}
	public AbstractSettingItem( Context context, int icon, int name )
	{
		this(context,context.getResources().getDrawable(icon),context.getResources().getString(name));
	}
	public AbstractSettingItem( Context context, int name )
	{
		this(context,new ColorDrawable(),context.getResources().getString(name));
	}
	public AbstractSettingItem( Context context )
	{
		this(context,new ColorDrawable(),"");
	}
	public void initSystemState(){
		view = inflater.inflate(R.layout.setting_manager_item, null);
		upLinearLayout = (LinearLayout)view.findViewById(R.id.setting_manager_item_up_ll);
		middleLinearLayout = (LinearLayout)view.findViewById(R.id.setting_manager_item_name_ly);
		iconImageView = (ImageView)view.findViewById(R.id.setting_manager_item_name_iv);
		iconImageView.setImageDrawable(this.icon);
		nameTextView = (TextView)view.findViewById(R.id.setting_manager_item_name_tv);
		nameTextView.setText(this.name);
		chooseToggleButton = (ToggleButton)view.findViewById(R.id.setting_manager_item_switch);
		remindBadgeView = (BadgeView)view.findViewById(R.id.setting_manager_item_update_badge);
		infoTextView = (TextView)view.findViewById(R.id.setting_manager_item_info_tv);
		infoImageView = (ImageView)view.findViewById(R.id.setting_manager_item_info_iv);
		downLineLayout = (LinearLayout)view.findViewById(R.id.setting_manager_item_down_ll);
		descriptionTextView = (TextView)view.findViewById(R.id.setting_manager_item_description);
	}
	
	public abstract void doSomethingAboutSystem();
	
	public  View getShowView(){
		return this.view;
	}
	
	
	protected boolean getBoolean( String key, boolean defaultValue ){
		return preference.getBoolean(key, defaultValue);
	}

	protected void putBoolean( String key, boolean value ){
		preference.putBoolean(key, value);
	}

	protected String getString( String key, String defaultValue ){
		return preference.getString(key, defaultValue);
	}

	protected void putString( String key, String newValue ){
		preference.putString(key, newValue);
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Context getmContext() {
		return mContext;
	}
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public LayoutInflater getInflater() {
		return inflater;
	}
	public void setInflater(LayoutInflater inflater) {
		this.inflater = inflater;
	}
	public ImageView getIconImageView() {
		return iconImageView;
	}
	public void setIconImageView(ImageView iconImageView) {
		this.iconImageView = iconImageView;
	}
	public TextView getNameTextView() {
		return nameTextView;
	}
	public void setNameTextView(TextView nameTextView) {
		this.nameTextView = nameTextView;
	}
	public ToggleButton getChooseToggleButton() {
		return chooseToggleButton;
	}
	public void setChooseToggleButton(ToggleButton chooseToggleButton) {
		this.chooseToggleButton = chooseToggleButton;
	}
	public BadgeView getRemindBadgeView() {
		return remindBadgeView;
	}
	public void setRemindBadgeView(BadgeView remindBadgeView) {
		this.remindBadgeView = remindBadgeView;
	}
	public TextView getInfoTextView() {
		return infoTextView;
	}
	public void setInfoTextView(TextView infoTextView) {
		this.infoTextView = infoTextView;
	}
	public ImageView getInfoImageView() {
		return infoImageView;
	}
	public void setInfoImageView(ImageView infoImageView) {
		this.infoImageView = infoImageView;
	}
	public void setInfoTextViewColor(int color){
		this.infoTextView.setTextColor(color);
	}
	public void setViewVisible(int visible){
		view.setVisibility(visible);
	}
}
