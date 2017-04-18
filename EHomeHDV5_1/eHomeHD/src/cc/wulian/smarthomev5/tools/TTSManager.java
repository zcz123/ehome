package cc.wulian.smarthomev5.tools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.media.MediaPlayer;
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.alibaba.fastjson.JSONObject;

public class TTSManager {

	private static TTSManager instance = new TTSManager();
	private TTSManager() {
	}
	public static TTSManager getInstance(){
		return instance;
	}
	public void readTts(String strRead , String strFrom){
		getTTSProvider(Locale.getDefault()).readTts(strRead , strFrom);
	}
	public void speak(int speed,String strRead){
		getTTSProvider(Locale.getDefault()).speak(speed, strRead);
	}
	private TTSProvider getTTSProvider(Locale locale){
		if(LanguageUtil.isChina() || LanguageUtil.isEnglish() || LanguageUtil.isTaiWan()){
			return BaiduOnlineTtsManager.getInstance();
		}else if(LanguageUtil.getLanguage().equals("ru") || LanguageUtil.getLanguage().equals("es") || LanguageUtil.getLanguage().equals("pt")){
			FreeOnlineTtsManager freeTTS = FreeOnlineTtsManager.getInstance();
			freeTTS.setLocale(locale.getLanguage());
			return freeTTS;
		}else{
			GoogleTtsManager googleTTS = GoogleTtsManager.getInstance();
			googleTTS.setLocale(locale.getLanguage());
			return googleTTS;
		}
	}
	public interface TTSProvider{
		public void readTts(String strRead , String strFrom);
		public void speak(int speed,String strRead);
	}
	public static class GoogleTtsManager implements TTSProvider
	{
		private MediaPlayer mediaPlayer = new MediaPlayer();
		private static GoogleTtsManager instance = new GoogleTtsManager();
		private String locale;
		private GoogleTtsManager(){
			
		}
		public static GoogleTtsManager getInstance(){
			return instance;
		}
		
		public String getLocale() {
			return locale;
		}
		public void setLocale(String locale) {
			this.locale = locale;
		}

		@Override
		public void readTts(String strRead, String strFrom) {
			speak(0, strRead);
		}

		@Override
		public void speak(int speed, String strRead) {
			String filePath = FileUtil.getMscPath()+"/" + strRead + ".mp3";
			File file = new File(filePath);
			if (file.exists()) {
				readLocal(filePath);
			}else {
				readOnline(strRead, filePath);
			}
			
		}

		private void readLocal(final String filePath) {
			try {
				if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
				mediaPlayer.setDataSource(filePath);
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		public void readOnline(final String strRead, final String filePath) {
			try {
				download(strRead, filePath);
				readLocal(filePath);
			}
			catch (IOException e) {
				return;
			}
		}

		public void download(String strRead, String filePath) throws IOException {
			String strMp3URL = getMp3URLByCountry(strRead,locale);
			Logger.debug("mp3URL:"+strMp3URL);
			if (StringUtil.isNullOrEmpty(strMp3URL)) return;
			DownloadManager downloadManager = new DownloadManager(strMp3URL, filePath);
			downloadManager.startDonwLoadFile();
		}
		private String getMp3URLByCountry(String strRead,String locale ) throws UnsupportedEncodingException {
			if (!StringUtil.isNullOrEmpty(strRead)) {
				return "http://translate.google.cn/translate_tts?ie=UTF-8&q=" + URLEncoder.encode(strRead, "UTF-8") + "&tl="+locale;
			}
			return null;
		}
		public boolean deleteMsc() {
			boolean result = true;
			
			try {
				File file = new File(FileUtil.getMscPath());
				File[] childFiles = file.listFiles();  
				if (childFiles == null || childFiles.length == 0) {  
					result =  file.delete();  			              
				}  
				
				for (int i = 0; i < childFiles.length; i++) {  
					result = childFiles[i].delete();  
				}  
				result =  file.delete();  
			} catch (Exception e) {
				e.printStackTrace();
			}
				        
			return result;
		}
	    // GENERAL_PUNCTUATION 判断中文的“号
	    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
	    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
//	    private  final boolean isChinese(char c) {
//	        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//	        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//	                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//	                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//	                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
//	                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//	                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
//	            return true;
//	        }
//	        return false;
//	    }
//	 
//	    public  final boolean isChinese(String strName) {
//	        char[] ch = strName.toCharArray();
//	        for (int i = 0; i < ch.length; i++) {
//	            char c = ch[i];
//	            if (isChinese(c)) {
//	                return true;
//	            }
//	        }
//	        return false;
//	    }
		
	}
	
	public static class FreeOnlineTtsManager implements TTSProvider{

		private MediaPlayer mediaPlayer = new MediaPlayer();
		private Preference preference = Preference.getPreferences();
		private static FreeOnlineTtsManager instance = new FreeOnlineTtsManager();
		private String locale;
		private FreeOnlineTtsManager(){
			
		}
		public static FreeOnlineTtsManager getInstance(){
			return instance;
		}
		
		public String getLocale() {
			return locale;
		}
		public void setLocale(String locale) {
			this.locale = locale;
		}

		@Override
		public void readTts(String strRead, String strFrom) {
			int speed = preference.getInt(strFrom + "_" + IPreferenceKey.P_KEY_VOICE_SPEED,5);
			if(speed >= 1 && speed <= 2){
				speed = -1;
			}else if(speed >= 3 && speed <= 5){
				speed = 0;
			}else if(speed >= 6 && speed <= 7){
				speed = 1;
			}else{
				speed = 2;
			}
			speak(speed, strRead);
		}

		@Override
		public void speak(int speed, String strRead) {
			String filePath = FileUtil.getMscPath()+"/" + strRead + speed + ".mp3";
			File file = new File(filePath);
			if (file.exists()) {
				readLocal(filePath);
			}else {
				readOnline(strRead, speed, filePath);
			}
			
		}

		private void readLocal(final String filePath) {
			try {
				if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
				mediaPlayer.setDataSource(filePath);
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		public void readOnline(final String strRead,final int speed, final String filePath) {
			try {
				download(strRead, speed,filePath);
				readLocal(filePath);
			}
			catch (IOException e) {
				return;
			}
		}

		public void download(String strRead, int speed, String filePath) throws IOException {
			String strMp3URL = getMp3URLByCountry(strRead,speed,locale);
			Logger.debug("mp3URL:"+strMp3URL);
			if (StringUtil.isNullOrEmpty(strMp3URL)) return;
			DownloadManager downloadManager = new DownloadManager(strMp3URL, filePath);
			downloadManager.startDonwLoadFile();
		}
		private String getMp3URLByCountry(String strRead,int speed,String locale ) throws UnsupportedEncodingException {
			String ttsUrl = "http://www.fromtexttospeech.com";
			Map<String, String> paramters = new HashMap<String, String>();
			paramters.put("input_text", strRead);
			if(StringUtil.equals("ru", locale)){
				paramters.put("language", "Russian");
				paramters.put("voice", "IVONA Tatyana22 (Russian)");
			}else if(StringUtil.equals("es", locale)){
				paramters.put("language", "Spanish");
				paramters.put("voice", "IVONA Conchita22 (Spanish [Modern])");
			}else if(StringUtil.equals("pt", locale)){
				paramters.put("voice", "IVONA Cristiano22 (Portuguese)");
				paramters.put("language", "Portuguese");
			}
			paramters.put("speed", speed + "");
			paramters.put("action", "process_text");
			JSONObject object = HttpManager.getDefaultProvider().post(ttsUrl, paramters);
			Pattern checkPattern = Pattern.compile("<BR><a href=(.*?)>Download audio file");
			Matcher checContentm = checkPattern.matcher(object.toJSONString());
			String mp3Str = "";
			while(checContentm.find()){
				mp3Str = checContentm.group(1);
			}
			if(!StringUtil.isNullOrEmpty(mp3Str)){
				String mp3Url = ttsUrl  + mp3Str.substring(1, mp3Str.length()-1);
				return mp3Url;
			}else{
				return null;
			}
		}
		public boolean deleteMsc() {
			boolean result = true;
			
			try {
				File file = new File(FileUtil.getMscPath());
				File[] childFiles = file.listFiles();  
				if (childFiles == null || childFiles.length == 0) {  
					result =  file.delete();  			              
				}  
				
				for (int i = 0; i < childFiles.length; i++) {  
					result = childFiles[i].delete();  
				}  
				result =  file.delete();  
			} catch (Exception e) {
				e.printStackTrace();
			}
				        
			return result;
		}
		
	}

	public static class BaiduOnlineTtsManager implements TTSProvider{

		private MediaPlayer mediaPlayer = new MediaPlayer();
		private static String KEY_API = "xX03hOjMHm4WXPHV2Ob2YtMl";
		private static String SECRET_API = "iP0rkPy8obQKASiU6lG2FYq4EYBY2Hjx";
		private static String GRANT_TYPE = "client_credentials";
		private static long BAIDU_TOKEN_TIME_20_DAY = 1728000000;
		private static BaiduOnlineTtsManager instance = new BaiduOnlineTtsManager();
		private Preference preference = Preference.getPreferences();
		private BaiduOnlineTtsManager(){
		}
		public static BaiduOnlineTtsManager getInstance(){
			return instance;
		}

		@Override
		public void readTts(String strRead, String strFrom) {
			int speed = preference.getInt(strFrom + "_" + IPreferenceKey.P_KEY_VOICE_SPEED,5);
			speak(speed, strRead);
		}

		@Override
		public void speak(int speed, String strRead) {
			strRead=strRead.replace("/","");
			String filePath = FileUtil.getMscPath()+"/" + strRead + speed + ".mp3";
			File file = new File(filePath);
			if (file.exists()) {
				readLocal(filePath);
			}else {
				readOnline(strRead, speed, filePath);
			}
		}
		private void readLocal(String filePath){
			try {
				if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
					mediaPlayer.stop();
				}
				mediaPlayer.reset();
				mediaPlayer.setDataSource(filePath);
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void readOnline(String strRead,int speed,String filePath){
			try {
				download(strRead, speed,filePath);
				readLocal(filePath);
			}
			catch (IOException e) {
				return;
			}
		}
		
		public void download(String strRead, int speed, String filePath) throws IOException {
			String strUrl = getMp3URLByCountry(strRead,speed);
			Logger.debug("mp3URL:"+strUrl);
			if (StringUtil.isNullOrEmpty(strUrl)) return;
			DownloadManager downloadManager = new DownloadManager(strUrl, filePath);
			downloadManager.startDonwLoadFile();
		}
		private String getMp3URLByCountry(String strRead,int speed) throws UnsupportedEncodingException{
			long time = preference.getLong(IPreferenceKey.P_KEY_BAIDU_TOKEN_TIME, 0);
			String baiduToken = preference.getString(IPreferenceKey.P_KEY_BAIDU_TOKEN, "");
			if(time != 0){
				long timePre = System.currentTimeMillis() - time;
				//20天
//				if(timePre > BAIDU_TOKEN_TIME_20_DAY){
//					baiduToken = getBaiduToken(baiduToken);
//				}
				if(timePre > BAIDU_TOKEN_TIME_20_DAY){
					baiduToken = getBaiduToken(baiduToken);
				}
			}else{
				baiduToken = getBaiduToken(baiduToken);
			}
			if(!StringUtil.isNullOrEmpty(baiduToken)){
				return "http://tsn.baidu.com/text2audio" + "?" + "tex=" + URLEncoder.encode(strRead, "UTF-8") + "&lan=" + "zh" + "&tok=" + baiduToken + "&ctp=" + "1" + "&cuid=" + AccountManager.getAccountManger().getRegisterInfo().getDeviceId() 
						+ "&spd=" + speed + "&pit=" + "5" + "&vol=" + "5" + "&per=" +  "0";
			}
			return null;
		}
		
		private String getBaiduToken(String baiduToken) {
			String baiduTokenUrl = "https://openapi.baidu.com/oauth/2.0/token";
			Map<String, String> baiduTokens = new HashMap<String, String>();
			baiduTokens.put("grant_type", GRANT_TYPE);
			baiduTokens.put("client_id", KEY_API);
			baiduTokens.put("client_secret", SECRET_API);
			JSONObject tokenObject = HttpManager.getDefaultProvider().post(baiduTokenUrl, baiduTokens);
			if(tokenObject != null){
				String obj = tokenObject.getString("body");
				JSONObject jsonObject = JSONObject.parseObject(obj);
				baiduToken = jsonObject.getString("access_token");
				preference.putString(IPreferenceKey.P_KEY_BAIDU_TOKEN, baiduToken);
				preference.putLong(IPreferenceKey.P_KEY_BAIDU_TOKEN_TIME, System.currentTimeMillis());
			}
			return baiduToken;
		}
	}
}
