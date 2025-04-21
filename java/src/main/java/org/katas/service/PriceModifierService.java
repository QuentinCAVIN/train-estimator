package org.katas.service;

import org.katas.model.*;


import org.katas.model.Passenger;

import java.util.Date;

public class PriceModifierService {

    public double applyingAgeModifierOnPrice(Passenger passenger, double basePrice) {
        double temp;
        // Tarification selon l'âge
        if (passenger.isInfant()) {
            temp = 0;
        } else if (passenger.isToddler()){
            temp = 9;
        } else if (!passenger.isMajor()) {
            temp = basePrice * 0.6;
        } else if (passenger.isSenior()) {
            temp = basePrice * 0.8;
        } else {
            temp = basePrice * 1.2;
        }
        return temp;
    }

    public double applyingDateModifierOnPrice(Date departure, double modifiedPrice, double basePrice) {
// DATE CHOISI = 10/05/2025
        Date currentDate = new Date();
//  NOUS SOMME LE   01/05/2025
        currentDate.setDate(currentDate.getDate() + 30);
//        31/05/2025
        if (departure.getTime() >= currentDate.getTime()) {
            modifiedPrice -= basePrice * 0.2;
        } else {
            // Sinon, majoration progressive si date proche
            currentDate.setDate(currentDate.getDate() - 30 + 5);
//            06/05/2025
            if (departure.getTime() > currentDate.getTime()) {
//                10/05/2025   >   06/05/2025
                currentDate.setDate(currentDate.getDate() - 5);
//                01/05/2025
                var diffDays = ((int) (departure.getTime() / (24 * 60 * 60 * 1000)) - (int) (currentDate.getTime() / (24 * 60 * 60 * 1000)));
                modifiedPrice += (20 - diffDays) * 0.02 * basePrice;
            } else {
                // Réservation très tardive → plein tarif + surcharge
                modifiedPrice += basePrice;
            }
        }
        return modifiedPrice;
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