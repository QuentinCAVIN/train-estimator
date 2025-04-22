package org.katas.repository;

import org.json.JSONObject;
import org.katas.model.TripRequest;
import org.katas.exceptions.ApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BasePriceRepositoryImpl implements IBasePriceRepository {
    // On inclut (et on ne teste pas) l'ApiException. Fred et Eric sont ok avec ça.
    @Override
    public double getBasePrice(TripRequest trainDetails) {
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