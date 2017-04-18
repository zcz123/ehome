package cc.wulian.smarthomev5.tools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.yuantuo.customview.ui.WLDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import cc.wulian.ihome.wan.util.TaskExecutor;

/**
 * Created by Administrator on 2016/9/6.
 */
public class SigninManager {

    private Activity mActivity;

    private WLDialog.Builder builder;
    private WLDialog dialog;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public SigninManager(Activity activity) {
        mActivity=activity;
    }

    /**
     * 生成二维码图片
     * @param text 生成的二维码信息字符串
     * @param w    二维码图片宽度
     * @param h    二维码图片高度
     * @param logo Bitmap类型的logo
     * @return
     */
    public  Bitmap createQRCodeImage(String text, int w, int h, Bitmap logo) {
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

    /**
     * 缩放logo到二维码的1/5
     *
     * @param logo logo图片
     * @param w
     * @param h
     * @return
     */
    private  Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        Bitmap result = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        return result;
    }

    /**
     * 从服务器获得扫描登录的网关和密码，并进行登录
     * @param readUrl 获取扫描登录网关和密码的服务器Url地址
     */
    public  void loginFromServer(final String readUrl, final LoginFromServerListenner loginFromServerListenner) {

        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                HttpClient httpCient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(readUrl);
                HttpResponse httpResponse = null;
                try {
                    //设置连接超时时间
//                    httpCient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
//                    //设置获取超时时间
                    httpCient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 120000);
                    httpResponse = httpCient.execute(httpGet);
                    HttpEntity entity = httpResponse.getEntity();
                    final String response = EntityUtils.toString(entity,"utf-8");
                    final HttpResponse finalHttpResponse = httpResponse;
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalHttpResponse.getStatusLine().getStatusCode() == 200) {
//                                {"retData":{"info":"50294D4077C2-e10adc3949ba59abbe56e057f20f883e"}}
                                JSONObject jsonObject= JSONObject.parseObject(response);
                                JSONObject retDataJsonObject=jsonObject.getJSONObject("retData");
                                String info=retDataJsonObject.getString("info");
                                String infos[]=info.split("-");
                                if(loginFromServerListenner!=null){
                                    loginFromServerListenner.doSomeThing(infos[0],infos[1]);
                                }
                            }else{
                                Toast.makeText(mActivity,"扫描登录失败"+ finalHttpResponse.getStatusLine().getStatusCode(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    dialog.dismiss();
                } catch (IOException e) {
                    dialog.dismiss();
                    Toast.makeText(mActivity,"扫描登录失败",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 从服务器获取read和write的地址，里面包含rid，由服务器维护
     * @param getUrlListenner 返回的数据的监听
     */
    public  void getUrlString(final QRScodeListenner getUrlListenner) {
        final Map<String,String> urlMap=new HashMap<String,String>();

        TaskExecutor.getInstance().execute(new Runnable() {

            @Override
            public void run() {
                HttpClient httpCient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("https://testdemo.wulian.cc:6009/tv/init");
                HttpResponse httpResponse = null;
                try {
                    //设置连接超时时间
                    httpCient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
                    //设置获取超时时间
                    httpCient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 120000);
                    httpResponse = httpCient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity,"utf-8");
//                      {"retData":{"write":"https://testdemo.wulian.cc:6009/tv/writeIdInfo?&rid=DNGXw8F83X3pHY6e1S17440GK4674mux","read":"https://testdemo.wulian.cc:6009/tv/readIdInfo?&rid=DNGXw8F83X3pHY6e1S17440GK4674mux"}}
                        JSONObject dataObject= JSONObject.parseObject(response);
                        JSONObject retDataObject=dataObject.getJSONObject("retData");
                        String writeUrl=retDataObject.getString("write");
                        String readUrl=retDataObject.getString("read");
                        urlMap.put("write",writeUrl);
                        urlMap.put("read",readUrl);
                        if(getUrlListenner!=null){
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    getUrlListenner.doSomeThing(urlMap);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    dialog.dismiss();
                    Toast.makeText(mActivity,"服务器获取数据失败，请重试",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public interface QRScodeListenner{
        public void doSomeThing(Map<String, String> urlMap);
    }

    public interface LoginFromServerListenner{
        public void doSomeThing(String gwID, String gwPSW);
    }

}
