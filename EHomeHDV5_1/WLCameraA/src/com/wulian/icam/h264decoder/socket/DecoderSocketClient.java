package com.wulian.icam.h264decoder.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

import javax.net.SocketFactory;

import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import android.os.HandlerThread;

import com.wulian.icam.h264decoder.SocketConfigure;
import com.wulian.icam.h264decoder.SocketErrorCode;
import com.wulian.icam.h264decoder.SocketMsgApiType;
import com.wulian.routelibrary.utils.LibraryLoger;

public class DecoderSocketClient {
	private static final String TAG = "WebSocketClient";

	private URI mURI;
	private Listener mListener;
	private Socket mSocket;
	private Thread mThread;
	private HandlerThread mHandlerThread;
	private Handler mHandler;
	private List<BasicNameValuePair> mExtraHeaders;
	private String mRequestData;
	private DecoderParser mParser;

	private final Object mSendLock = new Object();

	public DecoderSocketClient(URI uri, Listener listener,
			List<BasicNameValuePair> extraHeaders, String requestData) {
		mURI = uri;
		mListener = listener;
		mExtraHeaders = extraHeaders;
		mRequestData = requestData;
		mParser = new DecoderParser(this);

		mHandlerThread = new HandlerThread("wuliansocket-thread");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper());
	}

	public Listener getListener() {
		return mListener;
	}

	public void connect() {
		if (mThread != null && mThread.isAlive()) {
			return;
		}
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LibraryLoger.d("The URL is:" + mURI.getHost());
					int port = (mURI.getPort() != -1) ? mURI.getPort()
							: SocketConfigure.DEFAULT_SERVER_HOST_PORT;
					LibraryLoger.d("The Port is:" + port);

					SocketFactory factory = SocketFactory.getDefault();
					mSocket = factory.createSocket(mURI.getHost(), port);

					DecoderParser.HappyDataInputStream stream = new DecoderParser.HappyDataInputStream(
							mSocket.getInputStream());

					mListener.onConnect();

					mParser.start(stream);

				} catch (UnknownHostException e) {
					mListener.onDisconnect(SocketErrorCode.UNKNOWN_HOST);
				} catch (EOFException ex) {
					LibraryLoger.d(TAG, "WebSocket EOF!");
					mListener.onDisconnect(SocketErrorCode.EOF);
				} catch (IOException ex) {
					mListener.onError(SocketErrorCode.INVALID_IO);
				} catch (Exception ex) {
					mListener.onError(SocketErrorCode.UNKNOWN_EXCEPTION);
				}
			}
		});
		mThread.start();
	}

	public void disconnect() {
		if (mSocket != null) {
			LibraryLoger.d("The websocket disconnect");
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					try {
						LibraryLoger.d("The websocket run");
						mSocket.close();
						mSocket = null;
						LibraryLoger.d("The websocket close");
					} catch (IOException ex) {
						LibraryLoger.d(TAG, "Error while disconnecting");
						mListener.onError(SocketErrorCode.INVALID_IO);
					}
				}
			});
		}
	}

	public void send(String data) {
		sendFrame(data);
	}

	public void send(byte[] data) {
		sendFrame(data);
	}

	// 发送字符串消息
	void sendFrame(final String frame) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (mSendLock) {
						LibraryLoger.d("mSocket===" + mSocket);
						PrintWriter mPrintWriter = new PrintWriter(mSocket
								.getOutputStream(), true);
						mPrintWriter.print(frame);
						mPrintWriter.flush();
					}
				} catch (IOException e) {
					mListener.onError(SocketErrorCode.INVALID_IO);
				}
			}
		});
	}

	// 发送二进制消息
	void sendFrame(final byte[] frame) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					synchronized (mSendLock) {
						LibraryLoger.d("mSocket===" + mSocket);
						OutputStream outputStream = mSocket.getOutputStream();
						outputStream.write(frame);
						outputStream.flush();
					}
				} catch (IOException e) {
					mListener.onError(SocketErrorCode.INVALID_IO);
				}
			}
		});
	}

	public interface Listener {
		public void onConnect();// 连接

		public void onMessage(SocketMsgApiType api, String data);;// 返回普通消息

		public void onFileMessage(byte[] data);// 返回文件数据

		public void onH264StreamMessage(byte[] data);// 返回H264流消息

		public void onDisconnect(SocketErrorCode errorcode);// 断开

		public void onError(SocketErrorCode error);// 错误
	}

}
