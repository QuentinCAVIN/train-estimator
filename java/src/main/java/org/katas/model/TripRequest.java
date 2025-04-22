package org.katas.model;

import org.katas.exceptions.InvalidTripInputException;

import java.util.Date;
import java.util.List;

public record TripRequest(TripDetails details, List<Passenger> passengers) {

    public boolean isEligibleCouple() {
        return passengers.size() == 2 &&
                passengers.stream().allMatch(Passenger::isMajor) &&
                passengers.stream().anyMatch(p -> p.discounts().contains(DiscountCard.COUPLE)) &&
                passengers.stream().noneMatch(p -> p.discounts().contains(DiscountCard.TRAINSTROKE));
    }

    public boolean isEligibleHalfCouple() {
        return passengers.size() == 1 &&
                passengers.stream().allMatch(Passenger::isMajor) &&
                passengers.stream().anyMatch(p -> p.discounts().contains(DiscountCard.HALF_COUPLE)) &&
                passengers.stream().noneMatch(p -> p.discounts().contains(DiscountCard.TRAINSTROKE));
    }

    public boolean isEligibleFamily() {
        if (passengers.size() <= 1) return false;

        boolean hasFamilyCard = passengers.stream()
                .anyMatch(p -> p.discounts().contains(DiscountCard.FAMILY));

        String referenceLastName = passengers.get(0).lastName();

        boolean allSameLastName = passengers.stream()
                .allMatch(p -> p.lastName().equalsIgnoreCase(referenceLastName));

        return hasFamilyCard && allSameLastName;
    }

    public void isValid() {
        if (passengers().isEmpty()) {
            throw new InvalidTripInputException("No passenger");
        }

        if (details().from().trim().isEmpty()) {
            throw new InvalidTripInputException("Start city is invalid");
        }

        if (details().to().trim().isEmpty()) {
            throw new InvalidTripInputException("Destination city is invalid");
        }
        if (details().when() == null) {
            throw new InvalidTripInputException("Date is invalid");
        }

        if (details().when().before(new Date())) {
            throw new InvalidTripInputException("Date is invalid");
        }
        for (Passenger passenger : passengers()) {
            passenger.isValid();
        }
    }
}