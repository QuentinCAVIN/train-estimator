package org.katas;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripRequest;
import org.katas.repository.BasePriceRepositoryImpl;
import org.katas.repository.IBasePriceRepository;
import org.katas.service.PriceModifierService;

import java.util.List;

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

        // Appel à l’API pour obtenir le prix de base du trajet
        double basePrice = basePriceRepository.getBasePrice(trainDetails);

        // Liste des passagers
        List<Passenger> passengers = trainDetails.passengers();
        double total = 0;

        for (Passenger passenger : passengers) {

           double modifiedPrice = priceModifier.applyingAgeModifierOnPrice(passenger, basePrice);

            // méthode de calcul du prix en fonction de la date de départ
            modifiedPrice = priceModifier.applyingDateModifierOnPrice(trainDetails.details().when() ,modifiedPrice, basePrice);

            total += priceModifier.applyDiscounts(passenger, modifiedPrice, basePrice);
        }

        return priceModifier.applyGroupDiscounts(trainDetails, total, basePrice);
    }

    //Constructeur et méthode nécessaires pour assurer la rétrocompatibilité
    ///////////////////////////
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