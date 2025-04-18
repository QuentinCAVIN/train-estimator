package org.katas;

import org.katas.model.TripDetails;

import java.util.Date;

public class TripDetailsBuilder {
    private String from = "Paris";
    private String to = "Lyon";
    private Date when = new Date(System.currentTimeMillis() + 86400000);

    public TripDetailsBuilder from(String from) {
        this.from = from;
        return this;
    }

    public TripDetailsBuilder to(String to) {
        this.to = to;
        return this;
    }

    public TripDetailsBuilder when(Date when) {
        this.when = when;
        return this;
    }

    public TripDetails build() {
        return new TripDetails(from, to, when);
    }
}
