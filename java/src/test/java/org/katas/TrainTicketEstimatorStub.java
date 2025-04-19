package org.katas;

import org.katas.model.TripRequest;

public class TrainTicketEstimatorStub extends TrainTicketEstimator {
    double basePrice = 100;
    @Override
    public double getBasePrice(TripRequest trainDetails) {
        return basePrice;
    }
    public void setBasePrice(int price){
        this.basePrice = price;
    }
}