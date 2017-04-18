package cc.wulian.app.model.device.impls.configureable.ir.xml.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.text.TextUtils;
import android.util.Xml;

public class BrandParse
{
	private static final String BRAND_NODE_ID = "id";
	private static final String BRAND_NODE_NAME = "name";
	private static final String BRAND_NODE_CODES = "codes";
	private static final String BRAND_NODE_CODE = "code";

	public static class ACBrand
	{
		private String mID;
		private String mName;
		private List<String> mCodes;

		public String getID(){
			return mID;
		}

		public void setID( String iD ){
			mID = iD;
		}

		public String getName(){
			return mName;
		}

		public void setName( String name ){
			mName = name;
		}

		public List<String> getCodes(){
			return mCodes;
		}

		public void addCode( String code ){
			if (mCodes == null) mCodes = new ArrayList<String>();
			mCodes.add(code);
		}

		@Override
		public int hashCode(){
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mID == null) ? 0 : mID.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ){
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ACBrand other = (ACBrand) obj;
			if (getID() == null){
				if (other.mID != null) return false;
			}
			else if (!getID().equals(other.mID)) return false;
			return true;
		}

		@Override
		public String toString(){
			return mName == null ? super.toString() : mName;
		}
	}

	public BrandParse()
	{
	}

	public List<BrandParse.ACBrand> startParse( InputStream in ){
		List<BrandParse.ACBrand> brands = new ArrayList<BrandParse.ACBrand>();
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "utf-8");

			int type;
			while ((type = parser.next()) != XmlPullParser.START_TAG
					&& type != XmlPullParser.END_DOCUMENT){
				// Empty
			}

			if (type != XmlPullParser.START_TAG) return brands;

			rParseBrand(parser, brands);
		}
		catch (XmlPullParserException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return brands;
	}

	private void rParseBrand( XmlPullParser parser, List<ACBrand> brands )
			throws XmlPullParserException, IOException{

		final int depth = parser.getDepth();
		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
				&& type != XmlPullParser.END_DOCUMENT){

			if (type != XmlPullParser.START_TAG) continue;

			ACBrand brand = new ACBrand();
			rParse(parser, brand);
			brands.add(brand);
		}
	}

	private void rParse( XmlPullParser parser, ACBrand brand ) throws XmlPullParserException,
			IOException{
		final int depth = parser.getDepth();
		int type;
		while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
					&& type != XmlPullParser.END_DOCUMENT){

			if (type != XmlPullParser.START_TAG) continue;

			final String name = parser.getName();
			if (BRAND_NODE_NAME.equals(name)){
				brand.setName(parser.nextText());
			}
			else if (BRAND_NODE_ID.equals(name)){
				brand.setID(parser.nextText());
			}
			else if (BRAND_NODE_CODES.equals(name)){
				rParse(parser, brand);
			}
			else if (BRAND_NODE_CODE.equals(name)){
				String code = parser.nextText();
				splitCompoundCode(code, brand);
			}
		}
	}

	private void splitCompoundCode( String parseCode, ACBrand brand ){
		if (TextUtils.isEmpty(parseCode)) return;

		String[] splitCode = parseCode.split("-");
		int length = splitCode.length;

		if (length == 1){
			brand.addCode(parseCode);
			return;
		}
		
		if(length == 2) {
			Integer startCode = Integer.valueOf(splitCode[0]);
			Integer endCode = Integer.valueOf(splitCode[1]);
			
			while (startCode < endCode + 1){
				brand.addCode(String.format("%03d", startCode));
				startCode++;
			}
		}
	}
}
