/**
 * Project Name:  iCam
 * File Name:     BarcodeUtils.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2015年7月10日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.icam.utils;

import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wulian.siplibrary.utils.WulianLog;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * @ClassName: BarcodeUtils
 * @Function:  二维码操作工具类
 * @Date:      2015年7月10日
 * @author:    yuanjs
 * @email:     yuanjsh@wuliangroup.cn
 */
public class BarcodeUtils {
	/**
	 * @MethodName:  create2DCoderBitmap
	 * @Function:    生成一个二维码图像 
	 * @author:      yuanjs
	 * @date:        2015年7月10日
	 * @email:       yuanjsh@wuliangroup.cn
	 * @param url    传入的字符串，通常是一个URL
	 * @param QR_WIDTH 宽度（像素值px） 
	 * @param QR_HEIGHT 高度（像素值px） 
	 * @return
	 */
	public static final Bitmap create2DCoderBitmap(String url, int QR_WIDTH,  
            int QR_HEIGHT,Bitmap logoBm)throws WriterException {  
            // 判断URL合法性  
            if (url == null || "".equals(url) || url.length() < 1) {  
                return null;  
            }  
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
            // 图像数据转换，使用了矩阵转换  
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,  
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);  
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];  
            // 下面这里按照二维码的算法，逐个生成二维码的图片，  
            // 两个for循环是图片横列扫描的结果  
            for (int y = 0; y < QR_HEIGHT; y++) {  
                for (int x = 0; x < QR_WIDTH; x++) {  
                    if (bitMatrix.get(x, y)) {  
                        pixels[y * QR_WIDTH + x] = 0xff000000;  
                    } else {  
                        pixels[y * QR_WIDTH + x] = 0xffffffff;  
                    }  
                }  
            }  
            // 生成二维码图片的格式，使用ARGB_8888  
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,  
                    Bitmap.Config.ARGB_8888);  
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);  
            // 显示到一个ImageView上面  
            // sweepIV.setImageBitmap(bitmap); 
//            if (logoBm != null) { 
//                bitmap = addLogo(bitmap, logoBm); 
//            }  
            return bitmap;  
    } 
	private static final int BLACK = 0xff000000;  
	/**
	 * @MethodName:  createQRCode
	 * @Function:    生成一个二维码图像
	 * @author:      yuanjs
	 * @date:        2015年7月10日
	 * @email:       yuanjsh@wuliangroup.cn
	 * @param str    传入的字符串，通常是一个URL 
	 * @param widthAndHeight 图像的宽高 
	 * @return
	 * @throws WriterException
	 */
	public static Bitmap createQRCode(String str, int widthAndHeight,Bitmap logoBm)  
            throws WriterException {  
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
        hints.put(EncodeHintType.ERROR_CORRECTION, String.valueOf(ErrorCorrectionLevel.H));
        BitMatrix matrix = new MultiFormatWriter().encode(str,  
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        int[] pixels = new int[width * height];  
  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if (matrix.get(x, y)) {  
                    pixels[y * width + x] = BLACK;  
                }  
            }  
        }  
        Bitmap bitmap = Bitmap.createBitmap(width, height,  
                Bitmap.Config.ARGB_8888);  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//        if (logoBm != null) { 
//            bitmap = addLogo(bitmap, logoBm); 
//        }  
        return bitmap;  
    }  
	/**
	 * @MethodName:  addLogo
	 * @Function:    在二维码中间添加Logo图案
	 * @author:      yuanjs
	 * @date:        2015年7月10日
	 * @email:       yuanjsh@wuliangroup.cn
	 * @param src
	 * @param logo
	 * @return
	 */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) { 
        if (src == null) { 
            return null; 
        } 
   
        if (logo == null) { 
            return src; 
        } 
   
        //获取图片的宽高 
        int srcWidth = src.getWidth(); 
        int srcHeight = src.getHeight(); 
        int logoWidth = logo.getWidth(); 
        int logoHeight = logo.getHeight(); 
   
        if (srcWidth == 0 || srcHeight == 0) { 
            return null; 
        } 
   
        if (logoWidth == 0 || logoHeight == 0) { 
            return src; 
        } 
   
        //logo大小为二维码整体大小的1/5 
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth; 
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888); 
        try { 
            Canvas canvas = new Canvas(bitmap); 
            canvas.drawBitmap(src, 0, 0, null); 
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2); 
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null); 
            canvas.save(Canvas.ALL_SAVE_FLAG); 
            canvas.restore(); 
        } catch (Exception e) { 
            bitmap = null; 
            e.getStackTrace(); 
        } 
   
        return bitmap; 
    }  
}

