package cc.wulian.smarthomev5.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.more.littlewhite.CreateQRCodeFragment;
import cc.wulian.smarthomev5.fragment.more.littlewhite.LittleWhiteFragment;

/**
 * Created by Administrator on 2016/11/16.
 */

public class CreateQRCodeActivity extends EventBusActivity {
    public  String json;
    public static final String JSON = "json";
    private ImageView iv_qr_code;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, new CreateQRCodeFragment())
                    .commit();
        }
    }
}
