package com.wulian.iot.utils;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by syf on 2016/10/24.
 */
public class IOTCTimer {
    //更具具体时间计算时间
    public void startMoreTime(final int times){
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (timerCallback != null)
                    timerCallback.callback();
            }
        };
        timer.schedule(timerTask, times);
    }

    public void setTimerCallback(TimerCallback timerCallback) {
        this.timerCallback = timerCallback;
    }

    private TimerCallback timerCallback = null;
    public interface TimerCallback {
        void callback();
    }
}
