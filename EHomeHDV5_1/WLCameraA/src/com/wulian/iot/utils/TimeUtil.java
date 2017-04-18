package com.wulian.iot.utils;

import android.os.Handler;
import android.os.Message;

public class TimeUtil {
	
	
	//录像时计时工具add by 李凯

	private int hour1 = 0;
	private int hour2 = 0;
	private int minute1 = 0;
	private int minute2 = 0;
	private int second1 = 0;
	private int second2 = 0;
	private boolean isStart = false;
	public String lastTime;
	
	
	public void addTime(final Handler handler){
		isStart = true;
		new Thread(){
			 @Override
			 public void run(){
				super.run();
				while(isStart){
					try {
						if(second2<9){
							second2++;
						}else{	
							second2=0;
							if(second1<5){
								second1++;
							}else{
								second1=0;
								if(minute2<9){
									minute2++;
								}else{
									minute2=0;
									if(minute1<5){
										minute1++;
									}else{
										minute1=0;
										if(hour2<9){
											hour2++;
										}else{
											hour2=0;
											if(hour1<9){
												hour1++;
											}else{
												hour1=9;
											}
										}
									}
								}
							}
						}
						lastTime = hour1+""+hour2+":"+minute1+""+minute2+":"+second1+""+second2;
						Message msg = Message.obtain();
						msg.what = 2;
						handler.sendMessage(msg);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			 }
		 }.start();
	}
	
	public void stopTime(){
		isStart = false;
		hour1=0;
		hour2=0;
		minute1=0;
		minute2=0;
		second1=0;
		second2=0;
	}

	public int getHour1() {
		return hour1;
	}

	public void setHour1(int hour1) {
		this.hour1 = hour1;
	}

	public int getHour2() {
		return hour2;
	}

	public void setHour2(int hour2) {
		this.hour2 = hour2;
	}

	public int getMinute1() {
		return minute1;
	}

	public void setMinute1(int minute1) {
		this.minute1 = minute1;
	}

	public int getMinute2() {
		return minute2;
	}

	public void setMinute2(int minute2) {
		this.minute2 = minute2;
	}

	public int getSecond1() {
		return second1;
	}

	public void setSecond1(int second1) {
		this.second1 = second1;
	}

	public int getSecond2() {
		return second2;
	}

	public void setSecond2(int second2) {
		this.second2 = second2;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	
	
}
