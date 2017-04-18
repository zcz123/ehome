package cc.wulian.smarthomev5.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class StateDrawableFactory
{
	private static final int[] FOUCESED = {android.R.attr.state_focused};
	private static final int[] FOUCESED_PRESSED = {android.R.attr.state_focused, android.R.attr.state_pressed};
	private static final int[] PRESSED = {android.R.attr.state_pressed};
	private static final int[] SELECTED = {android.R.attr.state_selected};
	private static final int[] CHECKED = {android.R.attr.state_checked};
	private static final int[] NORMAL = {};

	public static class Builder
	{
		Context mContext;
		Resources mResources;

		Drawable foucesed;
		Drawable foucesed_Pressed;
		Drawable pressed;
		Drawable selected;
		Drawable checked;
		Drawable normal;

		public Builder( Context mContext )
		{
			this.mContext = mContext;
			mResources = mContext.getResources();
		}

		public Builder setFoucesed( int res ){
			setFoucesed(mResources.getDrawable(res));
			return this;
		}

		public Builder setFoucesed( Drawable resDrawable ){
			foucesed = resDrawable;
			return this;
		}

		public Builder setFoucesedPressed( int res ){
			setFoucesedPressed(mResources.getDrawable(res));
			return this;
		}

		public Builder setFoucesedPressed( Drawable resDrawable ){
			foucesed_Pressed = resDrawable;
			return this;
		}

		public Builder setPressed( int res ){
			setPressed(mResources.getDrawable(res));
			return this;
		}

		public Builder setPressed( Drawable resDrawable ){
			pressed = resDrawable;
			return this;
		}

		public Builder setSelected( int res ){
			setSelected(mResources.getDrawable(res));
			return this;
		}

		public Builder setSelected( Drawable resDrawable ){
			selected = resDrawable;
			return this;
		}

		public Builder setChecked( int res ){
			setChecked(mResources.getDrawable(res));
			return this;
		}

		public Builder setChecked( Drawable resDrawable ){
			checked = resDrawable;
			return this;
		}

		public Builder setNormal( int res ){
			setNormal(mResources.getDrawable(res));
			return this;
		}

		public Builder setNormal( Drawable resDrawable ){
			normal = resDrawable;
			return this;
		}

		public StateListDrawable create(){
			StateListDrawable stateDrawable = new StateListDrawable();
			applayAttr(stateDrawable, FOUCESED, foucesed);
			applayAttr(stateDrawable, FOUCESED_PRESSED, foucesed_Pressed);
			applayAttr(stateDrawable, PRESSED, pressed);
			applayAttr(stateDrawable, CHECKED, checked);
			applayAttr(stateDrawable, SELECTED, selected);
			applayAttr(stateDrawable, NORMAL, normal);
			return stateDrawable;
		}

		private void applayAttr( StateListDrawable stateDrawable, int[] attr, Drawable drawable ){
			if (drawable != null){
				stateDrawable.addState(attr, drawable);
			}
		}
	}

	public static Builder makeSimpleStateDrawable( Context context,
			Drawable normalDrawable, Drawable selectedDrawable ){
		StateDrawableFactory.Builder builder = new StateDrawableFactory.Builder(context);
		builder.setNormal(normalDrawable);
		builder.setPressed(selectedDrawable);
		builder.setSelected(selectedDrawable);
		return builder;
	}
}
