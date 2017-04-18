package cc.wulian.lan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.ResultUtil;

/**
 * Created by yanzy on 2016-6-21
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public class LanSocketConnection {	
	private Socket connToGateway = null;
	private ReaderThread reader = null;
	private WriterThread writer = null;
	
	private LanSocketConnectionHandler handler;
	
	public LanSocketConnection(LanSocketConnectionHandler handler) {
		this.handler = handler; 
	}
	
	public LanSocketConnectionHandler getHandler() {
		return handler;
	}
	
	public InputStream getInputStream() throws IOException {
		if(connToGateway == null || connToGateway.isClosed()) {
			throw new IOException("Should connect to gateway at first");
		}
		return connToGateway.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		if(connToGateway == null || connToGateway.isClosed()) {
			throw new IOException("Should connect to gateway at first");
		}
		return connToGateway.getOutputStream();
	}

	public void connectGateway(String ip, int port) throws IOException {
		connToGateway = new Socket();
		connToGateway.connect(new InetSocketAddress(ip, port));
		reader = new ReaderThread();
		reader.startup();
		writer = new WriterThread();
		writer.startup();
	}
	
	public boolean isConnected() {
		return connToGateway != null && connToGateway.isConnected();
	}
	
	public void disconnect() {
		if(connToGateway != null && connToGateway.isClosed() == false) {
			try {
				connToGateway.close();
				connToGateway = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(reader != null) {
			reader.shutdown();
			reader = null;
		}
		if(writer != null) {
			writer.shutdown();
			writer = null;
		}
	}
	
	public void fireDisconnected(int reason) {
		this.disconnect();
		if(this.handler!=null){
			handler.connectionBroken(reason);	
		}
	}
	
	public void fireMessageReceived(String msg) {
		if(this.handler!=null){
			handler.receviedMessage(msg);
		}
	}
	
	public void sendMessage(String msg) throws IOException {
		if(writer == null) {
			throw new IOException("Should connect to gateway at first");
		}
		writer.sendMsg(msg);
	}
	
	private class WriterThread extends Thread {

		private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
		private Writer writer;

		public WriterThread() throws UnsupportedEncodingException, IOException {
			initWriter();
		}

		public void initWriter() throws UnsupportedEncodingException, IOException {
			writer = new BufferedWriter(new OutputStreamWriter(LanSocketConnection.this.getOutputStream(), "utf-8"));
		}

		private void sendMsgToOutputStream(String msg) throws IOException {
			Logger.debug("write -->" + msg);
			writer.write(msg + "\r\n");
			writer.flush();
		}

		@Override
		public void run() {
			Logger.debug("writethread: start Thread");
			try {
				while (!isInterrupted()) {
					String msg = queue.take();
					if (msg != null && LanSocketConnection.this.isConnected()) {
						sendMsgToOutputStream(msg);
					}
				}
			} catch (SocketException se) {
				if (!se.getMessage().startsWith("Socket Closed")) {
					se.printStackTrace();
					Logger.debug("write thread execpetion");
					LanSocketConnection.this.fireDisconnected(ResultUtil.RESULT_EXCEPTION);
				}
			} catch (IOException e) {
				e.printStackTrace();
				Logger.debug("write thread execpetion");
				LanSocketConnection.this.fireDisconnected(ResultUtil.RESULT_EXCEPTION);
			} catch (InterruptedException e) {
				//just stop,do nothing;
				//LanSocketConnection.this.fireDisconnected(ResultUtil.RESULT_DISCONNECT);				
			} finally {
				Logger.debug("writethread: stop Thread");
			}
		}

		public void sendMsg(String msg) {
			try {
				queue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void startup() {
			this.start();
		}

		public void shutdown() {
			this.interrupt();
		}
	}

	
	private class ReaderThread extends Thread {

		private BufferedReader reader;
		public ReaderThread() throws UnsupportedEncodingException, IOException {
			initReader();
		}

		public void initReader() throws UnsupportedEncodingException, IOException {
			reader = new BufferedReader(new InputStreamReader(LanSocketConnection.this.getInputStream(), "utf-8"));
		}

		public void run() {
			Logger.debug("LanSocketConnection readthread:start Read Thread");
			try {
				readDataFromSocket();
			} catch (java.net.SocketTimeoutException te) {				
				//Logger.error(te.printStackTrace());
				Logger.debug("LanSocketConnection read thread execpetion");
				LanSocketConnection.this.fireDisconnected(ResultUtil.RESULT_EXCEPTION);
			} catch (SocketException se) {
				if (!se.getMessage().toLowerCase().startsWith("LanSocketConnection Socket Closed".toLowerCase())) {
					//Logger.error(se);
					Logger.debug("LanSocketConnection read thread execpetion");
					LanSocketConnection.this.fireDisconnected(ResultUtil.RESULT_EXCEPTION);
				}
			} catch (IOException e) {
				//Logger.error(e);
				e.printStackTrace();
				LanSocketConnection.this.fireDisconnected(ResultUtil.RESULT_EXCEPTION);
			} finally {
				Logger.debug("readthread:over Read Thread");
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						//do nothing
					}
				}
			}
		}

		private void readDataFromSocket() throws IOException {
			String msg = null;
			while (!isInterrupted() && reader != null) {
				msg = reader.readLine();
				if (msg == null) {
					Logger.error("Read a null line, the socket is closed.");
					LanSocketConnection.this.fireDisconnected(ResultUtil.RESULT_FAILED);
					break;
				}
				Logger.debug("read<--" + msg);
				LanSocketConnection.this.fireMessageReceived(msg);
			}
		}

		public void startup() {
			start();
		}

		public void shutdown() {
			interrupt();
		}
	}

}
