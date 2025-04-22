package org.katas.helper;

import java.util.Date;

public class DateHelper {

    public static double getDifferenceInDays(Date d1, Date d2) {
        long msPerDay = 24 * 60 * 60 * 1000L;
        return (double) (d1.getTime() - d2.getTime()) / msPerDay;
    }
}