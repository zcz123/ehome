package cc.wulian.h5plus.feature;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.webkit.JavascriptInterface;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.view.H5PlusWebView;

/**
 * Created by Administrator on 2015/12/15.
 */

/**
 * 原生与html之间的沟通桥梁
 */
public class Bridge{

    private H5PlusWebView htmlWebView;

    public Bridge(H5PlusWebView htmlWebView) {
        this.htmlWebView = htmlWebView;
    }

    @JavascriptInterface
    public void exec(final String interfaceName, final String methodName, final String param) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object feature = Engine.getFeature(interfaceName);
                if (feature != null) {
                    try {
                        Method method = getMethodByClassAndName(feature, methodName);
                        Object[] args = new Object[]{htmlWebView, param};
                        method.invoke(feature, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @JavascriptInterface
    public String execSync(String interfaceName, String methodName, String param) {
        try {
            Object feature = Engine.getFeature(interfaceName);
            if (feature != null) {
                Method method = getMethodByClassAndName(feature, methodName);
                Object[] args = new Object[]{htmlWebView, param};
                return (String) method.invoke(feature, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取Feature 中被JavascriptInterface 注释的的函数
    @SuppressLint("NewApi")
	public Method getMethodByClassAndName(Object feature, String methodName) {
        Method targetMethod = null;
        Method[] methods = feature.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(JavascriptInterface.class) && methodName.equals(method.getName())) {
                targetMethod = method;
                break;
            }
        }
        return targetMethod;
    }
}
