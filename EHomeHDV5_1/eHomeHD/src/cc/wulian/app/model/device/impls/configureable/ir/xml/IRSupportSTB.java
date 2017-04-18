package cc.wulian.app.model.device.impls.configureable.ir.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.STBParse;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.STBParse.Box;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.STBParse.STB;

public final class IRSupportSTB {
	private static IRSupportSTB mInstance = null;

	public static IRSupportSTB getInstance(Context context) {
		if (mInstance == null) {
			synchronized (IRSupportSTB.class) {
				if (mInstance == null) {
					mInstance = new IRSupportSTB(context);
				}
			}
		}
		return mInstance;
	}

	private List<STB> mSTBs;
	private Map<String, STB> mSTBByArea;
	private List<Box> mSTBBoxs;

	private IRSupportSTB(Context context) {
		InputStream in = null;
		try {
			STBParse mSTBParse = new STBParse();
			in = context.getResources().openRawResource(R.raw.stb);
			List<STB> stbs = mSTBParse.startParse(in);

			if (mSTBByArea == null)
				mSTBByArea = new HashMap<String, STB>();
			final Map<String, STB> stbByArea = mSTBByArea;

			if (mSTBBoxs == null)
				mSTBBoxs = new ArrayList<Box>();

			for (STB stb : stbs) {
				List<Box> boxs = stb.getBoxs();
				if (boxs != null)
					mSTBBoxs.addAll(boxs);

				String areaID = stb.getAreaID();
				stbByArea.put(areaID, stb);
			}
			mSTBs = stbs;
		} catch (NotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * all STB in list, can not add,addAll,remove,clear,set
	 */
	public List<STB> getSupportSTBsList() {
		if (mSTBs == null)
			mSTBs = new ArrayList<STB>();
		// return Collections.unmodifiableList(mSTBs);
		return mSTBs;
	}

	/**
	 * return STB list group by area id, can not add,addAll,remove,clear,set
	 */
	public Map<String, STB> getSupportSTBsMap() {
		if (mSTBByArea == null)
			mSTBByArea = new HashMap<String, STB>();
		return Collections.unmodifiableMap(mSTBByArea);
	}

	/**
	 * return spectial area id STB
	 */
	public STB getSupportSTBsInArea(String areaID) {
		return getSupportSTBsMap().get(areaID);
	}

	/**
	 * return all STB Box, can not add,addAll,remove,clear,set
	 */
	public List<Box> getSupportSTBBoxs() {
		if (mSTBBoxs == null)
			mSTBBoxs = new ArrayList<Box>();
		// return Collections.unmodifiableList(mSTBBoxs);
		return mSTBBoxs;
	}

	/**
	 * return stb box with box code
	 */
	public Box getSupportBoxByCode(String code) {
		List<Box> boxs = getSupportSTBBoxs();
		Box find = null;
		for (Box box : boxs) {
			int index = box.getCodes().indexOf(code);
			if (index != -1) {
				find = box;
				break;
			}
		}
		return find;
	}
}
