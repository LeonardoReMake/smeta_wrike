package ru.simplex_software.smeta.dao;

import net.sf.autodao.AutoDAO;
import net.sf.autodao.Dao;
import net.sf.autodao.Finder;
import net.sf.autodao.Named;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.template.Template;

import java.util.List;

@AutoDAO
public interface TemplateDAO extends Dao<Template, Long>{

    @Finder(query = "from Template")
    List<Template> findAllTemplates();

    @Finder(query = "from Template where name = :name")
    List<Template> findByName(@Named("name") String name);

}
