package com.bosnet.ngemart.libgen;

import android.os.Build;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by luis on 10/5/2016.
 * Purpose :
 */

public class DateUtil {

    public class DateformatConstants{
        public static final String AGENT_DATE_FORMAT = "dd/MM/yyyy HH:mm";
        public static final String AGENT_DATE_ONLY = "dd/MM/yyyy";
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static int DaysBetween(Date day1, Date day2) throws Exception {
        DateTime dt1 = new DateTime(day1);
        DateTime dt2 = new DateTime(day2);
        return Days.daysBetween(dt2, dt1).getDays();
    }

    public static long SecondBetween(Date date1 , Date date2) {

        return  TimeUnit.MILLISECONDS.toSeconds(date2.getTime()-date1.getTime());
    }

    public static long GetMinDate()
    {
        long minDate;
        if (Build.VERSION.SDK_INT <= 20) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String[] setDate = currentDate.split("-");
            int year = Integer.parseInt(setDate[0]), month = Integer.parseInt(setDate[1]), day = Integer.parseInt(setDate[2]);
            Calendar cal = Calendar.getInstance();
            if (month == 1) {
                month = 12;
            } else {
                month = month - 1;
            }
            cal.set(year, month, day, 0, 0, 0);
            minDate = cal.getTimeInMillis();
        }
        else
        {
           minDate = System.currentTimeMillis() - 1000;
        }
        return minDate;
    }

    public static boolean IsNeedUpdate(Date date) throws Exception{
        if (date == null)
            return true;

        final int diffDay = DateUtil.DaysBetween(new Date(), date);
        return diffDay > 0;
    }


    public static String DateToStringFormat(String dateFormat, Date date){
        // string format sample = "dd/MM/yyyy HH:mm"
        SimpleDateFormat simpleDate =  new SimpleDateFormat(dateFormat);

        return simpleDate.format(date);
    }

    public static String DateToStringFormat(String dateFomat, Date date, Locale customerLocale){
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFomat, customerLocale);
        return dateFormatter.format(date);
    }

    public static Date StringToDate(String dateValue, String dateFormat){
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = formatter.parse(dateValue);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
