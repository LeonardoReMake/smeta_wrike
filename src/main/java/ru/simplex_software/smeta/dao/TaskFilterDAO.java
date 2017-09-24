package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import ru.simplex_software.smeta.model.TaskFilter;

import java.util.List;

@AutoDAO
public interface TaskFilterDAO extends Dao<TaskFilter, Long> {

    @Finder(query = "from TaskFilter")
    List<TaskFilter> findAllFilters();

}
