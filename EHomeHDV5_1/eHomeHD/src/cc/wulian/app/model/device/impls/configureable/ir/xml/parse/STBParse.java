package cc.wulian.app.model.device.impls.configureable.ir.xml.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Xml;

public class STBParse
{
	private static final String STB_NODE_AREA_ID = "area_id";
	private static final String STB_NODE_AREA_NAME = "area_name";
	private static final String STB_NODE_BOX = "box";
	private static final String STB_NODE_CODE = "code";
	private static final String STB_NODE_NAME = "name";

	public static class STB
	{
		public static final String DEFAULT_AREA_ID = "35";

		private String mAreaID;
		private String mAreaName;
		private List<Box> mBoxs;

		public String getAreaID(){
			return mAreaID;
		}

		public void setAreaID( String areaID ){
			mAreaID = areaID;
		}

		public String getAreaName(){
			return mAreaName;
		}

		public void setAreaName( String areaName ){
			mAreaName = areaName;
		}

		public List<Box> getBoxs(){
			return mBoxs;
		}

		public void addBox( Box box ){
			if (mBoxs == null) mBoxs = new ArrayList<Box>();
			mBoxs.add(box);
		}
		
		@Override
		public boolean equals( Object o ){
			if(o instanceof STB) {
				STB target = (STB) o;
				return TextUtils.equals(getAreaID(), target.getAreaID())
						&& TextUtils.equals(getAreaName(), target.getAreaName())
						&& getBoxs().equals(target.getBoxs());
			}
			return false;
		}

		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mAreaID == null) ? 0 : mAreaID.hashCode());
			result = prime * result + ((mAreaName == null) ? 0 : mAreaName.hashCode());
			result = prime * result + ((mBoxs == null) ? 0 : mBoxs.hashCode());
			return result;
		}
	}

	public static class Box
	{
		private List<String> mCodes;
		private String mName;
		private STB mSTB;

		public List<String> getCodes(){
			return mCodes;
		}

		public void addCode( String code ){
			if (mCodes == null) mCodes = new ArrayList<String>();
			mCodes.add(code);
		}

		public String getName(){
			return mName;
		}

		public void setName( String name ){
			mName = name;
		}

		public STB getSTB(){
			return mSTB;
		}

		public void setSTB( STB sTB ){
			mSTB = sTB;
		}

		@Override
		public boolean equals( Object o ){
			if (o instanceof Box){
				Box targetBox = (Box) o;
				return getCodes().equals(targetBox.getCodes())
						&& TextUtils.equals(getName(), targetBox.getName());
			}
			return false;
		}

		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mCodes == null) ? 0 : mCodes.hashCode());
			result = prime * result + ((mName == null) ? 0 : mName.hashCode());
			return result;
		}
	}

	public STBParse()
	{
	}

	public List<STB> startParse( InputStream in ){
		List<STB> stbs = new ArrayList<STB>();
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "utf-8");

			int type;
			while ((type = parser.next()) != XmlPullParser.START_TAG
					&& type != XmlPullParser.END_DOCUMENT){
				// Empty
			}

			if (type != XmlPullParser.START_TAG) return stbs;

			rParseSTB(parser, stbs);
		}
		catch (XmlPullParserException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return stbs;
	}

	private void rParseSTB( XmlPullParser parser, List<STB> stbs )
			throws XmlPullParserException, IOException{

		final int depth = parser.getDepth();
		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
				&& type != XmlPullParser.END_DOCUMENT){

			if (type != XmlPullParser.START_TAG) continue;

			STB stb = new STB();
			rParse(parser, stb);
			stbs.add(stb);
		}
	}

	private void rParse( XmlPullParser parser, STB stb ) throws XmlPullParserException,
			IOException{
		final int depth = parser.getDepth();
		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
					&& type != XmlPullParser.END_DOCUMENT){

			if (type != XmlPullParser.START_TAG) continue;

			final String name = parser.getName();
			if (STB_NODE_AREA_ID.equals(name)){
				stb.setAreaID(parser.nextText());
			}
			else if (STB_NODE_AREA_NAME.equals(name)){
				stb.setAreaName(parser.nextText());
			}
			else if (STB_NODE_BOX.equals(name)){
				Box box = new Box();
				box.setSTB(stb);
				rParseBox(parser, box);
				stb.addBox(box);
			}
		}
	}

	private void rParseBox( XmlPullParser parser, Box box )
			throws XmlPullParserException, IOException{
		final int depth = parser.getDepth();
		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
				&& type != XmlPullParser.END_DOCUMENT){

			if (type != XmlPullParser.START_TAG) continue;

			final String name = parser.getName();
			if (STB_NODE_CODE.equals(name)){
				box.addCode(parser.nextText());
			}
			else if (STB_NODE_NAME.equals(name)){
				box.setName(parser.nextText());
			}
		}
	}
}
