package org.katas;

import org.katas.model.TripRequest;
import org.katas.repository.IBasePriceRepository;

public class FakeBasePriceRepository implements IBasePriceRepository {
    double basePrice = 100;
   public void setBasePrice(int price){
       this.basePrice = price;
   }

    @Override
    public double getBasePrice(TripRequest trainDetails) {
        return basePrice;
    }
}