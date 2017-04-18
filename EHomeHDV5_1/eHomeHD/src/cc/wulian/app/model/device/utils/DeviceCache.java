package cc.wulian.app.model.device.utils;

import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.IMultiEpDevice;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * Cache All Device By gwID + devID
 */
public class DeviceCache {
    private static final Class<?>[] mConstructorSignature = new Class[]{Context.class, String.class};

    private static DeviceCache mInstance;

    /**
     * key = gwID + devID
     */
    protected final Map<String, WulianDevice> mDeviceInfoMap = new ConcurrentHashMap<String, WulianDevice>();

    protected DeviceCache(Context context) {
    }

    /**
     * make sure only one device cache instance in this application
     */
    public static DeviceCache getInstance(Context context) {
        if (mInstance == null) mInstance = new DeviceCache(context);
        return mInstance;
    }

    /**
     * when device is new create (like onDeviceUp(...))
     */
    public WulianDevice startUpDevice(Context context, DeviceInfo devInfo, Set<DeviceEPInfo> epInfo) {
        devInfo.setDevEPInfoMap(epInfo);
        WulianDevice device = createDevice(context, devInfo, true);
        device.onDeviceUp(devInfo);
        return device;
    }

    /**
     * when device already in cache (like onDeviceData(...))
     * <p>
     * sometimes, device will return its own data before it onDeviceUp(...) we ignore it and did not create it in our device cache
     */
    public WulianDevice updateDevice(Context context, DeviceInfo devInfo, DeviceEPInfo devEPInfo) {
        WulianDevice device = createDevice(context, devInfo, false);
        if (device != null) device.onDeviceSet("", devInfo, devEPInfo);
        return device;
    }

    /**
     * get a device from cache, will be <code><b>null</b></code> if not contains this device.
     */
    public WulianDevice getDeviceByID(Context context, String gwID, String devID) {
        DeviceInfo devInfo = new DeviceInfo();
        devInfo.setGwID(gwID);
        devInfo.setDevID(devID);
        return createDevice(context, devInfo, false);
    }

    /**
     * get a device from cache, will be <code><b>null</b></code> if not contains this device.
     */
    public WulianDevice getDeviceByIDEp(Context context, String gwID, String devID, String ep) {
        DeviceInfo devInfo = new DeviceInfo();
        devInfo.setGwID(gwID);
        devInfo.setDevID(devID);
        WulianDevice device = createDevice(context, devInfo, false);
        if (DeviceUtil.isDeviceCompound(device)) {
            IMultiEpDevice epDevice = (IMultiEpDevice) device;
            WulianDevice child = epDevice.getChildDeviceByEp(ep);
            if (child != null) device = child;
        } else if (device != null && device.getChildDevices() != null && device.getChildDevices().size() > 0) {
            device = device.getChildDevice(ep);
        }
        return device;
    }


    /**
     * get devices from cache by type, will be <code><b>null</b></code> if not contains this device.
     */
    public Collection<WulianDevice> getDeviceByType(String gwID, String type) {
        List<WulianDevice> collection = new ArrayList<WulianDevice>();

        Iterator<WulianDevice> iterator = getAllDevice().iterator();
        while (iterator.hasNext()) {
            WulianDevice old = iterator.next();
            if (TextUtils.equals(gwID, old.getDeviceGwID()) && TextUtils.equals(type, old.getDeviceType())) {
                collection.add(old);
            }
        }
        return Collections.unmodifiableCollection(collection);
    }

    public List<WulianDevice> getDeviceByCategory(String gwID, Category category) {
        List<WulianDevice> categoryDevices = new ArrayList<WulianDevice>();
        Collection<WulianDevice> devices = getAllDevice();
        for (WulianDevice device : devices) {
            if (device != null && StringUtil.equals(device.getDeviceGwID(), gwID) && isCategory(device.getClass(), category)) {
                categoryDevices.add(device);
            }
        }
        return categoryDevices;
    }

    public boolean isCategory(Class<?> clazz, Category category) {
        if (clazz.isAnnotationPresent(DeviceClassify.class)) {
            DeviceClassify classify = clazz.getAnnotation(DeviceClassify.class);
            Category devCategory = classify.category();
            if (devCategory == category) {
                return true;
            }
        }
        return false;

    }

    /**
     * Returns a wrapper on the specified collection which throws an {@code UnsupportedOperationException} whenever an attempt is made to modify the collection.
     */
    public Collection<WulianDevice> getAllDevice() {
        return Collections.unmodifiableCollection(mDeviceInfoMap.values());
    }

    /**
     * Removes all elements from this {@code Map}, leaving it empty.
     */
    public void removeAllDevice() {
        mDeviceInfoMap.clear();
    }

    /**
     * Removes specified gwID device
     */
    public void removeDeviceInGateway(String gwID) {
        Iterator<String> iterator = mDeviceInfoMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith(gwID)) {
                iterator.remove();
            }
        }
    }

    /**
     * Removes specified gwID device with devID
     */
    public void removeDevice(String gwID, String devID) {
        mDeviceInfoMap.remove(gwID + devID);
    }

    /**
     * judge specified gwID & devID in cache
     */
    public boolean containsDevice(String gwID, String devID) {
        return mDeviceInfoMap.containsKey(gwID + devID);
    }

    /**
     * Returns the number of mappings in this cache
     */
    public int size() {
        return mDeviceInfoMap.size();
    }

    /**
     * Returns whether this cache is empty.
     */
    public boolean isEmpty() {
        return mDeviceInfoMap.isEmpty();
    }

    /**
     * create wulian device by type, this will find class in cache
     */
    public WulianDevice createDeviceWithType(Context context, String type) {
        Class<? extends WulianDevice> clazz = getDeviceClass(type);
        return createDeviceWithClass(clazz, context, type);
    }

    /**
     * create wulian device by specialed class
     */
    private WulianDevice createDeviceWithClass(Class<? extends WulianDevice> clazz, Context context, String type) {
        WulianDevice device = null;
        try {
            Constructor<? extends WulianDevice> constructor = clazz.getConstructor(mConstructorSignature);
            device = constructor.newInstance(context, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device;
    }


    private WulianDevice createDevice(Context context, DeviceInfo devInfo, boolean needRenew) {
        String gwID = devInfo.getGwID();
        String devID = devInfo.getDevID();

        // /////////////////////////////////////////
        String devType = devInfo.getType();
        WulianDevice device = mDeviceInfoMap.get(gwID + devID);

        if (needRenew && device == null) {
            // sometimes, getType is return null
            // but, in deviceUp interface this is not null
            // String type = devInfo.getType();
            device = createDeviceWithType(context, devType);
            mDeviceInfoMap.put(gwID + devID, device);
        }
        return device;
    }

    private Class<? extends WulianDevice> getDeviceClass(String key) {
        DeviceResource.ResourceInfo rinfo = DeviceResource.getResourceInfo(key);
        return rinfo.clazz;
    }
}