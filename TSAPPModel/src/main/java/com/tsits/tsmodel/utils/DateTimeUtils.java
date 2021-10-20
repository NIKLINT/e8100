package com.tsits.tsmodel.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by minif on 2016/12/2.
 */

public class DateTimeUtils {
    /**
     * 是否是今天
     *
     * @param date
     * @return
     */
    public static boolean isToday(final Date date) {
        Date today = Calendar.getInstance().getTime();
        if (today.getYear()== date.getYear() && today.getMonth()== date.getMonth() && today.getDay()== date.getDay()){
            return true;
        }else {
            return  false;
        }
    }

    /**
     * 获取指定时间的那天 00:00:00.000 的时间
     *
     * @param date
     * @return
     */
    public static Date dayBegin(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    /**
     * 获取指定时间的那天 23:59:59.999 的时间
     *
     * @param date
     * @return
     */
    public static Date dayEnd(final Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    public static String transferLongToDate(SimpleDateFormat sf, Long millSec){
        Date date= new Date(millSec);
        return sf.format(date);
    }
}
