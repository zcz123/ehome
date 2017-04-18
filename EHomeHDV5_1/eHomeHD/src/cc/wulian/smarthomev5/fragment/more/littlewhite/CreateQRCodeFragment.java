package cc.wulian.smarthomev5.fragment.more.littlewhite;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lidroid.xutils.ViewUtils;

import org.json.JSONObject;

import java.util.Hashtable;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;

/**
 * function:生成小白二维码
 * Created by hxc on 2016/11/16.
 */

public class CreateQRCodeFragment extends WulianFragment {
    public String json;
    public static final String JSON = "json";
    private ImageView iv_qr_code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_qr_code,
                container, false);
        ViewUtils.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initViews();
        initBar();
    }

    private void initData() {
        json = getActivity().getIntent().getStringExtra("json");
    }

    private void initViews() {
        iv_qr_code = (ImageView) getView().findViewById(R.id.iv_qr_code);
        iv_qr_code.setImageBitmap(createQRCodeImage(json, 250, 250, null));
    }
    private void initBar() {
        // this.mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setDisplayShowMenuTextEnabled(false);
        getSupportActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getSupportActionBar().setTitle(
                getResources().getString(R.string.gateway_explore_scanning_title));
        getSupportActionBar().setLeftIconClickListener(
                new ActionBarCompat.OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }
                });
    }

    private Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        Bitmap result = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        return result;
    }

    public Bitmap createQRCodeImage(String text, int w, int h, Bitmap logo) {
        try {
            Bitmap scaleLogo = getScaleLogo(logo, w, h);
            int offsetX = 0;
            int offsetY = 0;
            if (scaleLogo != null) {
                offsetX = (w - scaleLogo.getWidth()) / 2;
                offsetY = (h - scaleLogo.getHeight()) / 2;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    //判断是否在logo图片中
                    if (offsetX != 0 && offsetY != 0 && x >= offsetX && x < offsetX + scaleLogo.getWidth() && y >= offsetY && y < offsetY + scaleLogo.getHeight()) {
                        int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                        //如果logo像素是透明则写入二维码信息
                        if (pixel == 0) {
                            if (bitMatrix.get(x, y)) {
                                pixel = 0xff000000;
                            } else {
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * w + x] = pixel;

                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = 0xff000000;
                        } else {
                            pixels[y * w + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
