package com.wulian.iot.cdm;

import com.wulian.iot.cdm.product.I_Desk_Product;
import com.wulian.iot.cdm.product.I_Eagle_Product;

public interface AbstractFactory {
    /**
     * 桌面摄像机工厂
     * @return 
     */
	public I_Desk_Product deskFactory();
	/**
	 * 鹰眼工厂
	 * @return
	 */
	public I_Eagle_Product eagleFactory();
}
