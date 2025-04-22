package org.katas.builder;

import org.katas.model.DiscountCard;
import org.katas.model.Passenger;

import java.util.ArrayList;
import java.util.List;

public class PassengerBuilder {
    private int age = 30;
    private String lastName = "";
    private final List<DiscountCard> discounts = new ArrayList<>();

    public PassengerBuilder age(int age) {
        this.age = age;
        return this;
    }

    public PassengerBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public PassengerBuilder withDiscount(DiscountCard card) {
        this.discounts.add(card);
        return this;
    }

    public PassengerBuilder withOutDiscount() {
        return this;
    }

    public Passenger build() {
        return new Passenger(age, discounts, lastName);
    }
}
