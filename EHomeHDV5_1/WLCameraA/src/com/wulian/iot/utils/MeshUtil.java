package com.wulian.iot.utils;

import android.util.Log;
import android.util.Xml;

import com.wulian.iot.bean.CameraEagleUpdateInfo;
import com.wulian.iot.view.device.setting.SetEagleCameraActivity;
import com.wulian.iot.view.manage.FirmwareUpManage;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by syf on 2016/10/12.
 */

public class MeshUtil {
    public static String isToString(InputStream is) {
        ByteArrayOutputStream baop = new ByteArrayOutputStream();
        int b;
        try {
            while ((b = is.read()) != -1) {
                baop.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                baop.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baop.toString();
    }

    public static FirmwareUpManage.CameraUpPojoFromServer xmlParseString(String xmlStr) {
        FirmwareUpManage.CameraUpPojoFromServer cameraUpdateInfo = null;
        ByteArrayInputStream tInputStringStream = null;
        try {
            if (xmlStr != null && !xmlStr.trim().equals("")) {
                tInputStringStream = new ByteArrayInputStream(xmlStr.getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(tInputStringStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        cameraUpdateInfo = new FirmwareUpManage.CameraUpPojoFromServer();
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("versionCode")) {
                            int vCode = Integer.parseInt(parser.nextText());
                            cameraUpdateInfo.setVersionCode(vCode);
                        } else if (name.equalsIgnoreCase("versionName")) {
                            String vName = parser.nextText();
                            cameraUpdateInfo.setVersionName(vName);
                        } else if (name.equalsIgnoreCase("remindTimes")) {
                            int rTime = Integer.parseInt(parser.nextText());
                            cameraUpdateInfo.setRemindTimes(rTime);
                        } else if (name.equalsIgnoreCase("versionTxts")) {
                            String vTxts = parser.nextText();
                            cameraUpdateInfo.setVersionTxts(vTxts);
                        } else if (name.equalsIgnoreCase("url")) {
                            String url = parser.nextText();
                            cameraUpdateInfo.setUrl(url);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
            tInputStringStream.close();
        } catch (XmlPullParserException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return cameraUpdateInfo;
    }

    public static void xmlParseObject(final String url, final SetEagleCameraActivity.ObtainDevVersionByServer obtainDevVersionByServer) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                    if (httpURLConnection.getResponseCode() == 200) {
                        String res = isToString(httpURLConnection.getInputStream());
                        if (res != null) {
                            if(obtainDevVersionByServer!=null){
                                obtainDevVersionByServer.success(getXMLParseString(res));
                            }
                        }
                    }
                } catch (Exception ex) {
                    if(obtainDevVersionByServer!=null){
                        obtainDevVersionByServer.error(ex.fillInStackTrace());
                    }
                }
            }
        }).start();
    }

    private static CameraEagleUpdateInfo getXMLParseString(final String xml) throws XmlPullParserException, IOException {
        CameraEagleUpdateInfo mCameraEagleUpdateInfo = null;
        ByteArrayInputStream tInputStringStream = null;
        if (xml != null && !xml.trim().equals("")) {
            tInputStringStream = new ByteArrayInputStream(xml.getBytes());
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(tInputStringStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mCameraEagleUpdateInfo = new CameraEagleUpdateInfo();
                        break;
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("versionCode")) {
                            int vCode = Integer.parseInt(parser.nextText());
                            mCameraEagleUpdateInfo.setVersionCode(vCode);
                        } else if (name.equalsIgnoreCase("versionName")) {
                            String vName = parser.nextText();
                            mCameraEagleUpdateInfo.setVersionName(vName);
                        } else if (name.equalsIgnoreCase("remindTimes")) {
                            int rTime = Integer.parseInt(parser.nextText());
                            mCameraEagleUpdateInfo.setRemindTimes(rTime);
                        } else if (name.equals("devmodel")) {
                            String mdevmodel = parser.nextText();
                            mCameraEagleUpdateInfo.setDevmodel(mdevmodel);
                        } else if (name.equals("fwmodel")) {
                            String mfwmodel = parser.nextText();
                            mCameraEagleUpdateInfo.setFwmodel(mfwmodel);
                        } else if (name.equalsIgnoreCase("versionTxts")) {
                            String vTxts = parser.nextText();
                            mCameraEagleUpdateInfo.setVersionTxts(vTxts);
                        } else if (name.equalsIgnoreCase("url")) {
                            String url = parser.nextText();
                            mCameraEagleUpdateInfo.setUrl(url);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
            tInputStringStream.close();
        }
        return mCameraEagleUpdateInfo;
    }
}
