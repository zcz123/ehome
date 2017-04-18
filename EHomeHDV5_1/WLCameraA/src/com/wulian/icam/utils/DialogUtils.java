/**
 * Project Name:  iCam
 * File Name:     DialogUtils.java
 * Package Name:  com.wulian.icam.utils
 *
 * @Date: 2014年12月16日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wheel.NumericWheelAdapter;
import com.wheel.WheelView;
import com.wulian.icam.R;
import com.wulian.icam.model.Scene.OnSelectionLisenter;
import com.wulian.icam.model.Scene.SData;
import com.wulian.icam.view.widget.PBWebView;
import com.wulian.routelibrary.utils.LibraryLoger;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Puml
 * @ClassName: DialogUtils
 * @Function: 对话框常用类
 * @Date: 2014年12月16日
 * @email puml@wuliangroup.cn
 */
public class DialogUtils {

    public static Dialog showBarcodeTipDialog(Context mContext) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_alertdialog_barcodetips, null);
        final Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        ((Button) layout.findViewById(R.id.btn_positive))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
        return dialog;
    }

    public static Dialog showCommonInstructionsWebViewTipDialog(
            Context mContext, String title, String name) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_instructions_webview_tip_alertdialog,
                null);
        final Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        final PBWebView wv_info = (PBWebView) layout.findViewById(R.id.wv_info);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        Button bt_dialog_close = (Button) layout
                .findViewById(R.id.bt_dialog_close);
        if (bt_dialog_close != null) {
            bt_dialog_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (wv_info != null) {
                        wv_info.removeAllViews();
                        wv_info.destroy();
                    }
                    dialog.dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(title)) {
            TextView title_tv = (TextView) layout
                    .findViewById(R.id.tv_dialog_title);
            title_tv.setText(title);
        }
        if (!TextUtils.isEmpty(name)) {

            String language = Locale.getDefault().getLanguage();
            String country = Locale.getDefault().getCountry();
            String temp = (language + "_" + country).toLowerCase(Locale
                    .getDefault());
            if (temp.equalsIgnoreCase("zh_cn")
                    || temp.equalsIgnoreCase("pt_br")) {
                wv_info.loadUrl("file:///android_asset/help/" + temp + "/"
                        + name + ".html");
            } else {
                wv_info.loadUrl("file:///android_asset/help/" + "en/" + name
                        + ".html");
            }
//			WebSettings settings = wv_info.getSettings();
//			settings.setTextSize(WebSettings.TextSize.SMALLER);
        }
        return dialog;
    }

    public static Dialog showCommonInstructionsTipDialog(Context mContext,
                                                         String title, String message) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_instructions_tip_alertdialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        Button bt_dialog_close = (Button) layout
                .findViewById(R.id.bt_dialog_close);
        if (bt_dialog_close != null) {
            bt_dialog_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        if (!TextUtils.isEmpty(title)) {
            TextView title_tv = (TextView) layout
                    .findViewById(R.id.tv_dialog_title);
            title_tv.setText(title);
        }
        if (!TextUtils.isEmpty(message)) {
            TextView message_tv = (TextView) layout
                    .findViewById(R.id.tv_dialog_content);
            message_tv.setMovementMethod(new ScrollingMovementMethod());
            message_tv.setText(message);
        }
        return dialog;
    }

    public static Dialog showBarcodeConfigTipDialog(Context mContext, String deviceId) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_barcode_config_tip_alertdialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        dialog.setContentView(layout);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        ImageView image = (ImageView) layout.findViewById(R.id.iv_normal_set_background);
        DeviceType type = DeviceType.getDevivceTypeByDeviceID(deviceId);
        switch (type) {
            case INDOOR:
                image.setImageResource(R.drawable.icon_barcode_guide_pleguin);
                break;
            case INDOOR2:
                image.setImageResource(R.drawable.icon_barcode_guide_pleguin);
                break;
            case DESKTOP_C:
                image.setImageResource(R.drawable.icon_barcode_guide_desk);
                break;
            case NewEagle:
                image.setImageResource(R.drawable.icon_barcode_guide_neweagle);
            default:
        }

        dialog.show();
        changeDialogWidth(dialog, mContext);
        Button bt_dialog_close = (Button) layout
                .findViewById(R.id.bt_dialog_close);
        if (bt_dialog_close != null) {
            bt_dialog_close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        return dialog;
    }

    public static Dialog showCommonTipDialog(Context mContext,
                                             boolean isCancel, String tip, String message, String ok,
                                             final OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_tip_alertdialog, null);
        Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        if (!TextUtils.isEmpty(message)) {
            TextView message_tv = (TextView) layout.findViewById(R.id.tv_info);
            message_tv.setMovementMethod(new ScrollingMovementMethod());
            message_tv.setText(message);
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        // 绑定事件
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    public static Dialog showLocaleTipDialog(Context mContext,
                                             boolean isCancel, String tip, String ok, String cancel,
                                             boolean isMainLand, final OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_locale_alertdialog, null);
        Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        changeDialogWidth(dialog, mContext);

        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        Button cancleBtn = (Button) layout.findViewById(R.id.btn_negative);
        if (!TextUtils.isEmpty(cancel)) {
            cancleBtn.setText(cancel);
        }
        final RadioGroup rg = (RadioGroup) layout.findViewById(R.id.rg_locale);
        if (isMainLand) {
            rg.check(R.id.rb_mainland);
        } else {
            rg.check(R.id.rb_no_mainland);
        }
        // 绑定事件
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(rg);
                }
            }
        });
        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    public static Dialog showCommonDialog(Context mContext, boolean isCancel,
                                          String tip, CharSequence message, String ok, String cancel,
                                          final OnClickListener okOnclick) {
        return showCommonDialog(mContext, isCancel,
                tip, message, ok, cancel,
                okOnclick, okOnclick);
    }

    public static Dialog showCommonDialog(Context mContext, boolean isCancel,
                                          String tip, CharSequence message, String ok, String cancel,
                                          final OnClickListener okOnclick, final OnClickListener cancelOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_alertdialog, null);
        Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        if (!TextUtils.isEmpty(message)) {
            TextView message_tv = (TextView) layout.findViewById(R.id.tv_info);
            message_tv.setText(message);
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        Button cancleBtn = (Button) layout.findViewById(R.id.btn_negative);
        if (!TextUtils.isEmpty(ok)) {
            cancleBtn.setText(cancel);
        }
        // 绑定事件
        if (okOnclick != null) {
            okBtn.setOnClickListener(okOnclick);
        }
        if (cancelOnclick != null) {
            cancleBtn.setOnClickListener(cancelOnclick);
        }
        return dialog;
    }

    public static Dialog showCommonEditDialog(Context mContext,
                                              boolean isCancel, String tip, String ok, String cancle,
                                              String editHint, String editInfo, final OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_edit_alertdialog, null);
        Dialog dialog = new Dialog(mContext, R.style.alertDialog);

        // dialog.getWindow().clearFlags(
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // dialog.getWindow().setSoftInputMode(
        // WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        changeDialogWidth(dialog, mContext);
        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        final EditText infoEt = (EditText) layout.findViewById(R.id.et_input);
        // 获取编辑框焦点
        // infoEt.setFocusable(true);
        // //打开软键盘
        // InputMethodManager imm = (InputMethodManager) mContext
        // .getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
        // imm.showSoftInput(infoEt, InputMethodManager.SHOW_IMPLICIT);
        if (!TextUtils.isEmpty(editHint)) {
            infoEt.setHint(editHint);
        }
        if (!TextUtils.isEmpty(editInfo)) {
            infoEt.setText(editInfo);
            infoEt.setSelection(editInfo.length());
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        Button cancleBtn = (Button) layout.findViewById(R.id.btn_negative);
        if (!TextUtils.isEmpty(cancle)) {
            cancleBtn.setText(cancle);
        }
        // 绑定事件
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(infoEt);
                }
            }
        });
        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    /*
     * defaultTime: 格式是8,30,9,40
     */
    public static Dialog showCommonTimePeriodDialog(Context mContext,
                                                    boolean isCancel, String tip, String ok, String cancle,
                                                    String defaultTime, final OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_timeperiod_alertdialog, null);
        Dialog dialog = new Dialog(mContext, R.style.alertDialog);

        final WheelView start_time_hour = (WheelView) layout
                .findViewById(R.id.start_time_hour);
        final WheelView start_time_min = (WheelView) layout
                .findViewById(R.id.start_time_min);
        final WheelView end_time_hour = (WheelView) layout
                .findViewById(R.id.end_time_hour);
        final WheelView end_time_min = (WheelView) layout
                .findViewById(R.id.end_time_min);

        start_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        start_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        end_time_hour.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        end_time_min.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        start_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        start_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
        end_time_hour.setCurrentItem(calender.get(Calendar.HOUR_OF_DAY));
        end_time_min.setCurrentItem(calender.get(Calendar.MINUTE));
        start_time_hour.setCyclic(true);
        start_time_min.setCyclic(true);
        end_time_hour.setCyclic(true);
        end_time_min.setCyclic(true);

        if (!TextUtils.isEmpty(defaultTime)) {
            String timeNum[] = defaultTime.split(",");
            if (timeNum.length == 4) {
                start_time_hour.setCurrentItem(Integer.parseInt(timeNum[0]),
                        false);
                start_time_min.setCurrentItem(Integer.parseInt(timeNum[1]),
                        false);
                end_time_hour.setCurrentItem(Integer.parseInt(timeNum[2]),
                        false);
                end_time_min
                        .setCurrentItem(Integer.parseInt(timeNum[3]), false);
            }
        }

        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        changeDialogWidth(dialog, mContext);
        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        Button cancleBtn = (Button) layout.findViewById(R.id.btn_negative);
        if (!TextUtils.isEmpty(cancle)) {
            cancleBtn.setText(cancle);
        }
        // 绑定事件
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    String result = start_time_hour.getCurrentItem() + ","
                            + start_time_min.getCurrentItem() + ","
                            + end_time_hour.getCurrentItem() + ","
                            + end_time_min.getCurrentItem();
                    v.setTag(result);
                    okOnclick.onClick(v);
                }
            }
        });
        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    /**
     * @param mContext       上下文
     * @param isCancel       后退键是否取消dialog
     * @param tip            dialog标题
     * @param ok             dialog OK键
     * @param cancle         dialog cancle键
     * @param defaultDayTime 格式是0,1,2,3---->周日，周一，周二，周三，
     * @param dataSource     可选时间周期列表
     * @param okOnclick      传过来的点击事件，用于捆绑dialog中的ok和cancle两个点击事件
     * @return
     * @MethodName: showCommonTimeDayPeriodDialog
     * @Function: 设置周期弹出窗体
     * @author: yuanjs
     * @date: 2015年10月13日
     * @email: jiansheng.yuan@wuliangroup.com
     */

    public static Dialog showCommonTimeDayPeriodDialog(Context mContext,
                                                       boolean isCancel, String tip, String ok, String cancle,
                                                       String defaultDayTime, final List<String> dataSource,
                                                       final OnClickListener okOnclick) {
        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_alertdialog_time_day, null);
        Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        final ListView lv_info = (ListView) layout.findViewById(R.id.lv_info);
        ListAdapter adapter = lv_info.getAdapter();
        if (adapter == null) {
            adapter = new ArrayAdapter<String>(mContext,
                    R.layout.item_time_day, R.id.tv_name, dataSource);
        }
        lv_info.setAdapter(adapter);
        if (!TextUtils.isEmpty(defaultDayTime)) {
            String timeNum[] = defaultDayTime.split(",");
            if (timeNum.length > 0) {
                for (int i = 0; i < timeNum.length; i++) {
                    if (Integer.parseInt(timeNum[i]) == 7) {
                        lv_info.setItemChecked(0, true);
                    } else {
                        lv_info.setItemChecked(Integer.parseInt(timeNum[i]),
                                true);
                    }
                }
            }
        }
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        if (!TextUtils.isEmpty(tip)) {
            TextView title_tv = (TextView) layout.findViewById(R.id.tv_title);
            title_tv.setText(tip);
        }
        Button okBtn = (Button) layout.findViewById(R.id.btn_positive);
        if (!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
        }
        Button cancleBtn = (Button) layout.findViewById(R.id.btn_negative);
        if (!TextUtils.isEmpty(cancle)) {
            cancleBtn.setText(cancle);
        }
        // 绑定事件
        okBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    String result = "";
                    for (int i = 0; i < dataSource.size(); i++) {
                        LibraryLoger.e("" + i, lv_info.getCheckedItemPositions()
                                .get(i) + "");
                        if (lv_info.getCheckedItemPositions().get(i)) {
                            int j = i;
                            j = i == 0 ? 7 : i;
                            result += j + ",";
                        }
                    }
                    LibraryLoger.e("result:", result);
                    v.setTag(result);
                    okOnclick.onClick(v);
                }
            }
        });
        cancleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != okOnclick) {
                    okOnclick.onClick(v);
                }
            }
        });
        return dialog;
    }

    public static class SceneAdapter extends BaseAdapter {

        private List<SData> list;
        private SData tempGridViewItem;
        private LayoutInflater layoutInflater;
        private Context mcontext;

        public SceneAdapter(Context context, List<SData> list) {
            this.list = list;
            this.mcontext = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void refreshAdapter(List<SData> list) {
            if (list == null) {
                return;
            }
            if (this.list != null) {
                this.list.clear();
            }
            this.list.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public SData getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            tempGridViewItem = list.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.icam_item_scene,
                        null);
                viewHolder.btn = (Button) convertView
                        .findViewById(R.id.iv_icon);
                viewHolder.tv = (TextView) convertView.findViewById(R.id.tv_camera_scene_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (tempGridViewItem != null) {
                if (tempGridViewItem.iconNO >= 0
                        && tempGridViewItem.iconNO < 12) {
                    int drawableID = getPic("icam_selector_scene_"
                            + tempGridViewItem.iconNO);
                    if (drawableID != -1) {
                        // btn_scene_new.setBackgroundDrawable(drawable);
                        viewHolder.btn.setBackgroundResource(drawableID);
                        if (tempGridViewItem.status.equals("2")) {
                            viewHolder.btn.setSelected(true);
//                            viewHolder.tv.setTextColor(mcontext.getResources().getColor(R.color.action_bar_bg));
                        } else {
                            viewHolder.btn.setSelected(false);
                        }
                    }
                } else {
                    viewHolder.btn
                            .setBackgroundResource(R.drawable.selector_function_scene);
                }
                if (!TextUtils.isEmpty(tempGridViewItem.title)) {
                    viewHolder.tv
                            .setText(tempGridViewItem.title.length() < 2 ? tempGridViewItem.title
                                    : tempGridViewItem.title.substring(0, 2));
                } else {
                    viewHolder.tv.setText(R.string.common_scene);
                }
            } else {
                viewHolder.btn
                        .setBackgroundResource(R.drawable.selector_function_scene);
                viewHolder.tv.setText(R.string.common_scene);
            }

            return convertView;
        }

        class ViewHolder {
            Button btn;
            TextView tv;
        }
    }

    public static int getPic(String pid) {
        Field f;
        try {
            f = R.drawable.class.getField(pid);
            return f.getInt(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Dialog showCommonGridViewDialog(final Context mContext,
                                                  boolean isCancel, int defaultIdx,
                                                  final OnSelectionLisenter mSelectionListener,
                                                  final OperatorForV5Lisener operatorForV5Lisener,
                                                  final SceneAdapter adapter) {

        View layout = LayoutInflater.from(mContext).inflate(
                R.layout.custom_common_gridview_alertdialog, null);
        final Dialog dialog = new Dialog(mContext, R.style.alertDialog);
        GridView gv_info = (GridView) layout.findViewById(R.id.gridView);
        TextView tv_scene_dismiss = (TextView) layout.findViewById(R.id.tv_scene_dismiss);

        // MyAdapter adapter = new MyAdapter(mContext, dataSource);
        gv_info.setAdapter(adapter);
        tv_scene_dismiss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        gv_info.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (operatorForV5Lisener != null) {
                    operatorForV5Lisener.showProgressDialog();
                    operatorForV5Lisener.requestOverTime();
                }
                if (mSelectionListener != null) {
                    mSelectionListener.onSeleted(position,
                            adapter.getItem(position).tag,
                            adapter.getItem(position).status);
                }
                dialog.dismiss();
            }
        });
        dialog.setContentView(layout);
        dialog.setCancelable(isCancel);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        changeDialogWidth(dialog, mContext);
        return dialog;
    }

    /**
     * 用于v5场景操作接口
     */
    public interface OperatorForV5Lisener {
        void showProgressDialog();// 用于执行过程显示dialog

        void requestOverTime();// 用于超时计算
    }

    /**
     * 设置对话框的宽度
     **/
    public static void changeDialogWidth(Dialog dialog, Context mContext) {
        if (null != dialog) {
            WindowManager wm = (WindowManager) mContext
                    .getSystemService(Context.WINDOW_SERVICE);
            int screenWidth = wm.getDefaultDisplay().getWidth();
            WindowManager.LayoutParams params = dialog.getWindow()
                    .getAttributes();
            int width = mContext.getResources().getDimensionPixelSize(
                    R.dimen.margin_normal);
            params.width = screenWidth - 2 * width;
            dialog.getWindow().setAttributes(params);
        }
    }
}
