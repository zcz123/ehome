package cc.wulian.smarthomev5.utils;

import android.content.Context;
import android.content.Intent;

public class MailUtil
{
	private static final String APPERROR_EMAIL_ADD = "feedback@wulian.mobi";
	private static final String MIME_TYPE = "plain/text";
	
	public static Intent mailTo( Context context, String mailSubject, String mailContent ){
		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		mailIntent.setType(MIME_TYPE);
		String[] arrReceiver = {APPERROR_EMAIL_ADD};
		mailIntent.putExtra(Intent.EXTRA_EMAIL, arrReceiver);
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
		mailIntent.putExtra(Intent.EXTRA_TEXT, mailContent);
		return mailIntent;
	}
}
