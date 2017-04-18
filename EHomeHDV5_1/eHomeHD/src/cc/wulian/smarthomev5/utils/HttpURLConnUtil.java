package cc.wulian.smarthomev5.utils;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class HttpURLConnUtil {
    public static void post(final String url, final String data, final int type, final Handler mHandler, final String token){
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL ur = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) ur.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("token", token);
                    conn.setDoOutput(true); // 设置可输出流
                    OutputStream os = conn.getOutputStream(); // 获取输出流
                    os.write(data.getBytes()); // 将数据写给服务器
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String result = Inputstr2Str_Reader(is,""); // 字节流转字符串
                        Message ms= mHandler.obtainMessage();
                        ms.what=type;
                        ms.obj=result;
                        mHandler.sendMessage(ms);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }
    private static String Inputstr2Str_Reader(InputStream in, String encode)
    {

        String str = "";
        try
        {
            if (encode == null || encode.equals(""))
            {
                // 默认以utf-8形式
                encode = "utf-8";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, encode));
            StringBuffer sb = new StringBuffer();

            while ((str = reader.readLine()) != null)
            {
                sb.append(str);
            }
            return sb.toString();
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return str;
    }
}
