package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import ru.simplex_software.smeta.model.Task;

import java.util.List;

@AutoDAO
public interface TaskDAO extends Dao<Task, Long> {

    @Finder(query = "from Task")
    List<Task> findAllTasks();

    @Finder(query = "select count(*) from Task")
    long getTaskCount();

}
