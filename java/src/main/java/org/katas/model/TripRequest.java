package org.katas.model;

import org.katas.exceptions.InvalidTripInputException;

import java.util.Date;
import java.util.List;

public record TripRequest(TripDetails details, List<Passenger> passengers) {
    public void isValid() {
        if (passengers().isEmpty()) {
            throw new InvalidTripInputException("No passenger");
        }
        // Vérification du nom de la ville de départ
        if (details().from().trim().isEmpty()) {
            throw new InvalidTripInputException("Start city is invalid");
        }
        // Vérification du nom de la ville d'arrivée
        if (details().to().trim().isEmpty()) {
            throw new InvalidTripInputException("Destination city is invalid");
        }
//        TODO Vérifier qu'on a pas de null
        // Vérification de la validité de la date (doit être dans le futur)
        if (details().when().before(new Date())) {
            throw new InvalidTripInputException("Date is invalid");
        }
        for (Passenger passenger : passengers()) {
             passenger.isValid();
        }
    }
}
