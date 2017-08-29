package ru.simplex_software.smeta.dao;

import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;

import java.util.List;

public interface WorkDAO extends Dao<Work, Long>{

    @Finder(query = "from Work where task = :task order by id")
    List<Work> findByWorks(@Named("task") Task task);

}
