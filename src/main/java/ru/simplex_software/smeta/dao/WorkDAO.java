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
            "work.task.city = filterCity and " +
            "work.task.completedDate >= filter.startDate and " +
            "work.task.completedDate <= filter.endDate and " +
            "work.task.shopName = :shopName and " +
            "work.task.city.id = :cityId and " +
            "work.task.completedDate IS NOT NULL " +
            "group by work.name, work.units, work.unitPrice")
    List<Work> findWorkByShopName(@Named("filter") TaskFilter filter,
                                  @Named("shopName") String shopName,
                                  @Named("cityId") long cityId);

}


