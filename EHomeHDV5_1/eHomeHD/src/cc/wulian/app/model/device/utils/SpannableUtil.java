package cc.wulian.app.model.device.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

public class SpannableUtil
{
	public static Spannable makeSpannable( CharSequence source, Object span ){
		Spannable spannable = new SpannableString(TextUtils.isEmpty(source) ? "" : source);
		spannable.setSpan(span, 0, spannable.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return spannable;
	}
}
