package org.katas.builder;

import org.katas.model.Passenger;
import org.katas.model.TripDetails;
import org.katas.model.TripRequest;

import java.util.*;

public class TripRequestBuilder {
    private TripDetails details = new TripDetails("Paris", "Lyon", new Date(System.currentTimeMillis() + 86400000));
    private List<Passenger> passengers = new ArrayList<>();

    public TripRequestBuilder withDetails(TripDetails details) {
        this.details = details;
        return this;
    }

    public TripRequestBuilder withPassenger(Passenger passenger) {
        this.passengers.add(passenger);
        return this;
    }
    public TripRequestBuilder withNoPassenger() {
        return this;
    }

    public TripRequestBuilder withPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
        return this;
    }

    public TripRequest build() {
        return new TripRequest(details, passengers);
    }
}

