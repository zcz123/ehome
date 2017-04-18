package com.tutk.SLC;

import android.annotation.SuppressLint;
import java.io.Closeable;
import java.util.Arrays;

public class AcousticEchoCanceler implements Closeable {
	static final String TAG = "SLCAec";
	private short[] mCaptureBuf;

	static {
		System.loadLibrary("SLCAec");
	}

	private native void nativeInit();

	private native void nativeProcessMicFrame(short[] paramArrayOfShort,
			int paramInt1, int paramInt2);

	private native void nativeProcessSpeakerFrame(short[] paramArrayOfShort,
			int paramInt1, int paramInt2);

	private native void nativeTerminate();

	@SuppressLint("NewApi")
	public void Capture(short[] paramArrayOfShort) {
		try {
			mCaptureBuf = Arrays.copyOf(paramArrayOfShort,
					paramArrayOfShort.length);
		} catch (Exception e) {

		}
	}

	public void Open() {
		nativeInit();
	}

	public void Play(short[] paramArrayOfShort) {
		while (true) {
			int j;
			try {
				short[] arrayOfShort = this.mCaptureBuf;
				if (arrayOfShort == null)
					return;
				int i = paramArrayOfShort.length / 32;
				j = 0;
				if (j < i) {

				} else {
					mCaptureBuf = null;
					break;
				}
				nativeProcessSpeakerFrame(mCaptureBuf, j * 32, 32);
				nativeProcessMicFrame(paramArrayOfShort, j * 32, 32);
				++j;
			} catch (Exception e) {
			}
		}
	}

	public void close() {
		nativeTerminate();
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\tutkdemo.jar Qualified Name:
 * com.tutk.SLC.AcousticEchoCanceler JD-Core Version: 0.5.4
 */