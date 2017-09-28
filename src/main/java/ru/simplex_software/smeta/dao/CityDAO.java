package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.City;

import java.util.List;

@AutoDAO
public interface CityDAO extends Dao<City, Long> {

    @Finder(query = "from City where name = :cityName")
    List<City> findCityForName(@Named("cityName") String cityName);

    @Finder(query = "from City where lower(name) like lower(:city)")
    List<City> findLikeNameCaseInsensitive(@Named("city") String city);

    @Finder(query = "from City")
    List<City> findAll();
}
