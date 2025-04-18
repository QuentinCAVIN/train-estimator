package org.katas;

import org.json.JSONObject;
import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripRequest;
import org.katas.model.exceptions.ApiException;
import org.katas.model.exceptions.InvalidTripInputException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class TrainTicketEstimator {
    // Si aucun passager, le prix est 0
    public double estimate(TripRequest trainDetails) {
        if (trainDetails.passengers().isEmpty()) {
            return 0;
        }
        // Vérification du nom de la ville de départ
        if (trainDetails.details().from().trim().isEmpty()) {
            throw new InvalidTripInputException("Start city is invalid");
        }
        // Vérification du nom de la ville d'arrivée
        if (trainDetails.details().to().trim().isEmpty()) {
            throw new InvalidTripInputException("Destination city is invalid");
        }
//        TODO Vérifier qu'on a pas de null
        // Vérification de la validité de la date (doit être dans le futur)
        if (trainDetails.details().when().before(new Date())) {
            throw new InvalidTripInputException("Date is invalid");
        }

        // Appel à l’API pour obtenir le prix de base du trajet
        double basePrice = getBasePrice(trainDetails);

        // Liste des passagers
        List<Passenger> passengers = trainDetails.passengers();
        double total = 0;
        double temp;

        for (Passenger passenger : passengers) {

            // Vérification de l'âge
            if (passenger.age() < 0) {
                throw new InvalidTripInputException("Age is invalid");
            }

            // Tarification selon l'âge
            if (passenger.age() < 1) {
                temp = 0;
            } else if (passenger.age() <= 17) {
                temp = basePrice * 0.6;
            } else if (passenger.age() >= 70) {
                temp = basePrice * 0.8;
                if (passenger.discounts().contains(DiscountCard.Senior)) {
                    temp -= basePrice * 0.2;
                }
            } else {
                temp = basePrice * 1.2;
            }

            // Réduction si réservation anticipée (> 30 jours)
            Date currentDate = new Date();
            currentDate.setDate(currentDate.getDate() +30);
            if (trainDetails.details().when().getTime() >= currentDate.getTime() ) {
                temp -= basePrice * 0.2;
            } else {
                // Sinon, majoration progressive si date proche
                currentDate.setDate(currentDate.getDate() - 30 + 5);
                if (trainDetails.details().when().getTime() > currentDate.getTime()) {
                    currentDate.setDate(currentDate.getDate() - 5);
                    var diffDays = ((int)((trainDetails.details().when().getTime()/(24*60*60*1000)) - (int)(currentDate.getTime()/(24*60*60*1000))));
                    temp += (20 - diffDays) * 0.02 * basePrice;
                } else {
                    // Réservation très tardive → plein tarif + surcharge
                    temp += basePrice;
                }
            }

            // Tarification enfant (entre 1 et 4 ans) = 9 €
            if (passenger.age() > 0 && passenger.age() < 4) {
                temp = 9;
            }

            // Réduction spéciale carte TrainStroke
            if (passenger.discounts().contains(DiscountCard.TrainStroke)) {
                temp = 1;
            }

            // Ajout au total et réinitialisation
            total += temp;
            temp = basePrice;
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

    // On inclu (et on ne teste pas) l'ApiExcpetion Fréd et Eric sont ok avec ça
    protected double getBasePrice(TripRequest trainDetails) {
        double basePrice;
        try {
            // Connexion HTTP en GET
            String urlString = String.format("https://sncftrenitaliadb.com/api/train/estimate/price?from=%s&to=%s&date=%s", trainDetails.details().from(), trainDetails.details().to(), trainDetails.details().when());
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Lecture de la réponse
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            // Parsing JSON pour récupérer le prix
            JSONObject obj = new JSONObject(content.toString());
            basePrice = obj.has("price") ? obj.getDouble("price") : -1;
        } catch (Exception e) {
            basePrice = -1;
        }

        if (basePrice == -1) {
            throw new ApiException();
        }
        return basePrice;
    }
}