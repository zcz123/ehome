package cc.wulian.smarthomev5.fragment.singin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;

/**
 * Created by yuxiaoxuan on 2017/3/3.
 * 用于Activity或Fragment处理扫码结果
 */

public interface IQRScanHandlerResult {

    void handleDecode(Result rawResult, Bitmap barcode);
    void setResult(int result, Intent data);
    void drawViewfinder();
    Handler getHandler();
}
