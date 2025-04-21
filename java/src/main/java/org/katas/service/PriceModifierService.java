package org.katas.service;

import org.katas.helper.DateHelper;
import org.katas.model.*;


import org.katas.model.Passenger;

import java.util.Date;

public class PriceModifierService {

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
        Date datePlus30 = new Date(today.getTime());
        datePlus30.setDate(datePlus30.getDate() + 30);

        // Si réservation < à 30 jours
        if (departure.after(datePlus30) || departure.equals(datePlus30)) {
            return priceModified - basePrice * 0.2;
        }

        // Réservation entre 6 et 29 jours à l’avance
        Date datePlus5 = new Date(today.getTime());
        datePlus5.setDate(datePlus5.getDate() + 5);

        if (departure.after(datePlus5)) {
            int diffDays = DateHelper.getDifferenceInDays(departure, today);
            double surcharge = (20 - diffDays) * 0.02 * basePrice;
            return priceModified + surcharge;
        }

        // Réservation moins de 5 jours
        return priceModified + basePrice;
    }

    public double applyDiscounts(Passenger passenger, double currentPrice, double basePrice) {
        if (passenger.discounts().contains(DiscountCard.TrainStroke)) return 1;
        if (passenger.discounts().contains(DiscountCard.Senior) && passenger.age() >= 70) {
            return currentPrice - basePrice * 0.2;
        }
        return currentPrice;
    }

    public double applyGroupDiscounts(TripRequest trainDetails, double total, double basePrice) {

        if (trainDetails.isEligibleCouple()) {
            total -= basePrice * 0.2 * 2;
        } else if (trainDetails.isEligibleHalfCouple()) {
            total -= basePrice * 0.1;
        }

        return total;
    }
}