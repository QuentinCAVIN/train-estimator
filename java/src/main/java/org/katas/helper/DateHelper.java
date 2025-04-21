package org.katas.helper;

import java.util.Date;

public class DateHelper {

    public static int getDifferenceInDays(Date d1, Date d2) {
        long msPerDay = 24 * 60 * 60 * 1000L;
        long d1InDays = d1.getTime() / msPerDay;
        long d2InDays = d2.getTime() / msPerDay;
        return (int) (d1InDays - d2InDays);
    }
}