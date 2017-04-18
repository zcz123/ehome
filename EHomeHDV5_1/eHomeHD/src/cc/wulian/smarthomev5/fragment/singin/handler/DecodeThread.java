/*
 * Copyright (C) 2008 ZXing authors
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

package cc.wulian.smarthomev5.fragment.singin.handler;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.barcode.decode.CaptureActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import cc.wulian.smarthomev5.fragment.singin.IQRScanHandlerResult;
import cc.wulian.smarthomev5.fragment.singin.QRScanFragmentV5;

/**
 * This thread does all the heavy lifting of decoding the images.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";
  public static final String BARCODE_SCALED_FACTOR = "barcode_scaled_factor";

  private final IQRScanHandlerResult scanHandlerResult;
  private final Hashtable<DecodeHintType,Object> hints;
  private Handler handler;
  private final CountDownLatch handlerInitLatch;
 public DecodeThread(IQRScanHandlerResult scanHandlerResult, Vector<BarcodeFormat> decodeFormats,
               String characterSet) {
    this.scanHandlerResult = scanHandlerResult;
    handlerInitLatch = new CountDownLatch(1);
    hints = new Hashtable<DecodeHintType, Object>(1);
    if (decodeFormats == null || decodeFormats.isEmpty()) {
      decodeFormats = new Vector<BarcodeFormat>();
        decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
    }
    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
    }
  }
 public DecodeThread(IQRScanHandlerResult scanHandlerResult,
               Collection<BarcodeFormat> decodeFormats,
               Hashtable<DecodeHintType,?> baseHints,
               String characterSet,
               ResultPointCallback resultPointCallback) {

    this.scanHandlerResult = scanHandlerResult;
    handlerInitLatch = new CountDownLatch(1);

    hints = new Hashtable<>();
    if (baseHints != null) {
      hints.putAll(baseHints);
    }

    // The prefs can't change while the thread is running, so pick them up once here.
    if (decodeFormats == null || decodeFormats.isEmpty()) {
//      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
      decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
//      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_PRODUCT, true)) {
//        decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
//      }
//      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_1D_INDUSTRIAL, true)) {
//        decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
//      }
//      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_QR, true)) {
//        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
//      }
//      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_DATA_MATRIX, true)) {
//        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
//      }
//      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_AZTEC, false)) {
//        decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
//      }
//      if (prefs.getBoolean(PreferencesActivity.KEY_DECODE_PDF417, false)) {
//        decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
//      }
      decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
      decodeFormats.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
      decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
      decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
      decodeFormats.addAll(DecodeFormatManager.AZTEC_FORMATS);
      decodeFormats.addAll(DecodeFormatManager.PDF417_FORMATS);
    }
    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);
    }
    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
    Log.i("DecodeThread", "Hints: " + hints);
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();
    handler = new DecodeHandler(scanHandlerResult, hints);
    handlerInitLatch.countDown();
    Looper.loop();
  }

}
