package com.wulian.iot.cdm.factory;

import com.wulian.iot.cdm.AbstractFactory;
import com.wulian.iot.cdm.product.ConcreateDeskProduct;
import com.wulian.iot.cdm.product.ConcreateEagleProduct;
import com.wulian.iot.cdm.product.I_Desk_Product;
import com.wulian.iot.cdm.product.I_Eagle_Product;

public class ConcreateCameraFactory implements AbstractFactory{
	@Override
	public I_Desk_Product deskFactory() {
		return new ConcreateDeskProduct();
	}

	@Override
	public I_Eagle_Product eagleFactory() {
		return new ConcreateEagleProduct();
	}
}
