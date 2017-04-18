package cc.wulian.app.model.device.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.smarthomev5.utils.TargetConfigure;
import dalvik.system.DexFile;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class ClassUtil
{

    public static final String TAG = "ClassUtil";

    public static List<Class<? extends WulianDevice>> getAllFromDevice(Context context, Class<WulianDevice> whichInterface ){
		List<Class<? extends WulianDevice>> allFromDevice = new ArrayList<Class<? extends WulianDevice>>();

		if (!whichInterface.isInterface()) return allFromDevice;

		ClassLoader classLoader = whichInterface.getClassLoader();
		String pkgName = whichInterface.getPackage().getName();
		try{
            List<String> findClasseNames = findAllClasseNameInPackage(context, pkgName);
            for(String className : findClasseNames){
                Class clazz = Class.forName(className, true, classLoader);

                int modify = clazz.getModifiers();
				// filter interface class, abstract class and DeviceWraper
				if (Modifier.isInterface(modify) 
				 || Modifier.isAbstract(modify) 
				 || Modifier.isFinal(modify)) continue;

				// do not add interface self
				if (whichInterface.equals(clazz)) continue;
				
				// is impl from Device or extend AbstractDevice
				if(whichInterface.isAssignableFrom(clazz)){
					if(clazz.isAnnotationPresent(DeviceClassify.class))
						allFromDevice.add(clazz);
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		catch (Exception e) {
            if(TargetConfigure.LOG_LEVEL <= Log.ERROR) {
            	Log.e(TAG, "", e);
            }

		}
		return allFromDevice;
	}

	/**
	 * 取得当前类路径下的所有类
	 *
	 * @param cls
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static List<Class<?>> getClasses(Class<?> cls) throws IOException,
			ClassNotFoundException {
		String pk = cls.getPackage().getName();
		String path = pk.replace('.', '/');
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URL url = classloader.getResource(path);
		return getClasses(new File(url.getFile()), pk);
	}


	/**
	 * 迭代查找类
	 *
	 * @param dir
	 * @param pk
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> getClasses(File dir, String pk) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!dir.exists()) {
			return classes;
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				classes.addAll(getClasses(f, pk + "." + f.getName()));
			}
			String name = f.getName();
			if (name.endsWith(".class")) {
				classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
			}
		}
		return classes;
	}

	private static List<String> findAllClasseNameInPackage( Context context, String pkgName ) throws IOException{
		String codePath = context.getPackageCodePath();
		DexFile dexfile = new DexFile(codePath);
		Enumeration<String> entries = dexfile.entries();
		List<String> classNamesInPkg = new ArrayList<String>();

		if (entries == null) return classNamesInPkg;

		while (entries.hasMoreElements())
		{
			String name = entries.nextElement();
            Log.d(TAG, name);
			// infilter other package class name and internal class
			if(name.startsWith(pkgName) && !name.contains("$")){
				classNamesInPkg.add(name);
			}
		}
		return classNamesInPkg;
	}
}