package ru.simplex_software.smeta.dao;


import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;
import ru.simplex_software.smeta.model.template.Template;

import java.util.List;

@AutoDAO
public interface MaterialDAO extends Dao<Material, Long> {

    @Finder(query = "from Material where task = :task order by id")
    List<Material> findByTask(@Named("task") Task task);

    @Finder(query = "from Material where template = :template order by id")
    List<Material> findByTemplate(@Named("template") Template template);


    @Finder(query = "select new Material(material.name, material.units, sum(material.quantity), " +
            "material.unitPrice, sum(material.amount)) from Material material, " +
            "TaskFilter filter left join filter.cities as filterCity " +
            "where filter = :filter and " +
            "material.task.city = COALESCE(filterCity, material.task.city) and " +
            "COALESCE(material.task.completedDate, filter.startDate, cast('2101-01-02' as date)) >= COALESCE(filter.startDate, material.task.completedDate, cast('2001-01-01' as date)) and " +
            "COALESCE(material.task.completedDate, filter.endDate, cast('2001-01-01' as date)) <= COALESCE(filter.endDate, material.task.completedDate, cast('2101-01-02' as date)) and " +
            "material.task.shopName = :shopName and " +
            "material.task.city.id = :cityId " +
            "group by material.name, material.units, material.unitPrice")
    List<Material> findMaterialByShopName(@Named("filter") TaskFilter filter, @Named("shopName") String shopName, @Named("cityId") long cityId);

}

