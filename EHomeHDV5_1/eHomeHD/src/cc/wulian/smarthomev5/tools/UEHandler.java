package cc.wulian.smarthomev5.tools;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.LogUtil;
import cc.wulian.smarthomev5.utils.VersionUtil;

import com.lidroid.xutils.util.LogUtils;

public class UEHandler implements Thread.UncaughtExceptionHandler {
	private final MainApplication app;
	private StringBuffer sb;

	public UEHandler(MainApplication app) {
		this.app = app;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		String errorInfo = null;
		ByteArrayOutputStream baos = null;
		PrintStream printStream = null;
		try {
			baos = new ByteArrayOutputStream();
			printStream = new PrintStream(baos);
			ex.printStackTrace(printStream);
			LogUtil.logException("", ex);
			byte[] data = baos.toByteArray();
			errorInfo = new String(data);
			data = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (printStream != null)
					printStream.close();
				if (baos != null)
					baos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		LogUtils.e("debug_error", ex);
		collectApplicationInfo(errorInfo);
		handleException(thread);
	}

	private void collectApplicationInfo(String errorInfo) {
		sb = new StringBuffer();
		sb.append(errorInfo);
		sb.append("\r\n");
		sb.append("----------------------------------------------");
		sb.append("\r\n");
		sb.append(VersionUtil.getVersionInfo());
		sb.append(VersionUtil.getSystemEdition(app));
	}

	private void handleException(Thread thread) {
		FileUtil.writeLogger(":\n"+sb.toString());
		long threadId = thread.getId();
		if (threadId == 1) {
//			app.mBackNotification.cancelNotification(R.string.app_name);
//			app.stopApplication();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}
}