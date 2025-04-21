package org.katas;

import org.katas.model.Passenger;
import org.katas.model.TripRequest;
import org.katas.repository.BasePriceRepositoryImpl;
import org.katas.repository.IBasePriceRepository;
import org.katas.service.PriceModifierService;


public class TrainTicketEstimator {

    TripRequest trainDetails;
    IBasePriceRepository basePriceRepository;
    PriceModifierService priceModifier = new PriceModifierService();

    public TrainTicketEstimator(IBasePriceRepository basePriceRepository, TripRequest trainDetails) {
        this.basePriceRepository = basePriceRepository;
        this.trainDetails = trainDetails;
        trainDetails.isValid();
    }

    public double estimate() {
        double basePrice = getBasePrice();
        double priceForAllPassengers = 0;
        for (Passenger passenger : trainDetails.passengers()) {
            priceForAllPassengers += calculateFinalPriceForPassenger(passenger, basePrice);
        }
        return applyGroupDiscount(priceForAllPassengers, basePrice);
    }

    private double getBasePrice() {
        return basePriceRepository.getBasePrice(trainDetails);
    }

    private double calculateFinalPriceForPassenger(Passenger passenger, double basePrice) {
        double price = priceModifier.applyingAgeModifierOnPrice(passenger, basePrice);
        price = priceModifier.applyingDateModifierOnPrice(trainDetails.details().when(), price, basePrice);
        return priceModifier.applyDiscounts(passenger, price, basePrice);
    }

    private double applyGroupDiscount(double total, double basePrice) {
        return priceModifier.applyGroupDiscounts(trainDetails, total, basePrice);
    }

    //Constructeur et méthode nécessaires pour assurer la rétrocompatibilité

    /// ////////////////////////
    public TrainTicketEstimator() {
        this.basePriceRepository = new BasePriceRepositoryImpl();
    }

    public double estimate(TripRequest trainDetails) {
        this.trainDetails = trainDetails;
        trainDetails.isValid();
        return estimate();
    }
    ///////////////////////////
}