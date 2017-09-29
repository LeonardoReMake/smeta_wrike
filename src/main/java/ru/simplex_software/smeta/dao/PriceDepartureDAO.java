package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Finder;
import ru.simplex_software.smeta.model.PriceDeparture;

import java.util.List;

@AutoDAO
public interface PriceDepartureDAO {

    @Finder(query = "from PriceDeparture")
    List<PriceDeparture> findAllDepartures();

}
