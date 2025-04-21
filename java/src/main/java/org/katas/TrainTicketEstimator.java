package org.katas;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripRequest;
import org.katas.repository.BasePriceRepositoryImpl;
import org.katas.repository.IBasePriceRepository;

import java.util.List;

public class TrainTicketEstimator {

    TripRequest trainDetails;
    IBasePriceRepository basePriceRepository;

    public TrainTicketEstimator(IBasePriceRepository basePriceRepository, TripRequest trainDetails) {
        this.basePriceRepository = basePriceRepository;
        this.trainDetails = trainDetails;
        trainDetails.isValid();
    }

    // Si aucun passager, le prix est 0
    public double estimate() {

        // Appel à l’API pour obtenir le prix de base du trajet
        double basePrice = basePriceRepository.getBasePrice(trainDetails);

        // Liste des passagers
        List<Passenger> passengers = trainDetails.passengers();
        double total = 0;

        for (Passenger passenger : passengers) {

           double modifiedPrice = passenger.applyingAgeModifierOnPrice(basePrice);

            // Appel méthode de calcul du prix en fonction de la date de départ
            modifiedPrice = trainDetails.details().applyingDateModifierOnPrice(modifiedPrice, basePrice);


            // Réduction spéciale carte TrainStroke
            if (passenger.discounts().contains(DiscountCard.TrainStroke)) {
                modifiedPrice = 1;
            }
            if (passenger.discounts().contains(DiscountCard.Senior)) {
                modifiedPrice -= basePrice * 0.2;
            }

            // Ajout au total et réinitialisation
            total += modifiedPrice;
        }




        // Réduction couple (2 passagers adultes avec carte Couple)
        if (trainDetails.isEligibleCouple()) {
            total -= basePrice * 0.2 * 2;
        }

        // Réduction demi-couple (1 adulte avec carte HalfCouple)
        if (trainDetails.isEligibleHalfCouple()) {
            total -= basePrice * 0.1;
        }

        // Prix final estimé
        return total;
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