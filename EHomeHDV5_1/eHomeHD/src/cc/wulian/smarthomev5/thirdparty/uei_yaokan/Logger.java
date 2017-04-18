package cc.wulian.smarthomev5.thirdparty.uei_yaokan;

/**
 * Log 记录
 */
 public class Logger {

    /**
     * true 代表打印日志
     */
    public static boolean mLogGrade = true;

    public static void e(String tag, String msg, Exception e) {
        if (mLogGrade) {
            android.util.Log.e(tag, ""+msg);
        }
    }

    public static void e(String tag, String msg) {
        if (mLogGrade) {
            android.util.Log.e(tag, ""+msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mLogGrade) {
            android.util.Log.i(tag, ""+msg);
        }
    }

    public static void w(String tag, String msg) {
        if (mLogGrade) {
            android.util.Log.w(tag, ""+msg);
        }
    }

    public static void w(String tag, String msg, Exception e) {
        if (mLogGrade) {
            android.util.Log.w(tag, ""+msg);
        }
    }

    public static void w(String tag, String msg, Throwable cause) {
        if (mLogGrade) {
            android.util.Log.w(tag, ""+msg);
        }
    }

    public static void v(String tag, String msg) {
        if (mLogGrade) {
            android.util.Log.v(tag, ""+msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mLogGrade) {
            android.util.Log.d(tag, ""+msg);
        }
    }
}
