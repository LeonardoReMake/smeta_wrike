package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.Task;

import java.util.List;

@AutoDAO
public interface TaskDAO extends Dao<Task, Long> {

    @Finder(query = "from Task order by createdDate desc")
    List<Task> findAllTasks();

    @Finder(query = "from Task where wrikeId=:wrikeId")
    Task findByWrikeId(@Named("wrikeId")String wrikeId);

}
