package org.katas.service;

import org.katas.helper.DateHelper;
import org.katas.model.BookingTiming;

import java.util.Date;

public class BookingService {

    public BookingTiming determineBookingTiming(Date departure, Date today) {
        int daysBeforeDeparture = DateHelper.getDifferenceInDays(departure, today);

        if (daysBeforeDeparture >= 30) {
            return BookingTiming.EARLY;
        } else if (daysBeforeDeparture >= 6) {
            return BookingTiming.STANDARD;
        } else if (daysBeforeDeparture > 0.25) {
            return BookingTiming.LATE;
        } else {
            return BookingTiming.LAST_MINUTE;
        }
    }
}
