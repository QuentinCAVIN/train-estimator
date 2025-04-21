package org.katas.model;

import org.katas.exceptions.InvalidTripInputException;

import java.util.List;

public record Passenger(int age, List<DiscountCard> discounts) {

    public boolean isInfant() {
        return age < 1;
    }

    public boolean isToddler() {
        return age >= 1 && age <= 3;
    }

    public boolean isMajor() {
        return age >= 18;
    }

    public boolean isSenior() {
        return age >= 70;
    }

    public void isValid() {
        if (age < 0) {
            throw new InvalidTripInputException("Age is invalid");
        }
    }
}
