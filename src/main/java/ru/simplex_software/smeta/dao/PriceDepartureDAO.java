package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import ru.simplex_software.smeta.model.PriceDeparture;

import java.util.List;

@AutoDAO
public interface PriceDepartureDAO extends Dao<PriceDeparture, Long> {

    @Finder(query = "from PriceDeparture")
    List<PriceDeparture> findAllDepartures();

}
