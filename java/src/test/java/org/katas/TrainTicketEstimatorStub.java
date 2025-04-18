package org.katas;

import org.katas.model.TripRequest;

public class TrainTicketEstimatorStub extends TrainTicketEstimator {
    double price = 100;
    @Override
    public double getBasePrice(TripRequest trainDetails) {
        return price;
    }
    public void setPrice(int price){
        this.price = price;
    }
}