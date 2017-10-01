package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.zkutils.entity.LongIdPersistentEntity;

import javax.persistence.Entity;

@Entity
public class PriceDeparture extends LongIdPersistentEntity {

    private static Logger LOG = LoggerFactory.getLogger(PriceDeparture.class);

    private Double dayTimePrice;

    private Double urgentTimePrice;

    private Double nightlyTimePrice;

    public PriceDeparture() { }

    public Double getDayTimePrice() {
        return dayTimePrice;
    }

    public void setDayTimePrice(Double dayTimePrice) {
        this.dayTimePrice = dayTimePrice;
    }

    public Double getUrgentTimePrice() {
        return urgentTimePrice;
    }

    public void setUrgentTimePrice(Double urgentTimePrice) {
        this.urgentTimePrice = urgentTimePrice;
    }

    public Double getNightlyTimePrice() {
        return nightlyTimePrice;
    }

    public void setNightlyTimePrice(Double nightlyTimePrice) {
        this.nightlyTimePrice = nightlyTimePrice;
    }
}
