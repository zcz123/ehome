package cc.wulian.smarthomev5.fragment.home;

import cc.wulian.smarthomev5.R;

/**
 * Created by Administrator on 2016/10/20.
 */

public class WeatherEntity {
    private String date;
    private String address;
    private String status;
    private String pm25;
    private String temp;
    private int statusImg;

    public WeatherEntity() {
        this("","","","","", 0);
    }

    public WeatherEntity(String date, String address, String status, String pm25, String temp, int statusImg) {
        this.address = address;
        this.date = date;
        this.pm25 = pm25;
        this.status = status;
        this.statusImg = statusImg;
        this.temp = temp;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusImg() {
        return statusImg;
    }

    public void setStatusImg(int statusImg) {
        this.statusImg = statusImg;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public static String[] getWeatherStatus(String type){
        String [] weatherType=new String[2];
        switch (type){
            case"01d":
            case"01n":
                weatherType[0]="0";
                weatherType[1]=""+R.drawable.weather_clear_sky;
                break;
            case"02d":
            case"02n":
                weatherType[0]="1";
                weatherType[1]=""+R.drawable.weather_few_chouds;
                break;
            case"03d":
            case"03n":
                weatherType[0]="2";
                weatherType[1]=""+R.drawable.weather_scattered_clouds;
                break;
            case"04d":
            case"04n":
                weatherType[0]="3";
                weatherType[1]=""+R.drawable.weather_broken_clouds;
                break;
            case"09d":
            case"09n":
                weatherType[0]="4";
                weatherType[1]=""+R.drawable.weather_shower_rain;
                break;
            case"10d":
            case"10n":
                weatherType[0]="5";
                weatherType[1]=""+R.drawable.weather_rain;
                break;
            case"11d":
            case"11n":
                weatherType[0]="6";
                weatherType[1]=""+R.drawable.weather_thunder_storm;
                break;
            case"13d":
            case"13n":
                weatherType[0]="7";
                weatherType[1]=""+R.drawable.weather_snow;
                break;
            case"50d":
            case"50n":
                weatherType[0]="8";
                weatherType[1]=""+R.drawable.weather_mist;
                break;
        }
        return weatherType;
    }

}

