package com.bosnet.ngemart.libgen;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by luis on 8/2/2016.
 * Purpose :
 */
public class Common {


    public static final String HOME_PRODUCT_SEARCH_KEY = "searchProduct";
    public static final String image64String = "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";
    private static final String NUMBER_WO_FRACTION_PATTERN = "#,###";
    private static final String NUMBER_FRACTION_PATTERN = "#,###.##";
    private static final String NUMBER_SIGNED_WO_FRACTION_PATTERN = "#,###;(#,###)";
    private static final String SHORT_TIME_PATTERN = "hh:mm a";
    private static final String SHORT_DATE_TIME_PATTERN = "d MMM yyyy hh:mm a";
    private static final String SHORT_DATE_TIME_NO_YEAR_PATTERN = "d MMM hh:mm a";
    private static final String SHORT_DATE_TIME_NO_YEAR_WITH_DAY_NAME_PATTERN = "EEEE, d MMM hh:mm a";
    private static final String SHORT_DATE_PATTERN = "d MMM yyyy";

    private static DecimalFormat fractionNumberFormat = null;
    private static DecimalFormat nonFractionNumberFormat = null;
    private static DecimalFormat nonFractionSignedNumberFormat = null;

    private static DecimalFormat fractionNumberFormatWithSymbol = null;
    private static DecimalFormat nonFractionNumberFormatWithSymbol = null;
    private static DecimalFormat nonFractionSignedNumberFormatWithSymbol = null;

    private static DecimalFormat getLocaleIDDecimalFormat(String pattern) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        DecimalFormat df = (DecimalFormat)nf;
        df.applyPattern(pattern);
        return df;
    }

    public static DecimalFormat GetNumberFractionFormat(String currencySymbol) {
        if (fractionNumberFormatWithSymbol == null) {
            fractionNumberFormatWithSymbol = getLocaleIDDecimalFormat(currencySymbol + NUMBER_FRACTION_PATTERN);
        }
        fractionNumberFormatWithSymbol.setDecimalSeparatorAlwaysShown(true);
        return fractionNumberFormatWithSymbol;
    }

    public static DecimalFormat GetNumberWithoutFractionFormat(String currencySymbol)
    {
        if (nonFractionNumberFormatWithSymbol == null) {
            nonFractionNumberFormatWithSymbol = getLocaleIDDecimalFormat(currencySymbol + NUMBER_WO_FRACTION_PATTERN);
        }
        return nonFractionNumberFormatWithSymbol;
    }

    public static DecimalFormat GetNumberSignedWithoutFractionFormat(String currencySymbol)
    {
        if (nonFractionSignedNumberFormatWithSymbol == null){
            nonFractionSignedNumberFormatWithSymbol = getLocaleIDDecimalFormat(currencySymbol + NUMBER_SIGNED_WO_FRACTION_PATTERN);
        }
        return nonFractionSignedNumberFormatWithSymbol;
    }

    public static DecimalFormat GetNumberFractionFormat() {
        if (fractionNumberFormat == null) {
            fractionNumberFormat = getLocaleIDDecimalFormat(NUMBER_FRACTION_PATTERN);
        }
        fractionNumberFormat.setDecimalSeparatorAlwaysShown(true);
        return fractionNumberFormat;
    }

    public static DecimalFormat GetNumberWithoutFractionFormat()
    {
        if (nonFractionNumberFormat == null) {
            nonFractionNumberFormat = getLocaleIDDecimalFormat(NUMBER_WO_FRACTION_PATTERN);
        }
        return nonFractionNumberFormat;
    }

    public static DecimalFormat GetNumberSignedWithoutFractionFormat()
    {
        if (nonFractionSignedNumberFormat == null){
            nonFractionSignedNumberFormat = getLocaleIDDecimalFormat(NUMBER_SIGNED_WO_FRACTION_PATTERN);
        }
        return nonFractionSignedNumberFormat;
    }

    public static Date dateAddDays(Date date, int days)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static String getShortDateTimeString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(SHORT_DATE_TIME_PATTERN, Locale.getDefault());
        return sf.format(date);
    }

    public static String getShortDateTimeNoYearString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(SHORT_DATE_TIME_NO_YEAR_PATTERN, Locale.getDefault());
        return sf.format(date);
    }

    public static String getShortDateTimeNoYearWithDayNameString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(SHORT_DATE_TIME_NO_YEAR_WITH_DAY_NAME_PATTERN, Locale.getDefault());
        return sf.format(date);
    }


    public static String getShortDateString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(SHORT_DATE_PATTERN, Locale.getDefault());
        return sf.format(date);
    }

    public static String getShortTimeString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(SHORT_TIME_PATTERN, Locale.getDefault());
        return sf.format(date);
    }

    public static String getDecimalFormatForQty(Float qty) {
        DecimalFormat decimalFormat = new DecimalFormat(NUMBER_WO_FRACTION_PATTERN);
        return decimalFormat.format(qty);
    }

    public static String getDecimalFormatForAmount(Object amount) {

        DecimalFormat decimalFormat = new DecimalFormat(NUMBER_FRACTION_PATTERN);
        return decimalFormat.format(amount);
    }

    public static String getAccountingFormatForAmount(Object amount) {
        DecimalFormat decimalFormat = new DecimalFormat(NUMBER_SIGNED_WO_FRACTION_PATTERN);
        return decimalFormat.format(amount);
    }

    public static String GetFormatQtyForInteger(int qty) {
        String stringFormat;
        stringFormat = NumberFormat.getIntegerInstance().format(qty);
        return stringFormat;
    }

    public static Date GetMinValueDate () {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900,Calendar.JANUARY,1);
        return calendar.getTime();
    }

    public static Date AddDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date RemoveTimeFromDate(Date date) {

        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static ApplicationVersion GetVersionInfo(Context context) {
        ApplicationVersion appVersion = new ApplicationVersion();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion.versionName = packageInfo.versionName;
            appVersion.versionCode = packageInfo.versionCode;
            appVersion.isDebugAble = (0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return appVersion;
    }


    public static class ApplicationVersion {
        public String versionName;
        public int versionCode;
        public boolean isDebugAble;

        ApplicationVersion() {
            this.versionName = "";
            this.versionCode = 0;
            this.isDebugAble = false;
        }
    }

    public static final String FCM_DELIMITER = "[|]@[|]";
}
