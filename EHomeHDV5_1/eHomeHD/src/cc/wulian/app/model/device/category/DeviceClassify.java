package cc.wulian.app.model.device.category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DeviceClassify
{
	String[] devTypes() default {};

	Category category() default Category.C_OTHER;
}