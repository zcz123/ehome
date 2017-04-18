/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.camera;

import com.google.zxing.LuminanceSource;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * This object extends LuminanceSource around an array of YUV data returned from the camera driver,
 * with the option to crop to a rectangle within the full data. This can be used to exclude
 * superfluous pixels around the perimeter and speed up decoding.
 *
 * It works for any pixel format where the Y channel is planar and appears first, including
 * YCbCr_420_SP and YCbCr_422_SP.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class PlanarYUVLuminanceSource extends LuminanceSource {
  private final byte[] yuvData;
  private final int dataWidth;
  private final int dataHeight;
  private final int left;
  private final int top;
  private String TAG=PlanarYUVLuminanceSource.class.getName();
  public PlanarYUVLuminanceSource(byte[] yuvData, int dataWidth, int dataHeight, int left, int top,
      int width, int height) {
    super(width, height);
//    if(CameraManager.get().isLandscape()){
//      if (left + width > dataWidth || top + height > dataHeight) {
//        throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
//      }
//    }else{
//      if (left + width > dataHeight || top + height > dataWidth) {
//        throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
//      }
//    }
    if (left + width > dataWidth || top + height > dataHeight) {
      throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
    }

    this.yuvData = yuvData;
    this.dataWidth = dataWidth;
    this.dataHeight = dataHeight;
    this.left = left;
    this.top = top;
  }
  private void reverseHorizontal(int width, int height) {
    byte[] yuvData = this.yuvData;
    for (int y = 0, rowStart = top * dataWidth + left; y < height; y++, rowStart += dataWidth) {
      int middle = rowStart + width / 2;
      for (int x1 = rowStart, x2 = rowStart + width - 1; x1 < middle; x1++, x2--) {
        byte temp = yuvData[x1];
        yuvData[x1] = yuvData[x2];
        yuvData[x2] = temp;
      }
    }
  }
  @Override
  public byte[] getRow(int y, byte[] row) {
    if (y < 0 || y >= getHeight()) {
      throw new IllegalArgumentException("Requested row is outside the image: " + y);
    }
    int width = getWidth();
    if (row == null || row.length < width) {
      row = new byte[width];
    }
    /*int offset=0;
    if(CameraManager.get().isLandscape()){
      offset = (y + top) * dataWidth + left;
    }else {
      offset = (y + top) * dataHeight + left;
    }*/
    int offset = (y + top) * dataWidth + left;
    try{
      Log.d(TAG, "getRow: yuvData.len="+yuvData.length+" offset="+offset+" row.len"+row.length+" width="+width);
      System.arraycopy(yuvData, offset, row, 0, width);
    }catch (java.lang.ArrayIndexOutOfBoundsException ex){
      throw  ex;
    }
    return row;
  }

  @Override
  public byte[] getMatrix() {
    int width = getWidth();
    int height = getHeight();
    Log.d(TAG, "getMatrix: width="+width+" height="+height);
    // If the caller asks for the entire underlying image, save the copy and give them the
    // original data. The docs specifically warn that result.length must be ignored.
    if (width == dataWidth && height == dataHeight) {
      return yuvData;
    }

    int area = width * height;
    byte[] matrix = new byte[area];
//    int inputOffset=0;
//    if(CameraManager.get().isLandscape()){
//      inputOffset = top * dataWidth + left;
//    }
//    else{
//      inputOffset = top * dataHeight + left;
//    }
    int inputOffset = top * dataWidth + left;
    // If the width matches the full width of the underlying data, perform a single copy.
    if (width == dataWidth) {
      System.arraycopy(yuvData, inputOffset, matrix, 0, area);
      return matrix;
    }

    // Otherwise copy one cropped row at a time.
    byte[] yuv = yuvData;
    for (int y = 0; y < height; y++) {
      int outputOffset = y * width;
      try {
        System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
        inputOffset += dataWidth;
      }catch (java.lang.ArrayIndexOutOfBoundsException ex){
//        Log.e(TAG, "getMatrix: ",ex );
        throw ex;
      }

//      if(CameraManager.get().isLandscape()){
//        inputOffset += dataWidth;
//      }else {
//        inputOffset += dataHeight;
//      }


    }
    return matrix;
  }

  @Override
  public boolean isCropSupported() {
    return true;
  }

  public int getDataWidth() {
    return dataWidth;
  }

  public int getDataHeight() {
    return dataHeight;
  }

  public Bitmap renderCroppedGreyscaleBitmap() {
    int width = getWidth();
    int height = getHeight();
    int[] pixels = new int[width * height];
    byte[] yuv = yuvData;
    /*int inputOffset=0;
    if(CameraManager.get().isLandscape()){
      inputOffset = top * dataWidth + left;
    }else {
      inputOffset = top * dataHeight + left;
    }*/

    int inputOffset = top * dataWidth + left;
    for (int y = 0; y < height; y++) {
      int outputOffset = y * width;
      for (int x = 0; x < width; x++) {
        int grey = yuv[inputOffset + x] & 0xff;
        pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
      }
      /*if(CameraManager.get().isLandscape()){
        inputOffset += dataWidth;
      }else{
        inputOffset += dataHeight;
      }*/
      inputOffset += dataWidth;
    }

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
    return bitmap;
  }
}
