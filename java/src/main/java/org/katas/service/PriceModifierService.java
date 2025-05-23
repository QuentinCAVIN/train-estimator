package org.katas.service;

import org.katas.helper.DateHelper;
import org.katas.model.*;

import org.katas.model.Passenger;

import java.util.Date;

public class PriceModifierService {

    BookingService bookingService = new BookingService();

    public double applyingAgeModifierOnPrice(Passenger passenger, double basePrice) {
        double priceModified;

        if (passenger.isInfant()) {
            priceModified = 0;
        } else if (passenger.isToddler()) {
            priceModified = 9;
        } else if (!passenger.isMajor()) {
            priceModified = basePrice * 0.6;
        } else if (passenger.isSenior()) {
            priceModified = basePrice * 0.8;
        } else {
            priceModified = basePrice * 1.2;
        }
        return priceModified;
    }

    public double applyingDateModifierOnPrice(Date departure, double priceModified, double basePrice) {
        Date today = new Date();
        BookingTiming timing = bookingService.determineBookingTiming(departure, today);

        switch (timing) {
            case EARLY, LAST_MINUTE:
                return priceModified - basePrice * 0.2;

            case STANDARD:
                double diffDays = DateHelper.getDifferenceInDays(departure, today);
                double surcharge = (20 - diffDays) * 0.02 * basePrice;
                return priceModified + surcharge;

            case LATE:
            default:
                return priceModified + basePrice;
        }
    }

    public double applyIndividualDiscounts(Passenger passenger, double currentPrice, double basePrice) {
        if (passenger.discounts().contains(DiscountCard.TRAINSTROKE)) return 1;
        if (passenger.discounts().contains(DiscountCard.SENIOR) && passenger.age() >= 70) {
            return currentPrice - basePrice * 0.2;
        }
        return currentPrice;
    }

    public double applyGroupDiscounts(TripRequest trainDetails, double total, double basePrice) {

        if (trainDetails.isEligibleFamily()) {
            total -= basePrice * 0.3 * trainDetails.passengers().size();
        } else if (trainDetails.isEligibleCouple()) {
            total -= basePrice * 0.2 * 2;
        } else if (trainDetails.isEligibleHalfCouple()) {
            total -= basePrice * 0.1;
        }

        return total;
    }
}