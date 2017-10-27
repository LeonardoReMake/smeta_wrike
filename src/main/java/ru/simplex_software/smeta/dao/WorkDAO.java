package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;
import ru.simplex_software.smeta.model.Work;
import ru.simplex_software.smeta.model.template.Template;

import java.util.List;

@AutoDAO
public interface WorkDAO extends Dao<Work, Long>{

    @Finder(query = "from Work where task = :task order by id")
    List<Work> findByTask(@Named("task") Task task);

    @Finder(query = "from Work where template = :template order by id")
    List<Work> findByTemplate(@Named("template") Template template);

    @Finder(query = "select new Work(work.name, work.units, sum(work.quantity), " +
            "work.unitPrice, sum(work.amount)) from Work work, " +
            "TaskFilter filter left join filter.cities as filterCity " +
            "where filter = :filter and " +
            "work.task.city = COALESCE(filterCity, work.task.city) and " +
            "COALESCE(work.task.completedDate, filter.startDate, cast('2101-01-02' as date)) >= COALESCE(filter.startDate, work.task.completedDate, cast('2001-01-01' as date)) and " +
            "COALESCE(work.task.completedDate, filter.endDate, cast('2001-01-01' as date)) <= COALESCE(filter.endDate, work.task.completedDate, cast('2101-01-02' as date)) and " +
            "work.task.shopName = :shopName and " +
            "work.task.city.id = :cityId " +
            "group by work.name, work.units, work.unitPrice")
    List<Work> findWorkByShopName(@Named("filter") TaskFilter filter, @Named("shopName") String shopName, @Named("cityId") long cityId);

}


