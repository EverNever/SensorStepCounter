package com.chao.stepcounter;

import java.util.Calendar;

/**
 * Created by chao on 2015/11/25.
 */
public class Util {
    /**
     * 获取当天0点时间戳
     * @return 当天0点时间戳
     */
    public static long getTimesMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }
}
