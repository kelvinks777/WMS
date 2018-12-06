package com.bosnet.ngemart.libgen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Created by manbaul on 12/29/2016.
 */

public class DateUtilTest {

    @Test
    public void test_SmallDateToBiggerWithSameYear()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFrom = sdf.parse("28/12/2016");
            Date dateTo = sdf.parse("29/12/2016");

            int result = DateUtil.DaysBetween(dateFrom, dateTo);
            assertFalse("From : " + sdf.format(dateFrom) + ", To : " + sdf.format(dateTo) + ", result = " + result, result != -1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_BigDateToSmallerWithSameYear()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateTo = sdf.parse("28/12/2016");
            Date dateFrom = sdf.parse("29/12/2016");

            int result = DateUtil.DaysBetween(dateFrom, dateTo);
            assertFalse("From : " + sdf.format(dateFrom) + ", To : " + sdf.format(dateTo) + ", result = " + result, result != 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_SmallDateToBiggerWithDiffYear()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFrom = sdf.parse("28/12/2016");
            Date dateTo = sdf.parse("5/1/2017");

            int result = DateUtil.DaysBetween(dateFrom, dateTo);
            assertFalse("From : " + sdf.format(dateFrom) + ", To : " + sdf.format(dateTo) + ", result = " + result, result != -8);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_BigDateToSmallerWithDiffYear()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateTo = sdf.parse("28/12/2016");
            Date dateFrom = sdf.parse("5/1/2017");

            int result = DateUtil.DaysBetween(dateFrom, dateTo);
            assertFalse("From : " + sdf.format(dateFrom) + ", To : " + sdf.format(dateTo) + ", result = " + result, result != 8);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_SmallDateToBiggerWithDiffYearMoreThanOne()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateFrom = sdf.parse("28/12/2016");
            Date dateTo = sdf.parse("5/1/2018");

            int result = DateUtil.DaysBetween(dateFrom, dateTo);
            assertFalse("From : " + sdf.format(dateFrom) + ", To : " + sdf.format(dateTo) + ", result = " + result, result != -373);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test_BigDateToSmallerWithDiffYearMoreThanOne()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateTo = sdf.parse("28/12/2016");
            Date dateFrom = sdf.parse("5/1/2018");

            int result = DateUtil.DaysBetween(dateFrom, dateTo);
            assertFalse("From : " + sdf.format(dateFrom) + ", To : " + sdf.format(dateTo) + ", result = " + result, result!=373);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
