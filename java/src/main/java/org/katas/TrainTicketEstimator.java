package org.katas;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripRequest;
import org.katas.repository.BasePriceRepositoryImpl;
import org.katas.repository.IBasePriceRepository;

import java.util.Date;
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
        // TODO Après test voir si on peux modifier temp en prixTotal
        double temp;

        for (Passenger passenger : passengers) {

            temp = passenger.applyingAgeModifierOnPrice(basePrice);
//            temp = getBasePriceBasedOnAge(passenger, basePrice);

            // Appel méthode de calcul du prix en fonction de la date de départ
            temp = applyingDateModifierOnPrice(trainDetails, temp, basePrice);

            // Réduction spéciale carte TrainStroke
            if (passenger.discounts().contains(DiscountCard.TrainStroke)) {
                temp = 1;
            }

            // Ajout au total et réinitialisation
            total += temp;
        }

        // Réduction couple (2 passagers adultes avec carte Couple)
        if (passengers.size() == 2) {
            boolean couple = false;
            boolean minor = false;
            for (Passenger passenger : passengers) {
                if (passenger.discounts().contains(DiscountCard.Couple)) {
                    couple = true;
                }
                if (passenger.age() < 18) {
                    minor = true;
                }
            }
            if (couple && !minor) {
                total -= basePrice * 0.2 * 2;
            }
        }

        // Réduction demi-couple (1 adulte avec carte HalfCouple)
        if (passengers.size() == 1) {
            boolean halfCouple = false;
            boolean minor = false;
            for (Passenger passenger : passengers) {
                if (passenger.discounts().contains(DiscountCard.HalfCouple)) {
                    halfCouple = true;
                }
                if (passenger.age() < 18) {
                    minor = true;
                }
            }
            if (halfCouple && !minor) {
                total -= basePrice * 0.1;
            }
        }

        // Prix final estimé
        return total;
    }

    // TODO Refactor pour envisager de mettre la variable basePrice en constante?
    protected double applyingDateModifierOnPrice(TripRequest trainDetails, double temp, double basePrice) {
// DATE CHOISI = 10/05/2025
        Date currentDate = new Date();
//  NOUS SOMME LE   01/05/2025
        currentDate.setDate(currentDate.getDate() + 30);
//        31/05/2025
        if (trainDetails.details().when().getTime() >= currentDate.getTime()) {
            temp -= basePrice * 0.2;
        } else {
            // Sinon, majoration progressive si date proche
            currentDate.setDate(currentDate.getDate() - 30 + 5);
//            06/05/2025
            if (trainDetails.details().when().getTime() > currentDate.getTime()) {
//                10/05/2025   >   06/05/2025
                currentDate.setDate(currentDate.getDate() - 5);
//                01/05/2025
                var diffDays = ((int) ((trainDetails.details().when().getTime() / (24 * 60 * 60 * 1000)) - (int) (currentDate.getTime() / (24 * 60 * 60 * 1000))));
                temp += (20 - diffDays) * 0.02 * basePrice;
            } else {
                // Réservation très tardive → plein tarif + surcharge
                temp += basePrice;
            }
        }
        return temp;
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