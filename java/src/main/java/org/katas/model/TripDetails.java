package org.katas.model;

import java.util.Date;

public record TripDetails(String from, String to, Date when) {

    public double applyingDateModifierOnPrice(double modifiedPrice, double basePrice) {
// DATE CHOISI = 10/05/2025
        Date currentDate = new Date();
//  NOUS SOMME LE   01/05/2025
        currentDate.setDate(currentDate.getDate() + 30);
//        31/05/2025
        if (when().getTime() >= currentDate.getTime()) {
            modifiedPrice -= basePrice * 0.2;
        } else {
            // Sinon, majoration progressive si date proche
            currentDate.setDate(currentDate.getDate() - 30 + 5);
//            06/05/2025
            if (when().getTime() > currentDate.getTime()) {
//                10/05/2025   >   06/05/2025
                currentDate.setDate(currentDate.getDate() - 5);
//                01/05/2025
                var diffDays = ((int) ((when().getTime() / (24 * 60 * 60 * 1000)) - (int) (currentDate.getTime() / (24 * 60 * 60 * 1000))));
                modifiedPrice += (20 - diffDays) * 0.02 * basePrice;
            } else {
                // Réservation très tardive → plein tarif + surcharge
                modifiedPrice += basePrice;
            }
        }
        return modifiedPrice;
    }
}
