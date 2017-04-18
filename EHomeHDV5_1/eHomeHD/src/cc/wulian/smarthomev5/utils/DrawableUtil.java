package cc.wulian.smarthomev5.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by yanzy on 2016-5-30 Copyright wulian group 2008-2016 All rights
 * reserved. http://www.wuliangroup.com
 **/
public class DrawableUtil {
	public final static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		}
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * use to lessen pic 50%
	 * 
	 * @param path
	 *            sd card path
	 * @return bitmap
	 */
	public final static Bitmap lessenUriImage(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = false; // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = (int) (options.outHeight / (float) 320);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be; // 重新读入图片，注意此时已经把 options.inJustDecodeBounds
									// 设回 false 了
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}
	
	//使用Bitmap加Matrix来缩放
    public final static Bitmap resizeImage(Bitmap bitmap, int w, int h) 
    {  
        Bitmap BitmapOrg = bitmap;  
        int width = BitmapOrg.getWidth();  
        int height = BitmapOrg.getHeight();  
 
        float scaleWidth = ((float) w) / width;  
        float scaleHeight = ((float) h) / height;  
 
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);  
        // if you want to rotate the Bitmap   
        // matrix.postRotate(45);   
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,  
                        height, matrix, true);  
        return resizedBitmap;  
    }
}
