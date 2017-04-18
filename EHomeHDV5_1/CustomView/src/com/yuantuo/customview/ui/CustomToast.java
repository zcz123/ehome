package com.yuantuo.customview.ui;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yuantuo.customview.R;

public class CustomToast
{
	public static final int LENGTH_SHORT = 0;
	public static final int LENGTH_LONG = 1;

	private static void makeToast( Context context, int imageId, String text, int showLength )
	{
		Toast toast = new Toast(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layoutView = inflater.inflate(R.layout.for_toast, null);

		if (imageId != 0)
		{
			((ImageView) layoutView.findViewById(R.id.imageView_toast)).setImageResource(imageId);
		}
		((TextView) layoutView.findViewById(R.id.textView_toast)).setText(text);
		toast.setView(layoutView);
		toast.setDuration(showLength);
		toast.show();
	}

	public static void showToast( Context context, String text, int showLength, boolean isInOtherThread )
	{
		showToast(context, 0, text, showLength, isInOtherThread);
	}

	public static void showToast( final Context context, final int imageId, final String text, final int showLenth, boolean isInOtherThread )
	{
		if (isInOtherThread)
		{
			Handler handler = new Handler(context.getMainLooper());
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					makeToast(context, imageId, text, showLenth);
				}
			});
		}
		else
		{
			makeToast(context, imageId, text, showLenth);
		}
	}
}
