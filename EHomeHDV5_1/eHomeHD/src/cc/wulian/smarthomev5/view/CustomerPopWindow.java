package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;
import cc.wulian.smarthomev5.R;

public class CustomerPopWindow {

    /** 显示网络异常的提示框 **/
    private static PopupWindow popupWindow;

    /**
     * <h1>隐藏提示框</h1>
     */
    public static void dismisPopopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * <h1>x显示自定义view的popupwindow</h1>
     * 
     * @param v
     *            target界面
     * @param c
     *            上下文
     * @param res
     *            自定义veiw的资源文件
     */

    public static  synchronized void showCustomPopopupWindow(final View v, final Context c,
            final View res) {
        if (popupWindow != null && popupWindow.isShowing()) {
            dismisPopopupWindow();
            return;
        }
        popupWindow = new PopupWindow(c);
        popupWindow.setBackgroundDrawable(c.getResources().getDrawable(
                R.drawable.popwindow_bg));
        /**
         * 指定popupwindow的宽和高
         */
        popupWindow.setWidth(v.getWidth());
        popupWindow.setHeight(500);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(res);
        popupWindow.showAsDropDown(v);
    }
    
   public static PopupWindow  getPopupWindow(){
       return popupWindow;
   }
}
