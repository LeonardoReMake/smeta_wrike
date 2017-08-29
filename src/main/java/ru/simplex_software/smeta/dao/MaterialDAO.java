package ru.simplex_software.smeta.dao;


import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;

import java.util.List;

@AutoDAO
public interface MaterialDAO extends Dao<Material, Long> {

    @Finder(query = "from Material where task = :task order by id")
    List<Material> findByMaterials(@Named("task") Task task);

}
