package cc.wulian.smarthomev5.fragment.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

/**
 * Created by Administrator on 2016/10/18.
 */

public class InformationWeatherStatusItem {

    protected Context mContext;
    protected LayoutInflater inflater;
    private WeatherEntity entity;

    private View view;
    private TextView weatherData;
    private TextView weatherAddress;
    private TextView weatherStatus;
    private TextView weatherPM;
    private TextView weatherPMNumber;
    private TextView weatherTemp;
    private ImageView weatherStatusImg;

    public TextView getWeatherAddress() {
        return weatherAddress;
    }

    public TextView getWeatherData() {
        return weatherData;
    }

    public TextView getWeatherPM() {
        return weatherPM;
    }

    public TextView getWeatherPMNumber() {
        return weatherPMNumber;
    }

    public TextView getWeatherStatus() {
        return weatherStatus;
    }

    public ImageView getWeatherStatusImg() {
        return weatherStatusImg;
    }

    public TextView getWeatherTemp() {
        return weatherTemp;
    }

    public View getView() {
        return view;
    }

    public InformationWeatherStatusItem(Context context) {
        this(context, null);
    }

    public InformationWeatherStatusItem(Context context, WeatherEntity entity) {
        this.mContext = context;
        inflater = LayoutInflater.from(this.mContext);
        if (entity != null) {
            this.entity = entity;
        } else {
            entity = new WeatherEntity();
            this.entity = entity;
        }
        initSystemState();
    }

    private void initSystemState() {
        view = inflater.inflate(R.layout.information_weather_status_item, null);
        weatherData = (TextView) view.findViewById(R.id.weather_data_tv);
        weatherAddress = (TextView) view.findViewById(R.id.weather_address_tv);
        weatherStatus = (TextView) view.findViewById(R.id.weather_status_tv);
        weatherPM = (TextView) view.findViewById(R.id.weather_pm_tv);
        weatherPMNumber = (TextView) view.findViewById(R.id.weather_pm_number_tv);
        weatherTemp = (TextView) view.findViewById(R.id.weather_temp_tv);
        weatherStatusImg = (ImageView) view.findViewById(R.id.weather_status_iv);
        changeStatus(entity);
    }

    public void changeStatus(WeatherEntity entity){
        weatherData.setText(entity.getDate());
        weatherAddress.setText(entity.getAddress());
        if(StringUtil.isNullOrEmpty(entity.getAddress())){
            weatherAddress.setVisibility(View.GONE);
        }else{
            weatherAddress.setVisibility(View.VISIBLE);
        }
        String status = "";
        //此方法为了兼容国际化
        switch (entity.getStatus()){
            case "0":
                status = mContext.getResources().getString(R.string.home_the_weather_clear);
                break;
            case "1":
                status = mContext.getResources().getString(R.string.home_the_weather_few_clouds);
                break;
            case "2":
                status = mContext.getResources().getString(R.string.home_the_weather_scattered_clouds);
                break;
            case "3":
                status = mContext.getResources().getString(R.string.home_the_weather_overcast_sky);
                break;
            case "4":
                status = mContext.getResources().getString(R.string.home_the_weather_shower_rain);
                break;
            case "5":
                status = mContext.getResources().getString(R.string.home_the_weather_rain);
                break;
            case "6":
                status = mContext.getResources().getString(R.string.home_the_weather_thunderstorm);
                break;
            case "7":
                status = mContext.getResources().getString(R.string.home_the_weather_snow);
                break;
            case "8":
                status = mContext.getResources().getString(R.string.home_the_weather_mist);
                break;
        }
        weatherStatus.setText(status);
        if(StringUtil.isNullOrEmpty(entity.getPm25())){
            weatherPM.setVisibility(View.GONE);
        }else{
            weatherPM.setVisibility(View.VISIBLE);
        }
        weatherPMNumber.setText(entity.getPm25());
        weatherTemp.setText(entity.getTemp());
        weatherStatusImg.setBackgroundResource(entity.getStatusImg());
    }



}
