package cc.wulian.app.model.device.impls.configureable.ir.xml.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class KeyCodeParse
{
	private static final String KEY_NODE_CODE = "code";
	private static final String KEY_NODE_NAME = "name";

	public static class KeyCode{
		private String mCode;
		private String mName;
		
		public String getCode(){
			return mCode;
		}

		public void setCode( String code ){
			mCode = code;
		}

		public String getName(){
			return mName;
		}

		public void setName( String name ){
			mName = name;
		}

		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mCode == null) ? 0 : mCode.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			KeyCode other = (KeyCode) obj;
			if (getCode() == null){
				if (other.mCode != null) return false;
			}
			else if (!getCode().equals(other.mCode)) return false;
			return true;
		}

		@Override
		public String toString(){
			return mName == null ? super.toString() : mName;
		}
	}
	
	public KeyCodeParse()
	{
	}

	public List<KeyCode> startParse( InputStream in ){
		List<KeyCode> keyCodes = new ArrayList<KeyCode>();
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "utf-8");

			int type;
			while ((type = parser.next()) != XmlPullParser.START_TAG
					&& type != XmlPullParser.END_DOCUMENT){
				// Empty
			}

			if (type != XmlPullParser.START_TAG) return keyCodes;

			rParsekeyCode(parser, keyCodes);
		}
		catch (XmlPullParserException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return keyCodes;
	}

	private void rParsekeyCode( XmlPullParser parser, List<KeyCode> keyCodes )
			throws XmlPullParserException, IOException{

		final int depth = parser.getDepth();
		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
				&& type != XmlPullParser.END_DOCUMENT){

			if (type != XmlPullParser.START_TAG) continue;

			KeyCode keyCode = new KeyCode();
			rParse(parser, keyCode);
			keyCodes.add(keyCode);
		}
	}

	private void rParse( XmlPullParser parser, KeyCode keyCode ) throws XmlPullParserException,
			IOException{
		final int depth = parser.getDepth();
		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
					&& type != XmlPullParser.END_DOCUMENT){

			if (type != XmlPullParser.START_TAG) continue;

			final String name = parser.getName();
			if (KEY_NODE_CODE.equals(name)){
				keyCode.setCode(parser.nextText());
			}
			else if (KEY_NODE_NAME.equals(name)){
				keyCode.setName(parser.nextText());
			}
		}
	}
}
