package cc.wulian.app.model.device.impls.controlable.cooker;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class DeviceNewDoorLockAccountTemporaryTimeView extends LinearLayout {
    private String[] yearArray = new String[31];
    private String[] monthArray = {"01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11", "12"};
    private String[] dayArray = {"01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31"};
    private String[] hoursArray = {"00", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23"};
    private String[] minsArray = {"00", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28",
            "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59"};
//private String[] minsArray = {"00","10", "20", "30","40","50","00","10", "20", "30","40","50","00","10", "20", "30","40","50","00","10", "20", "30","40","50"};
    private Context mContext;
    private WheelView mYears = null;
    private WheelView mMonths = null;
    private WheelView mHours = null;
    private WheelView mMins = null;
    private WheelView mDays = null;
    private NumberAdapter yearAdapter;
    private NumberAdapter monthAdapter;
    private NumberAdapter dayAdapter;
    private NumberAdapter hourAdapter;
    private NumberAdapter minAdapter;
    private int year, month, day, hour, minues;
    int i = 0;
    private String data[] = null;

    public DeviceNewDoorLockAccountTemporaryTimeView(Context context,String data[]) {
        super(context);
        this.mContext = context;
        this.data = data;
        inflate(mContext,
                R.layout.device_new_door_lock_account_temporary_end_time_view,
                this);
        for (int i = 0; i < 31; i++) {
            yearArray[i] = String.valueOf(2017 + i);
        }
        mYears = (WheelView) findViewById(R.id.wheel1);
        mMonths = (WheelView) findViewById(R.id.wheel2);
        mDays = (WheelView) findViewById(R.id.wheel3);
        mHours = (WheelView) findViewById(R.id.wheel4);
        mMins = (WheelView) findViewById(R.id.wheel5);
        mYears.setScrollCycle(true);
        mMonths.setScrollCycle(true);
        mDays.setScrollCycle(true);
        mHours.setScrollCycle(true);
        mMins.setScrollCycle(true);

        yearAdapter = new NumberAdapter(yearArray);
        monthAdapter = new NumberAdapter(monthArray);
        dayAdapter = new NumberAdapter(dayArray);
        hourAdapter = new NumberAdapter(hoursArray);
        minAdapter = new NumberAdapter(minsArray);

        mYears.setAdapter(yearAdapter);
        mMonths.setAdapter(monthAdapter);
        mDays.setAdapter(dayAdapter);
        mHours.setAdapter(hourAdapter);
        mMins.setAdapter(minAdapter);
        mYears.setSelection(0, true);
        mMonths.setSelection(Integer.parseInt(data[1])-1, true);
        mDays.setSelection(Integer.parseInt(data[2])-1, true);
        mHours.setSelection(Integer.parseInt(data[3]), true);
        mMins.setSelection(Integer.parseInt(data[4]), true);
        // ((WheelTextView) mYears.getSelectedView()).setTextSize(30);
        // ((WheelTextView) mMonths.getSelectedView()).setTextSize(30);
        // ((WheelTextView) mDays.getSelectedView()).setTextSize(30);
        // ((WheelTextView) mHours.getSelectedView()).setTextSize(30);
        // ((WheelTextView) mMins.getSelectedView()).setTextSize(30);
        // ((WheelTextView) mSeconds.getSelectedView()).setTextSize(30);
        mYears.setOnItemSelectedListener(mListener);
        mMonths.setOnItemSelectedListener(mListener);
        mDays.setOnItemSelectedListener(mListener);
        mHours.setOnItemSelectedListener(mListener);
        mMins.setOnItemSelectedListener(mListener);
        mYears.setUnselectedAlpha(0.5f);
        mMonths.setUnselectedAlpha(0.5f);
        mDays.setUnselectedAlpha(0.5f);
        mHours.setUnselectedAlpha(0.5f);
        mMins.setUnselectedAlpha(0.5f);
    }

    private TosAdapterView.OnItemSelectedListener mListener = new TosAdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(TosAdapterView<?> parent, View view,
                                   int position, long id) {
            // ((WheelTextView) view).setTextSize(30);
            // int index = StringUtil.toInteger(view.getTag().toString());
            // int count = parent.getChildCount();
            // if (index < count - 1) {
            // ((WheelTextView) parent.getChildAt(index + 1)).setTextSize(20);
            // }
            // if (index > 0) {
            // ((WheelTextView) parent.getChildAt(index - 1)).setTextSize(20);
            // }
            year = mYears.getSelectedItemPosition();
            month = mMonths.getSelectedItemPosition();
            day = mDays.getSelectedItemPosition();
            hour = mHours.getSelectedItemPosition();
            minues = mMins.getSelectedItemPosition();
        }

        @Override
        public void onNothingSelected(TosAdapterView<?> parent) {

        }

    };

    private class NumberAdapter extends BaseAdapter {

        int mHeight = 50;
        String[] mData = null;
        String current = null;

        public NumberAdapter(String[] data) {
            mHeight = (int) DisplayUtil.dip2Pix(mContext, mHeight);
            this.mData = data;
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.length : 0;
        }

        @Override
        public String getItem(int arg0) {
            return mData[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WheelTextView textView = null;

            if (null == convertView) {
                convertView = new WheelTextView(mContext);
                convertView.setLayoutParams(new TosGallery.LayoutParams(-1,
                        mHeight));
                textView = (WheelTextView) convertView;
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(5, 5, 30, 20);
                textView.setTextColor(mContext.getResources().getColor(
                        R.color.black));
            }

            String text = mData[position];
            if (null == textView) {
                textView = (WheelTextView) convertView;
            }
            textView.setTextSize(20);
            textView.setText(text);
            return convertView;
        }

    }

    // private String add0( int time ) {
    // String str_m = String.valueOf(time);
    // String str = "00000";
    // str_m = str.substring(0, 5 - str_m.length()) + str_m;
    // return str_m;
    // }


    public String getSettingYearTime() {
        return yearArray[year];
    }


    public String getSettingMonthTime() {
        return monthArray[month];
    }

    public String getSettingDayTime() {
        return dayArray[day];
    }

    public String getSettingHourTime() {
        return hoursArray[hour];
    }

    public String getSettingMinuesTime() {
        return minsArray[minues];
    }
}
