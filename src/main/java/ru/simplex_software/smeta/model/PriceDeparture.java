package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.Entity;

@Entity
public class PriceDeparture extends LongIdPersistentEntity {

    private static Logger LOG = LoggerFactory.getLogger(PriceDeparture.class);

    private double dayTimePrice;

    private double urgentTimePrice;

    private double nightlyTimePrice;

    public PriceDeparture() { }

    public double getDayTimePrice() {
        return dayTimePrice;
    }

    public void setDayTimePrice(double dayTimePrice) {
        this.dayTimePrice = dayTimePrice;
    }

    public double getUrgentTimePrice() {
        return urgentTimePrice;
    }

    public void setUrgentTimePrice(double urgentTimePrice) {
        this.urgentTimePrice = urgentTimePrice;
    }

    public double getNightlyTimePrice() {
        return nightlyTimePrice;
    }

    public void setNightlyTimePrice(double nightlyTimePrice) {
        this.nightlyTimePrice = nightlyTimePrice;
    }
}
