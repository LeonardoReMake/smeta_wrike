package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Limit;
import net.sf.autodao.Named;
import net.sf.autodao.Offset;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;

import java.util.List;

@AutoDAO
public interface TaskFilterImplDAO extends Dao<Task, Long> {

    @Finder(query = "select task.id from Task task, TaskFilter filter " +
            "where filter = :filter and " +
            "task.city = COALESCE(filter.city, task.city) " +
//            "COALESCE(task.completedDate, filter.startDate, cast('2001-01-01' as date)) >= COALESCE(filter.startDate, task.createdDate, cast('2001-01-01' as date)) and " +
//            "COALESCE(task.completedDate, filter.endDate, cast('2001-01-01' as date)) <= COALESCE(filter.endDate, task.createdDate, cast('2001-01-01' as date)) " +
            "order by task.createdDate desc")
    List<Long> findTaskIdsByFilter(@Named("filter") TaskFilter taskFilter, @Offset int offset, @Limit int limit);

    @Finder(query = "select count(*) from Task task, TaskFilter filter " +
            "where filter = :filter and " +
            "task.city = COALESCE(filter.city, task.city)")
//            "COALESCE(task.completedDate, filter.startDate, cast('2001-01-01' as date)) >= COALESCE(filter.startDate, task.createdDate, cast('2001-01-01' as date)) and " +
//            "COALESCE(task.completedDate, filter.endDate, cast('2001-01-01' as date)) <= COALESCE(filter.endDate, task.createdDate, cast('2001-01-01' as date))")
    Long countTasksByFilter(@Named("filter") TaskFilter taskFilter);
}
