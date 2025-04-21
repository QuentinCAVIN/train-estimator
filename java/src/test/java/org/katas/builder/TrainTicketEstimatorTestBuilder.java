package org.katas.builder;

import org.katas.TrainTicketEstimator;
import org.katas.fake.FakeBasePriceRepository;
import org.katas.model.TripRequest;

public class TrainTicketEstimatorTestBuilder {
    TripRequest tripRequest;
    //TODO Pas besoin de constructeur (juste un vide) utiliser uniquement les with...
    FakeBasePriceRepository fakeBasePriceRepository;
    public TrainTicketEstimatorTestBuilder(FakeBasePriceRepository fakeBasePriceRepository){
        this.fakeBasePriceRepository = fakeBasePriceRepository;
    }

    public TrainTicketEstimatorTestBuilder withTripRequest(TripRequest tripRequest) {
        this.tripRequest = tripRequest;
        return this;
    }
    public TrainTicketEstimatorTestBuilder withBasePriceRepository(FakeBasePriceRepository fakeBasePriceRepository) {
        this.fakeBasePriceRepository = fakeBasePriceRepository;
        return this;
    }
    public TrainTicketEstimator build() {
        return new TrainTicketEstimator(fakeBasePriceRepository, tripRequest);
    }
}
