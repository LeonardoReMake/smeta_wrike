package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.Manager;

@AutoDAO
public interface ManagerDAO extends Dao<Manager, Long> {

    @Finder(query = "from Manager where name = :name")
    Manager findMangerForName(@Named("name") String name);


}
