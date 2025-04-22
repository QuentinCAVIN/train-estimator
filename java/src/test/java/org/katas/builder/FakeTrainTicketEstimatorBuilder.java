package org.katas.builder;

import org.katas.service.TrainTicketEstimator;
import org.katas.fake.FakeBasePriceRepository;
import org.katas.model.TripRequest;
import org.katas.repository.IBasePriceRepository;

public class FakeTrainTicketEstimatorBuilder {
    TripRequest tripRequest;
    IBasePriceRepository basePriceRepository = new FakeBasePriceRepository();

    public FakeTrainTicketEstimatorBuilder withTripRequest(TripRequest tripRequest) {
        this.tripRequest = tripRequest;
        return this;
    }

    public TrainTicketEstimator build() {
        return new TrainTicketEstimator(basePriceRepository, tripRequest);
    }
}
