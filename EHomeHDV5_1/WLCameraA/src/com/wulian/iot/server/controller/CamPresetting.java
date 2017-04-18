package com.wulian.iot.server.controller;
import java.util.List;
import com.wulian.iot.bean.PresettingModel;
import com.wulian.iot.view.manage.PresettingManager.Smit406_Pojo_Item;

import android.content.Context;
public interface CamPresetting {
	public List<PresettingModel>findPresettingListAll(String path,List<Smit406_Pojo_Item> smit406_Pojo_Items);
	public List<PresettingModel>getDefaultPresettingList();
	public PresettingModel getDefaultPresettingMode();
	public List<PresettingModel> groupPresettingList(List<PresettingModel> pModels);

}
