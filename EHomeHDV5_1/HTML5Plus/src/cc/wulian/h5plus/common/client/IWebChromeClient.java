package cc.wulian.h5plus.common.client;


import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.yuantuo.customview.ui.WLDialog;

/**
 * Created by Administrator on 2015/11/20.
 */
public class IWebChromeClient extends WebChromeClient{

    // 设置网页加载的进度条
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        // TODO Auto-generated method stub
        super.onProgressChanged(view, newProgress);
    }

    // 获取网页的标题
    @Override
    public void onReceivedTitle(WebView view, String title) {
        // TODO Auto-generated method stub
        super.onReceivedTitle(view, title);
    }

    // JavaScript弹出框
    @Override
    public boolean onJsAlert(WebView view, String url, String message,JsResult result) {
        // TODO Auto-generated method stub
//        return super.onJsAlert(view, url, message, result);
//        Toast.makeText(view.getContext(),message,Toast.LENGTH_SHORT).show();
        final WLDialog.Builder builder = new WLDialog.Builder(view.getContext());
        String strOk=view.getContext().getString(cc.wulian.app.model.device.R.string.device_ok);
        builder.setPositiveButton(strOk).setMessage(message);
        WLDialog mMessageDialog = builder.create();
        mMessageDialog.show();
        result.cancel();    //一定要cancel，否则会出现各种奇怪问题
        return true;
    }

    // JavaScript确认框
    @Override
    public boolean onJsConfirm(WebView view, String url, String message,JsResult result) {
        // TODO Auto-generated method stub
        return super.onJsConfirm(view, url, message, result);
    }

    // JavaScript输入框
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        // TODO Auto-generated method stub
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

}
