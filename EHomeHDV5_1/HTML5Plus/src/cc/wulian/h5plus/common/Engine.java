package cc.wulian.h5plus.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cc.wulian.h5plus.feature.Device;
import cc.wulian.h5plus.feature.EventListenerFeature;
import cc.wulian.h5plus.feature.UiFeature;
import cc.wulian.h5plus.feature.WebViewFeature;
import cc.wulian.h5plus.feature.ajax.HtmlRequestFeature;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;

/**
 * Created by Administrator on 2015/12/15.
 */
public class Engine {

	public static boolean isStartUp = false;

	private static Map<String, Object> interfacesMap = new HashMap<String, Object>();

	private static Map<H5PlusWebViewContainer, List<H5PlusWebView>> webViewMap = new ConcurrentHashMap<H5PlusWebViewContainer, List<H5PlusWebView>>();

	private static final List<H5PlusWebView> NULL_H5PlusWebViewList = new LinkedList<H5PlusWebView>();

	public synchronized static void startup() {
		if (!isStartUp) {
			parseConfiguredFeatures();
			isStartUp = true;
		}
	}

	public synchronized static void shutdown() {
		for (List<H5PlusWebView> entitys : webViewMap.values()) {
			for (H5PlusWebView entity : entitys) {
				entity.destroy();
			}
		}
		webViewMap.clear();
		isStartUp = false;
	}

	public static H5PlusWebView createWebView(H5PlusWebView parent, String url, String id) {
		H5PlusWebView webView = new H5PlusWebView(parent, url, id);
		bindWebviewToContainer(parent.getContainer(), webView);
		return webView;
	}

	/**
	 * 把webview和他的容器关联起来，并且把webview加入Engine管理。 2016-7-27
	 * 
	 * @author Administrator
	 * @param container
	 * @param webview
	 */
	public static void bindWebviewToContainer(H5PlusWebViewContainer container, H5PlusWebView webview) {
		webview.setContainer(container);

		List<H5PlusWebView> entitys = webViewMap.get(container);
		if (entitys == null) {
			entitys = new ArrayList<H5PlusWebView>();
			webViewMap.put(container, entitys);
		}
		entitys.add(webview);

	}

	public static List<H5PlusWebView> getWebviewList(H5PlusWebViewContainer container) {
		List<H5PlusWebView> ret = webViewMap.get(container);
		if (ret != null) {
			return ret;
		} else {
			return NULL_H5PlusWebViewList;
		}
	}

	public static H5PlusWebView getWebview(H5PlusWebViewContainer container, String webViewId) {
		List<H5PlusWebView> entities = webViewMap.get(container);
		if (entities != null) {
			for (H5PlusWebView webview : entities) {
				if (webview.getWebviewId().equals(webViewId)) {
					return webview;
				}
			}
		}
		return null;
	}

	public static void destroyPager(final H5PlusWebViewContainer activity) {
		if (webViewMap.containsKey(activity)) {
			List<H5PlusWebView> entitys = getWebviewList(activity);
			for (H5PlusWebView entity : entitys) {
				entity.destroy();
			}
			webViewMap.remove(activity);
		}
	}

	public static void onContainerResume(H5PlusWebViewContainer container) {
		for (H5PlusWebView entity : getWebviewList(container)) {
			JsUtil.getInstance().fireJsEventListener(entity, SysEventType.onActivityResume.getValue(), null);
		}
	}

	public static void removeWebview(H5PlusWebView webview) {
		List<H5PlusWebView> entitys = webViewMap.get(webview.getContainer());
		if (entitys != null) {
			entitys.remove(webview);
		}
	}

	/**
	 * 加载用户配置的插件 2016-7-28
	 * 
	 * @author Administrator
	 */
	private static void parseConfiguredFeatures() {
		interfacesMap.put("webView", new WebViewFeature());
		interfacesMap.put("XMLHttpRequest", new HtmlRequestFeature());
		interfacesMap.put("eventListener", new EventListenerFeature());
		interfacesMap.put("device", new Device());
		interfacesMap.put("UiFeature", new UiFeature());
	}

	public static void addFeature(String name, Object featureImp) {
		interfacesMap.put(name, featureImp);
	}

	public static Object getFeature(String name) {
		return interfacesMap.get(name);
	}

}
