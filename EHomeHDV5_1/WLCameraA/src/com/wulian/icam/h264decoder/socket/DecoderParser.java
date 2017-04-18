package com.wulian.icam.h264decoder.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.message.BasicLineParser;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.wulian.icam.h264decoder.SocketAction;
import com.wulian.icam.h264decoder.SocketErrorCode;
import com.wulian.icam.h264decoder.SocketMsgApiType;

public class DecoderParser {
	private static final String TAG = "DecoderParser";
	private static final int DEFAULT_LENGTH_SIZE = 2048;// 默认的片长度
	private DecoderSocketClient mClient;

	private int mCurrentStage;

	private int mLengthSize;
	private boolean mStreamInit = false;
	private SocketAction mCurrentSocketAction;//

	private static final int OP_TEXT = 0; // 字符串文字
	private static final int OP_BINARY = 1; // 二进制
	private static final int OP_CLOSE = 2;// 关闭
	private static final int OP_CONTINUATION = 3;// 继续上一个状态

	public DecoderParser(DecoderSocketClient client) {
		mClient = client;
		mCurrentStage = OP_TEXT;
		mCurrentSocketAction = SocketAction.DEFAULT;
		mLengthSize = DEFAULT_LENGTH_SIZE;
	}

	public void start(HappyDataInputStream stream) throws IOException {
		while (true) {
			if (stream.available() == -1)
				break;
			switch (mCurrentStage) {
			case OP_TEXT:
				parseOpcode(readLine(stream));
				break;
			case OP_BINARY:
				parseOpcode(stream.readBytes(mLengthSize));
				break;
			case OP_CLOSE:
				break;
			case OP_CONTINUATION:
				break;
			}
		}
		if (mStreamInit) {
			UninitDecoder();
		}
		mClient.getListener().onDisconnect(SocketErrorCode.EOF);
	}

	private void parseOpcode(String msg) {
		if (!TextUtils.isEmpty(msg)) {
			switch (mCurrentSocketAction) {
			case CONNECTION:
			case CONTROL:
			case GET:
				handleMsgData(msg);
				break;
			case DEFAULT:
			case PICTURE:
			case STREAM:
				Header header = parseHeader(msg);
				if (header != null) {
					if (mCurrentSocketAction == SocketAction.DEFAULT) {
						if (header.getName().equals("Action")) {
							String action = header.getValue();
							mCurrentSocketAction = SocketAction
									.getAction(action);
							mCurrentStage = OP_TEXT;
						}
					} else if (header.getName().equals("Content-Length")) {
						try {
							mLengthSize = Integer.parseInt(header.getValue());
						} catch (NumberFormatException e) {
							// TODO
							mLengthSize = 0;
						}
						mCurrentStage = OP_BINARY;
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private void parseOpcode(byte[] msg) {
		switch (mCurrentSocketAction) {
		case STREAM:
			handleStream(msg, msg.length);
			break;
		case PICTURE:
			handlePictureData(msg);
			break;
		default:
			break;
		}
		mCurrentStage = OP_TEXT;
		mCurrentSocketAction = SocketAction.DEFAULT;
	}

	private Header parseHeader(String line) {
		return BasicLineParser.parseHeader(line, new BasicLineParser());
	}

	private String readLine(HappyDataInputStream reader) throws IOException {
		int readChar = reader.read();
		if (readChar == -1) {
			return null;
		}
		StringBuilder string = new StringBuilder("");
		while (readChar != '\n') {
			if (readChar != '\r') {
				string.append((char) readChar);
			}
			readChar = reader.read();
			if (readChar == -1) {
				return null;
			}
		}
		return string.toString();
	}

	// 处理普通的消息数据
	private void handleMsgData(String data) {
		try {
			JSONObject json = new JSONObject(data);
			SocketErrorCode errorCode;
			if (json.isNull("statuscode")) {
				mClient.getListener()
						.onError(SocketErrorCode.UNKNOWN_EXCEPTION);
				return;
			}
			errorCode = SocketErrorCode
					.getTypeByCode(json.getInt("statuscode"));

			if (errorCode == SocketErrorCode.SUCCESS) {
				if (json.isNull("cmd")) {
					mClient.getListener().onError(
							SocketErrorCode.UNKNOWN_EXCEPTION);
				} else {
					SocketMsgApiType msgApi = SocketMsgApiType
							.getSipTypeByRespondCmd(json.getString("cmd"));
					if (msgApi != null) {
						mClient.getListener()
								.onMessage(msgApi, data);
					} else {
						mClient.getListener().onError(
								SocketErrorCode.UNKNOWN_EXCEPTION);
					}
				}
			} else {
				mClient.getListener().onError(errorCode);
			}
		} catch (JSONException e) {
			mClient.getListener().onError(SocketErrorCode.UNKNOWN_EXCEPTION);
		}
	}

	// 处理图片数据
	private void handlePictureData(byte[] data) {

	}

	/***********************************************/
	int mTrans = 0x0F0F0F0F;
	int mWidth = 640; // 此处设定不同的分辨率
	int mHeight = 480;// 默认值
	int iTemp = 0;
	int nalLen;

	boolean bFirst = true;
	boolean bFindPPS = true;
	int NalBufUsed = 0;
	int SockBufUsed = 0;
	byte[] mPixel;
	byte[] NalBuf = new byte[81960]; // 80k

	/***********************************************/
	private void initH264Stream() {
		InitDecoder(mWidth, mHeight);
		mPixel = new byte[mWidth * mHeight * 2];
		int i = mPixel.length;

		for (i = 0; i < mPixel.length; i++) {
			mPixel[i] = (byte) 0x00;
		}
	}

	// 处理流数据
	private void handleStream(byte[] originData, int readLength) {
		if (readLength <= 0) {
			return;
		}
		if (!mStreamInit) {
			initH264Stream();
			mStreamInit = true;
		}
		SockBufUsed = 0;
		while (readLength - SockBufUsed > 0) {
			nalLen = MergeBuffer(NalBuf, NalBufUsed, originData, SockBufUsed,
					readLength - SockBufUsed);
			NalBufUsed += nalLen;
			SockBufUsed += nalLen;
			while (mTrans == 1) {
				mTrans = 0xFFFFFFFF;
				if (bFirst == true) {
					bFirst = false;
				} else {
					if (bFindPPS == true) {
						if ((NalBuf[4] & 0x1F) == 7) {
							bFindPPS = false;
						} else {
							NalBuf[0] = 0;
							NalBuf[1] = 0;
							NalBuf[2] = 0;
							NalBuf[3] = 1;
							NalBufUsed = 4;
							break;
						}
					}
					iTemp = DecoderNal(NalBuf, NalBufUsed - 4, mPixel);
					if (iTemp > 0) {
						mClient.getListener().onH264StreamMessage(mPixel);
					}
				}
				NalBuf[0] = 0;
				NalBuf[1] = 0;
				NalBuf[2] = 0;
				NalBuf[3] = 1;
				NalBufUsed = 4;
			}
		}
	}

	private int MergeBuffer(byte[] NalBuf, int NalBufUsed, byte[] SockBuf,
			int SockBufUsed, int SockRemain) {
		int i = 0;
		byte Temp;

		for (i = 0; i < SockRemain; i++) {
			Temp = SockBuf[i + SockBufUsed];
			NalBuf[i + NalBufUsed] = Temp;
			mTrans <<= 8;
			mTrans |= Temp;
			if (mTrans == 1) // 找到一个开始字
			{
				i++;
				break;
			}
		}
		return i;
	}

	public static class HappyDataInputStream extends DataInputStream {
		public HappyDataInputStream(InputStream in) {
			super(in);
		}

		public byte[] readBytes(int length) throws IOException {
			byte[] buffer = new byte[length];

			int total = 0;

			while (total < length) {
				int count = read(buffer, total, length - total);
				if (count == -1) {
					break;
				}
				total += count;
			}

			if (total != length) {
				throw new IOException(String.format(
						"Read wrong number of bytes. Got: %s, Expected: %s.",
						total, length));
			}

			return buffer;
		}
	}

	public native int InitDecoder(int width, int height);

	public native int UninitDecoder();

	public native int DecoderNal(byte[] in, int insize, byte[] out);

	static {
		System.loadLibrary("H264Android");
	}

}
