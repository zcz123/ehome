package cc.wulian.app.model.device.impls.configureable.ir.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.BrandParse;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.BrandParse.ACBrand;

public final class IRSupportACBrand {
	private static IRSupportACBrand mInstance = null;

	public static IRSupportACBrand getInstance(Context context) {
		if (mInstance == null) {
			synchronized (IRSupportACBrand.class) {
				if (mInstance == null) {
					mInstance = new IRSupportACBrand(context);
				}
			}
		}
		return mInstance;
	}

	private List<ACBrand> mBrands;

	private IRSupportACBrand(Context context) {
		InputStream in = null;
		try {
			BrandParse mBrandParse = new BrandParse();
			in = context.getResources().openRawResource(R.raw.brand);
			mBrands = mBrandParse.startParse(in);
		} catch (Exception e) {

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
	 * return all Brand, can not add,addAll,remove,clear,set
	 */
	public List<ACBrand> getSupportBrandList() {
		if (mBrands == null)
			mBrands = new ArrayList<BrandParse.ACBrand>();
		// return Collections.unmodifiableList(mBrands);
		return mBrands;
	}

	/**
	 * return special id brand
	 */
	public ACBrand getSupportBrandByID(String id) {
		List<ACBrand> brands = getSupportBrandList();
		ACBrand find = null;
		for (ACBrand brand : brands) {
			if (TextUtils.equals(brand.getID(), id)) {
				find = brand;
				break;
			}
		}
		return find;
	}

	/**
	 * return specital code brand
	 */
	public ACBrand getSupportBrandByCode(String code) {
		List<ACBrand> brands = getSupportBrandList();
		ACBrand find = null;
		for (ACBrand brand : brands) {
			int index = brand.getCodes().indexOf(code);
			if (index != -1) {
				find = brand;
				break;
			}
		}
		return find;
	}
}
