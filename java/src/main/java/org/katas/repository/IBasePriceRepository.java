package org.katas.repository;

import org.katas.model.TripRequest;

public interface IBasePriceRepository {
    // On inclut (et on ne teste pas) l'ApiExcpetion Fred et Eric sont ok avec ça
    double getBasePrice(TripRequest trainDetails);
}