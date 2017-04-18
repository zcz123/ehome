package com.wulian.iot.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.litesuits.orm.db.utils.DataUtil;
import com.nostra13.universalimageloader.utils.L;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.Packet;
import com.wulian.icam.utils.StringUtil;
import com.wulian.iot.Config;
import com.wulian.iot.bean.EagleWifiListEntiy;
import com.wulian.iot.bean.PresettingModel;
import com.wulian.iot.bean.VideotapeInfo;
import com.wulian.icam.R;
import com.wulian.iot.view.device.setting.SetEagleCameraActivity;
import com.wulian.iot.view.device.setting.SetProtectActivity;
import com.wulian.iot.view.ui.DeskMoveDetectionActivity;
import com.yuantuo.customview.ui.WLToast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.DhcpInfo;
import android.net.ParseException;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class IotUtil {
    private final static String TAG = "IotUtil";

    // add syf
    public static String longToString(long currentTime, String formatType) throws ParseException {
        Date date = null;
        try {
            date = longToDate(currentTime, formatType);
        } catch (Exception e) {
            e.printStackTrace();
        } // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    public static Date longToDate(long currentTime, String formatType)
            throws Exception {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException, Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static String getSnapshotPath() {
        String folder = Environment.getExternalStorageDirectory()
                + "/wulian/camera";
        isFolderExists(folder);
        return folder;
    }

    public static String getVideoRecodertPath(String tag, int type) {
        String folder = null;
        switch (type) {
            case 0:
                folder = Environment.getExternalStorageDirectory()
                        + "/wulian/video" + "/" + tag;
                break;
            case 1:
                folder = Environment.getExternalStorageDirectory()
                        + "/wulian/eagle/video" + "/" + tag;
                break;
        }
        isFolderExists(folder);
        return folder;
    }

    public static String getVersionPath() {
        String folder = Environment.getExternalStorageDirectory()
                + "/wulian/camera/version/";
        isFolderExists(folder);
        return folder;
    }

    public static String getPresetInfoPath(String tag) {
        String folder = null;
        if (tag != null && !tag.trim().equals(" ")) {
            folder = Environment.getExternalStorageDirectory()
                    + "/wulian/camera/preset" + "/" + tag;
            isFolderExists(folder);
        }
        return folder;
    }

    /**
     * add syf 鹰眼存储图片路径
     */
    public static String getSnapshotEaglePath(String tag) {
        String folder = Environment.getExternalStorageDirectory()
                + "/wulian/eagle/snapshot" + "/" + tag + "/" + longToString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss").substring(0, 10);
        isFolderExists(folder);
        return folder;
    }

    private static boolean isFolderExists(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static boolean saveBitmapToJpeg(Bitmap bm, String folder) {
        String fileName = DateUtil.getFormatIMGTime(System.currentTimeMillis())
                + ".jpg";
        return saveBitmapToJpeg(bm, folder, fileName);
    }

    public static boolean saveBitmapToJpeg(Bitmap bm, String folder,
                                           String fileName) {
        boolean result = false;
        if (bm == null)
            return result;
        Log.i("saveBitmapToJpeg", "===folder(" + folder + ")===");
        Log.i("saveBitmapToJpeg", "===fileName(" + fileName + ")===");
        BufferedOutputStream bos = null;
        try {
            File file = new File(folder, fileName);
            Log.i("saveBitmapToJpeg", "===file(" + file.getPath() + ")===");
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            result = true;
        } catch (Exception e) {
            Log.i("saveBitmapToJpeg", "===Exception===");
            Log.i("saveBitmapToJpeg", "===Exception(" + e.getMessage().toString() + ")===");
            e.printStackTrace();
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    Log.i("saveBitmapToJpeg", "===IOException===");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return result;
    }

    public static void saveSnapshot(Context context, Bitmap bitmap, String path) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            WLToast.showToast(context,
                    context.getString(R.string.home_monitor_no_sdcard_hint),
                    WLToast.TOAST_SHORT);
        } else {
            boolean result = saveBitmapToJpeg(bitmap, path);
            if (result) {
                WLToast.showToast(context,
                        context.getString(R.string.smartLock_Snapshot_success),
                        WLToast.TOAST_SHORT);
                try {
                    context.sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                            + path)));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                WLToast.showToast(context,
                        context.getString(R.string.home_monitor_snapshot_fail_hint),
                        WLToast.TOAST_SHORT);
            }
        }
    }

    /**
     * 保存图片
     */
    public static void saveSnapshot(Context context, Bitmap bitmap) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            WLToast.showToast(context,
                    context.getString(R.string.home_monitor_no_sdcard_hint),
                    WLToast.TOAST_SHORT);
        } else {
            String folderPath = getSnapshotPath();
            boolean result = saveBitmapToJpeg(bitmap, folderPath);
            if (result) {
                WLToast.showToast(context,
                        context.getString(R.string.play_take_picture_ok),
                        WLToast.TOAST_SHORT);
                try {
                    context.sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
                            + folderPath)));
                } catch (Exception e) {
                }
            } else {
                WLToast.showToast(context,
                        context.getString(R.string.play_take_picture_exception),
                        WLToast.TOAST_SHORT);
            }
        }
    }

    /**
     * 通过路径删除指定文件
     */
    public static boolean delFilePassWay(String path, String posName) {
        Log.i(TAG, path);
        Log.i(TAG, posName);
        File f = new File(path.trim());
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File obj : files) {
                if (obj.getName().equals(posName)) {
                    return obj.delete();
                }
            }
        }
        return false;
    }

    public static boolean delFilePassWay(String path, int index) {
        File f = new File(path.trim());
        File[] files = f.listFiles();
        for (File obj : files) {
            if (IotUtil.cutOutStringForInt(wipeOutSuffix(obj.getName())) == index) {
                return obj.delete();
            }
        }
        return false;
    }

    public static String wipeOutSuffix(String str) {
        String res = null;
        if (str != null && !str.trim().equals("")) {
            res = str.substring(0, str.lastIndexOf("."));
        }
        return res;
    }

    /**
     * 生成视频文件名称
     */
    public static String getFileName(String tag, int type) {
        return IotUtil.getVideoRecodertPath(tag, type) + File.separator
                + DateUtil.getFormatIMGTime(System.currentTimeMillis())
                + ".mp4";
    }

    public static File[] getFiles(String path) {
        File[] files = null;
        if (path != null && !path.trim().equals("")) {
            File file = new File(path);
            files = file.listFiles();
        }
        return files;
    }

    public static String cutoutFileName(String fileName) {
        return fileName.substring(0, fileName.length() - 1);
    }

    public static int cutOutStringForInt(String name) {
        if (name != null && !name.trim().equals("")) {
            return Integer.valueOf(name.substring(name.length() - 1));
        }
        return -1;
    }

    public static String splice(String name, int index) {
        return name + index;//去除符号
    }

    public static int rotateIndex(int position) {
        return ++position;
    }

    public static int listPosition(int position) {
        return --position;
    }

    /**
     * 获取摄像机内的视频
     */
    @SuppressWarnings("null")
    public static List<VideotapeInfo> getVieos(byte[] data, Context context) {
        List<VideotapeInfo> vieos =
                new ArrayList<>();
        int total_num;        //文件总数
        int cur_num;            //当前文件数
        total_num = DateUtil.bytesToInt(data, 0);
        cur_num = DateUtil.bytesToInt(data, 4);
        if (total_num < cur_num) {
            return new ArrayList<VideotapeInfo>();
        }
        byte[] ipFileName = new byte[36];
        for (int i = 0; i < cur_num; i++) {
            VideotapeInfo vInfo = new VideotapeInfo();
            System.arraycopy(data, 8 + 36 * i, ipFileName, 0, 33);
            String fileName = DateUtil.bytesToStying(ipFileName);
            Log.i(TAG, fileName);
            vInfo.setFileName(fileName);
            vInfo.setVideoType(Config.SERVER_VOIDE);
            vInfo.setBitmap(pathImage(R.drawable.camera_history_default, context));
            vieos.add(vInfo);
        }
        return vieos;
    }

    /**
     * 获取文件夹下 所有数据
     */
    public static List<VideotapeInfo> getVieos(String tag) {
        String finalPath = null;
        VideotapeInfo bean = null;
        List<VideotapeInfo> videos = null;
        finalPath = Config.rootVideo + tag;
        Log.i(TAG, finalPath);
        if (finalPath != null) {
            File file = new File(finalPath);
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                videos = new ArrayList<>();
                for (File obj : files) {
                    bean = new VideotapeInfo();
                    bean.setFileName(obj.getName());
                    bean.setVideoLocation(obj.getPath());
                    bean.setBitmap(getVideoThumbnail(obj.getPath(), 120, 80,
                            MediaStore.Video.Thumbnails.MICRO_KIND));
                    bean.setVideoType(Config.LOCAL_VOIDE);
                    videos.add(bean);
                }
                return videos;
            }
        }
        return new ArrayList<VideotapeInfo>();
    }

    /**
     * 文件 是否存在
     */
    public static boolean isFirmware(String firmware, String fileName) {
        File file = new File(firmware);
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File obj : files) {
                if (obj.getName().equals(fileName)) {//文件存在
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param videoPath 视频路径
     * @param width     图片宽度
     * @param height    图片高度
     * @param kind      eg:MediaStore.Video.Thumbnails.MICRO_KIND MINI_KIND: 512 x
     *                  384，MICRO_KIND: 96 x 96
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width,
                                           int height, int kind) {
        // 获取视频的缩略图
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        // extractThumbnail 方法二次处理,以指定的大小提取居中的图片,获取最终我们想要的图片
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * Bitmap to Drawable
     *
     * @param bitmap
     * @param mcontext
     * @return
     */
    public static Drawable bitmapToDrawble(Bitmap bitmap, Context mcontext) {
        Drawable drawable = new BitmapDrawable(mcontext.getResources(), bitmap);
        return drawable;
    }

    @SuppressLint("NewApi")
    public static void saveBitmap(Bitmap bitmap, Editor editor) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.PNG, 100, baos);
            try {
                editor.putString(Config.SNAPSHOT,
                        Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT))
                        .commit();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //add by guofeng
    @SuppressLint("NewApi")
    public static void saveBitmap(final Bitmap bitmap, final Editor editor, final String stingGw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.PNG, 100, baos);
                    try {
                        editor.putString(stingGw + Config.SNAPSHOT,
                                Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT))
                                .commit();
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 获取预置位 参数
     *
     * @param data
     * @return string[] data
     */
    public static String[] getDeviceSafeProtectSetting(byte[] data) {
        String spMoveAreaTemp = "", spMoveSensitivity, spMoveWeekday, spMoveTime, spMoveArea;
        String[] move = null;
        if (data.length >= 0) {
            String string = DateUtil.Bytes2HexString(data);
            spMoveSensitivity = Integer.toString(DateUtil.bytesToInt(data, 4)); // 获取灵敏度
            if (!Integer.toString(DateUtil.bytesToInt(data, 12)).equals("0")) {
                spMoveAreaTemp = spMoveAreaTemp
                        + Integer.toString(DateUtil.bytesToInt(data, 12) / 6) + ","
                        + Integer.toString((int) (DateUtil.bytesToInt(data, 16) / 4.5)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 20) / 6 + DateUtil.bytesToInt(data, 12) / 6) + ","
                        + Integer.toString((int) (DateUtil.bytesToInt(data, 24) / 4.5) + (int) (DateUtil.bytesToInt(data, 16) / 4.5)) + ";";
            }
            if (!Integer.toString(DateUtil.bytesToInt(data, 32)).equals("0")) {
                spMoveAreaTemp = spMoveAreaTemp
                        + Integer.toString(DateUtil.bytesToInt(data, 32)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 36)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 40)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 44)) + ";";
            }
            if (!Integer.toString(DateUtil.bytesToInt(data, 52)).equals("0")) {
                spMoveAreaTemp = spMoveAreaTemp
                        + Integer.toString(DateUtil.bytesToInt(data, 52)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 56)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 60)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 64)) + ";";
            }
            if (!Integer.toString(DateUtil.bytesToInt(data, 72)).equals("0")) {
                spMoveAreaTemp = spMoveAreaTemp
                        + Integer.toString(DateUtil.bytesToInt(data, 72)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 76)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 80)) + ","
                        + Integer.toString(DateUtil.bytesToInt(data, 84)) + ";";
            }
            if (string.subSequence(184, 186).equals("3E")) {
                spMoveWeekday = "1,2,3,4,5,";
            } else {
                spMoveWeekday = "1,2,3,4,5,6,7,";
            }
            spMoveTime = Integer.valueOf(string.substring(198, 200), 16)
                    .toString()
                    + ","
                    + Integer.valueOf(string.substring(196, 198), 16)
                    .toString()
                    + ","
                    + Integer.valueOf(string.substring(194, 196), 16)
                    .toString()
                    + ","
                    + Integer.valueOf(string.substring(192, 194), 16)
                    .toString();
//			if (spMoveAreaTemp == "") {
//				spMoveAreaTemp = null;
//			}
            spMoveArea = spMoveAreaTemp;
            move = new String[]{spMoveSensitivity, spMoveWeekday, spMoveTime, spMoveArea};
        }
        return move;
    }

    public static DeskMoveDetectionActivity.MotionDetectionPojo assemblyMotion(String[] fences, String moveArea, int switching) {
        String moveSensitivity = fences[0];//灵敏度
        String moveWeekday = fences[1];
        String moveTime = fences[2];
        int sensitivity = 0; // 0 - 4 , 越高越灵敏
        if (moveSensitivity != null && !moveSensitivity.equals("")) {
            sensitivity = Integer.parseInt(moveSensitivity);
        }
        int[] area = new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // 共支持4个区域
        int defenceused = 0; // 是否使用布防时刻表 交给测试人员的软件这个地方修改为0 ，正常情况下为1
        int week = 62; // bit 位表示 bit0-6 分别表示星期日 - 星期六 , 如星期日有效(week｜(1 << 0)) ,
        // 如星期六有效(week｜(1 << 6))
        byte[] moveTimes = new byte[]{0, 0, 0, 0}; // 有效的起、止时间，bit0-7:结束时间的分钟
        // ， bit8-15:结束时间的小时 ，
        // bit16-23:开始时间的分钟，
        // bit24-31:开始时间的小时
        if (moveArea != null) {
            String tempMoveArea = moveArea.replaceAll(";", ",");
            String[] destString = tempMoveArea.split(",");
            for (int i = 0; i < destString.length + 1; i++) {
                if (i == 0) {
                    continue;
                }
                if (!StringUtil.isNullOrEmpty(destString[i - 1])) {
                    area[i] = Integer.parseInt(destString[i - 1]);
                    if (i % 2 != 0) {
                        area[i] = Integer.parseInt(destString[i - 1]) * 6;    //这是X点   修改 mabo
                    } else {
                        area[i] = (int) (Integer.parseInt(destString[i - 1]) * 4.5);    //这是Y点
                    }
                    Log.i("SetProtectActivity", "------------" + area[i]);
                }

            }
            area[3] = area[3] - area[1];//宽
            area[4] = area[4] - area[2];//高
        }
        if (moveWeekday != null) {
            String[] destString = moveWeekday.split(",");
            if (destString.length <= 5) {
                week = 62; // 12345 0011 1110
            } else {
                week = 127; // 1234567 0111 1111
            }
        }
        if (moveTime != null) {
            String[] deStrings = moveTime.split(",");
            for (int i = 0; i < deStrings.length; i++) {
                moveTimes[deStrings.length - i - 1] = (byte) Integer
                        .parseInt(deStrings[i]);
            }
        }

        return new DeskMoveDetectionActivity.MotionDetectionPojo(switching, sensitivity, area, defenceused, week, moveTimes);
    }

    /**
     * @param base64
     * @return bitmap
     */
    public static Bitmap getBitmap(String base64) {
        Bitmap bitmap = null;
        try {
            if (base64 != null && !base64.trim().equals("")) {
                byte[] data = Base64.decode(base64, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            }
        } catch (Exception ex) {
            System.out.println("------>getBitmap" + bitmap);
        }
        return bitmap;
    }

    /**
     * 根据路径获取图片
     */
    public static Bitmap pathImage(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(path);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("------>pathImage" + ex.getMessage());
            return null;
        }
        return bitmap;
    }

    public static Bitmap pathImage(int method, Context context) {
        Bitmap bmp = null;
        Resources res = context.getResources();
        bmp = BitmapFactory.decodeResource(res, method);
        if (bmp == null) {
            return null;
        }
        return bmp;
    }

    /**
     * 发送获取摄像机版本信息 add by likai
     */
    public static void sendCameraVersion(String HOST, int PORT,
                                         String jsonData, final Handler handler) {
        try {
            final Socket clientSocket = new Socket(HOST, PORT);
            clientSocket.setSoTimeout(5000);
            clientSocket.setTcpNoDelay(true);
            clientSocket.setSoLinger(true, 30);
            clientSocket.setSendBufferSize(4096);
            clientSocket.setReceiveBufferSize(4096);
            clientSocket.setKeepAlive(true);
            OutputStream osSend = clientSocket.getOutputStream();
            OutputStreamWriter osWrite = new OutputStreamWriter(osSend);
            BufferedWriter bufWrite = new BufferedWriter(osWrite);
            clientSocket.setOOBInline(true);
            clientSocket.sendUrgentData(0x44);
            bufWrite.write(jsonData);
            bufWrite.flush();

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        while (true) {
                            if (true == clientSocket.isConnected()
                                    && false == clientSocket.isClosed()) {
                                InputStream isRead = clientSocket
                                        .getInputStream();
                                byte[] buffer = new byte[isRead.available()];
                                isRead.read(buffer);
                                String responseInfo = new String(buffer);
                                Bundle bundle = new Bundle();
                                bundle.putString("cameraVersion", responseInfo);
                                Message msg = Message.obtain();
                                msg.what = 2;
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                            clientSocket.close();
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送固件新版本
     */
    public static void sendCameraVersionToDevice(String HOST, int PORT, String file) {
        Socket socket = new Socket();
        FileInputStream reader = null;
        DataOutputStream out = null;
        byte[] buf = null;
        try {
            socket.connect(new InetSocketAddress(HOST, PORT), 1000);
            reader = new FileInputStream(file);
            out = new DataOutputStream(socket.getOutputStream());
            int bufferSize = 20480;
            buf = new byte[bufferSize];
            int read = 0;
            while ((read = reader.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, read);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buf = null;
                out.close();
                reader.close();
                socket.close();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 存储 预置位信息
     *
     * @param move
     * @param editor
     */
    public static void commit(String[] move, Editor editor) {
        editor.putString(Config.MOVE_SENSITIVITY, move[0]);// 灵敏度
        editor.putString(Config.MOVE_WEEKDAY, move[1]);
        editor.putString(Config.MOVE_TIME, move[2]);
        editor.putString(Config.MOVE_AREA, move[3]);// 区域
        editor.commit();
    }

    /**
     * 解析猫眼的 wifi列表
     * 获取wifi名字
     */
    public static List<EagleWifiListEntiy> parseWifiList(byte[] date) {
        List<EagleWifiListEntiy> mList = new ArrayList<EagleWifiListEntiy>();
        EagleWifiListEntiy mEntiy = null;
        byte[] wifiname = new byte[32];
        int a = 36;
        int wifiSum = DateUtil.bytesToInt(date, 0);
        for (int i = 0; i < wifiSum; i++) {
            mEntiy = new EagleWifiListEntiy();
            System.arraycopy(date, (a * i + 4), wifiname, 0, 32);
            try {
                String ab = new String(wifiname, "utf-8");
                mEntiy.setWifiname(ab.trim());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte mode = date[a * i + 4 + 32];
            mEntiy.setMode(mode);
            byte enctype = date[a * i + 4 + 33];
            mEntiy.setEnctype(enctype);
            byte signal = date[a * i + 4 + 34];
            mEntiy.setSignal(signal);
            byte status = date[a * i + 4 + 35];
            mEntiy.setStatus(status);
            mList.add(mEntiy);
        }
        return mList;
    }

    /**
     * 解析猫眼固件信息
     *
     * @param date
     */
    public static SetEagleCameraActivity.IOTCDevInfo parseEagleInfo(byte[] date) {
        SetEagleCameraActivity.IOTCDevInfo iotcDevInfo = new SetEagleCameraActivity.IOTCDevInfo();
        if (date.length <= 0) {
            return iotcDevInfo;
        }
        //date中的所有返回
        char[] model = new char[16];
        char[] vender = new char[16];
        int version;
        int channel;
        int total;
        int free;
        char[] reserved = new char[8];
        //获取设备model
        byte[] modeDte = new byte[16];
        System.arraycopy(date, 0, modeDte, 0, 16);
        model = DateUtil.getChars(modeDte);
        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < model.length; i++) {
            sb1.append(String.valueOf(model[i]));
        }
        if (sb1.toString() != null)
            iotcDevInfo.setModel(sb1.toString());
        //获取设备vendor
        byte[] vendorDate = new byte[16];
        System.arraycopy(date, 16, vendorDate, 0, 16);
        vender = DateUtil.getChars(vendorDate);
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < vender.length; i++) {
            sb2.append(String.valueOf(vender[i]));
        }
        if (sb2.toString() != null)
            iotcDevInfo.setVendor(sb2.toString());
        //获取version
        version = DateUtil.bytesToInt(date, 32);
        if (version != -1)
            iotcDevInfo.setVersion(version);
        //获取channel
        channel = DateUtil.bytesToInt(date, 36);
        if (channel != -1)
            iotcDevInfo.setChannel(channel);
        //获取固件toal
        total = DateUtil.bytesToInt(date, 40);
        if (total != -1)
            iotcDevInfo.setTotal(total);
        free = DateUtil.bytesToInt(date, 44);
        if (free != -1)
            iotcDevInfo.setFree(free);
        byte[] reservedDate = new byte[8];
        System.arraycopy(date, 52, reservedDate, 0, 8);
        reserved = DateUtil.getChars(reservedDate);
        StringBuilder sb4 = new StringBuilder();
        for (int i = 0; i < reserved.length; i++) {
            sb4.append(String.valueOf(reserved[i]));
        }
        if (sb4.toString() != null)
            iotcDevInfo.setRESERVED(sb4.toString());
        return iotcDevInfo;
    }

    public static String getFirmware(String code) {
        return Config.firHead + code + Config.firSuffix;
    }

    /**
     * 判断是否在同一局域网内
     */
    public static boolean isSameIpAddress(Context context, String deskDeviceIp) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        int ipAddress = dhcpInfo.ipAddress;
        int ipMask = dhcpInfo.netmask;
        String localIp = getStringIp(ipAddress);
        String localMask = getStringIp(ipMask);
        int mask = IpV4Util.getIpV4Value(localMask);
        return IpV4Util.checkSameSegment(deskDeviceIp, localIp, mask);
    }

    public static String getStringIp(long ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }

    /**
     * 获取sd卡信息
     */
    public static Map<String, Integer> byteArrayToMap(byte byt[]) {
        Map<String, Integer> maps = new HashMap<>();
        maps.put(Config.status, Packet.byteArrayToInt_Little(byt, 0));
        maps.put(Config.sdexist, Packet.byteArrayToInt_Little(byt, 4));
        maps.put(Config.totalMB, Packet.byteArrayToInt_Little(byt, 8));
        maps.put(Config.freeMB, Packet.byteArrayToInt_Little(byt, 12));
        return maps;
    }

    public static byte[] stringToByteforSearch(String str, int type) {
        String year, month, day;
        byte[] searchByte = null;
        if (str != null) {
            year = str.substring(2, 4);
            month = contains(str.substring(5, 7));
            day = contains(str.substring(8, 10));
            if (type == 0) {
                searchByte = new byte[]{
                        parseByte(year), parseByte(month), parseByte(day), 0,
                        0, 0, 1, 0
                };
            } else {
                searchByte = new byte[]{
                        parseByte(year), parseByte(month), parseByte(day), 0,
                        23, 59, 59, 0
                };
            }
        }
        return searchByte;
    }


    public static byte[] stringToByteforSearchHawk(String str, int type) {
        String month, day;
        byte[] searchByte = null;
        if (str != null) {
            month = contains(str.substring(5, 7));
            day = contains(str.substring(8, 10));
            if (type == 0) {
                searchByte = new byte[]{
                        parseByte(month), (byte) (parseByte(day) - 1), 0x00,
                        0x11, 0x12, 0x16
                };
            } else {
                searchByte = new byte[]{
                        parseByte(month), parseByte(day), 0x00,
                        0xf, 0x3b, 0x3b
                };
            }
        }
        return searchByte;
    }

    public static byte[] stringToByteforPlay(String str) {
        String year, month, day, time, minute, sec;
        byte[] playByte = null;
        if (str != null) {
            year = str.substring(2, 4);
            month = contains(str.substring(4, 6));
            day = contains(str.substring(6, 8));
            time = contains(str.substring(9, 11));
            minute = contains(str.substring(11, 13));
            sec = contains(str.substring(13, 15));
            playByte = new byte[]{
                    parseByte(year), parseByte(month), parseByte(day), 0,
                    parseByte(time), parseByte(minute), parseByte(sec), 0};

        }
        return playByte;
    }

    private static String contains(String str) {
//		if(str.contains("0")){
//			str = str.substring(1);
//		}
        return str;
    }

    private static byte parseByte(String str) {
        byte var = -1;
        if (str != null && !str.equals("")) {
            var = Byte.parseByte(str);
        }
        return var;
    }

    public static int selectDecode() {
        if ("Meizu".equals(android.os.Build.MANUFACTURER)) {
            return Camera.SOFT_DECODE;
        }else if("Letv".equals(Build.MANUFACTURER)){//add by hxc 乐视手机暂时使软解码
            return Camera.SOFT_DECODE;
        }
        return Camera.HARD_DECODE;
    }
}
