package cc.wulian.smarthomev5.tools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileUpload {
	private final static String FILE_PREFIX = "file://";
	private final static int FILE_PREFIX_IDX = FILE_PREFIX.length();
	
	private final static Map<String, String> suffixMap = new HashMap<String, String>();
	static {
		suffixMap.put("png", "image/jpeg");
		suffixMap.put("jpg", "image/jpeg");
		suffixMap.put("log", "text/plain");
		suffixMap.put("cfg", "text/plain");
		suffixMap.put("dat", "text/plain");
	}
	
	public static String getMime(String suffix) {
		if(suffixMap.containsKey(suffix)) {
			return suffixMap.get(suffix);
		} else {
			return "application/octet-stream";
		}
	}
	
	
    /** 
     * 上传文件
     * @param urlStr 
     * @param textMap 
     * @param fileMap 
     * @return 
     * @throws IOException 
     */  
    public static String formUpload(String urlStr, Map<String, String> textMap, Map<String, String> fileMap) throws IOException {  
        String res = "";  
        HttpURLConnection conn = null;  
        String BOUNDARY = "---------------------------1238174asdfsd8716"; 
        OutputStream out =null;
        DataInputStream in =null;
        try {  
            URL url = new URL(urlStr);  
            conn = (HttpURLConnection) url.openConnection();  
            conn.setConnectTimeout(5000);  
            conn.setReadTimeout(30000);  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            conn.setRequestMethod("POST");  
            conn.setRequestProperty("Connection", "Keep-Alive");  
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");  
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
  
            out = new DataOutputStream(conn.getOutputStream());  
            // text    
            if (textMap != null) {  
                StringBuffer strBuf = new StringBuffer();  
                Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry<String, String> entry = iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }  
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");  
                    strBuf.append(inputValue);  
                }  
                out.write(strBuf.toString().getBytes());  
            }  
  
            // file    
            if (fileMap != null) {  
                Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();  
                while (iter.hasNext()) {  
                    Map.Entry<String, String> entry = iter.next();  
                    String inputName = (String) entry.getKey();  
                    String inputValue = (String) entry.getValue();  
                    if (inputValue == null) {  
                        continue;  
                    }
                    if (inputValue.startsWith(FILE_PREFIX)) {
                    	inputValue = inputValue.substring(FILE_PREFIX_IDX);
                    }
                    File file = new File(inputValue);  
                    String filename = file.getName();
                    String suffix = filename.substring(filename.lastIndexOf('.') + 1);
                    String contentType = getMime(suffix);  
  
                    StringBuffer strBuf = new StringBuffer();  
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");  
                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");  
                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");  
  
                    out.write(strBuf.toString().getBytes());  
  
                    in = new DataInputStream(new FileInputStream(file));  
                    int bytes = 0;  
                    byte[] bufferOut = new byte[1024];  
                    while ((bytes = in.read(bufferOut)) != -1) {  
                        out.write(bufferOut, 0, bytes);  
                    }  
                }  
            }  
  
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();  
            out.write(endData);  
            out.flush();  
            // 返回值
            StringBuffer strBuf = new StringBuffer();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));  
            String line = null;  
            while ((line = reader.readLine()) != null) {  
                strBuf.append(line).append("\n");  
            }  
            res = strBuf.toString();  
            reader.close();  
            reader = null;  
        } finally {  
        	if(out!=null)out.close();
        	if(in!=null) in.close();  
            if (conn != null) {  
                conn.disconnect();  
                conn = null;  
            }  
        }  
        return res;  
    }  

}
