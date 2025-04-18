package org.katas;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;

import java.util.ArrayList;
import java.util.List;

public class PassengerBuilder {
    private int age = 30;
    private List<DiscountCard> discounts = new ArrayList<>();

    public PassengerBuilder age(int age) {
        this.age = age;
        return this;
    }

    public PassengerBuilder withDiscount(DiscountCard card) {
        this.discounts.add(card);
        return this;
    }

    public PassengerBuilder withDiscounts(List<DiscountCard> cards) {
        this.discounts = cards;
        return this;
    }

    public Passenger build() {
        return new Passenger(age, discounts);
    }
}
