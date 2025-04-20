package org.katas.model;

import org.katas.model.exceptions.InvalidTripInputException;

import java.util.List;

public record Passenger(int age, List<DiscountCard> discounts) {
    public double getBasePriceBasedOnAge(double basePrice) {
        double temp;
        // Tarification selon l'âge
        if (age < 1) {
            temp = 0;
        } else if (age < 4){
            temp = 9;
        } else if (age <= 17) {
            temp = basePrice * 0.6;
        } else if (age >= 70) {
            temp = basePrice * 0.8;
            if (discounts().contains(DiscountCard.Senior)) {
                temp -= basePrice * 0.2;
            }
        } else {
            temp = basePrice * 1.2;
        }
        return temp;
    }

    public void isValid() {
        if (age < 0) {
            throw new InvalidTripInputException("Age is invalid");
        }
    }
}
