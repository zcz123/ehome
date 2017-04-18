package com.wulian.iot.server.controller.logic;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.wulian.icam.R;
import com.wulian.iot.bean.PresettingModel;
import com.wulian.iot.server.controller.CamPresetting;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.manage.PresettingManager.Smit406_Pojo_Item;
@SuppressLint("NewApi")
public class CamPresettingLogicImpl  implements CamPresetting{
	private static String TAG = "CamPresettingLogicImpl";
	private Context mContext = null;
	public CamPresettingLogicImpl(){
	}
	public CamPresettingLogicImpl(Context context){
		this.mContext =context;
	}
	private List<PresettingModel> defaultPresetting(List<Smit406_Pojo_Item> smit406_Pojo_Items){
		 List<PresettingModel> presettingModels = Collections.synchronizedList(new ArrayList<PresettingModel>());
		for(Smit406_Pojo_Item obj:smit406_Pojo_Items){
			if(!obj.getName().equals("")){
				presettingModels.add(new PresettingModel(obj.getName(), defaultDrawable(),false,Integer.valueOf(obj.getLocation())));
			}
		}
		return presettingModels;
	}
	private Drawable defaultDrawable(){
		return mContext.getResources().getDrawable(R.drawable.iot_preset_default);
	}
	private List<PresettingModel> defaultPresetting(String path,File [] files,List<Smit406_Pojo_Item> smit406_Pojo_Items){
		boolean isEquals =true ;
		List<Integer> index = new ArrayList<Integer>();
		Drawable mDrawable = null;
		Bitmap mBitmap = null;
		List<PresettingModel> presettingModels = new ArrayList<PresettingModel>();
		for(File obj:files){
			int rotateIndex = IotUtil.cutOutStringForInt(IotUtil.wipeOutSuffix(obj.getName()));//获取本地位置
			int itemIndex = rotateIndex-1;
			Smit406_Pojo_Item smItem = smit406_Pojo_Items.get(itemIndex);//获取网关来的位置信息
			if(!smItem.getLocation().equals("")) {
				if (Integer.valueOf(smItem.getLocation()) == rotateIndex && !smItem.getName().equals("")) {
					mBitmap = IotUtil.pathImage(path + "/" + obj.getName());
					if (mBitmap != null) {//本地存在
						mDrawable = IotUtil.bitmapToDrawble(mBitmap, mContext);
						index.add(rotateIndex);
						presettingModels.add(new PresettingModel(smItem.getName(), mDrawable, false, Integer.valueOf(smItem.getLocation())));
					}
				}
			}
		}
			for(Smit406_Pojo_Item sItem:smit406_Pojo_Items){
				if(!sItem.getName().equals("")){
					for(Integer obj: index){
						if(sItem.getLocation().equals(String.valueOf(obj))){
							isEquals = false;
						}
					}
					if(isEquals){
						presettingModels.add(new PresettingModel(sItem.getName(), defaultDrawable(),false,Integer.valueOf(sItem.getLocation())));
					}
					isEquals = true;
				}
			}			
		return presettingModels;
	}
	@Override
	public List<PresettingModel> findPresettingListAll(String path,List<Smit406_Pojo_Item> smit406_Pojo_Items) {
		if(path!=null){
			File [] files = null;
			if(smit406_Pojo_Items == null){
				return getDefaultPresettingList();
			}
			files = IotUtil.getFiles(path);
			if(files!=null){
				if(files.length == 0){
					//本地文件为空
					return groupPresettingList(defaultPresetting(smit406_Pojo_Items));
					}
				return groupPresettingList(defaultPresetting(path,files,smit406_Pojo_Items));
			}
			//文件获取失败直接获取网关数据
			return groupPresettingList(defaultPresetting(smit406_Pojo_Items));
		}
		return null;
	}
	@Override
	public List<PresettingModel> getDefaultPresettingList() {
		List<PresettingModel> presettingModels = new ArrayList<PresettingModel>();
		int j;
		int count = 4;
		for(j=0;j<count;j++){
			presettingModels.add(getDefaultPresettingMode());
		}
		return presettingModels;
	}
	@Override
	public PresettingModel getDefaultPresettingMode() {
		return PresettingModel.defaultData(mContext);
	}
	@Override
	public List<PresettingModel> groupPresettingList(
			List<PresettingModel> pModels) {
		List<PresettingModel> presettingModels = getDefaultPresettingList();
		for(int j=0;j<pModels.size();j++){
			presettingModels.remove(pModels.get(j).getRotateIndex()-1);
			presettingModels.add(pModels.get(j).getRotateIndex()-1, pModels.get(j));
		}
		return presettingModels;
	}
}
