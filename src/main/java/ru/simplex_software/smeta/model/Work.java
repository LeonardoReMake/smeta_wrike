package ru.simplex_software.smeta.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.smeta.model.template.Template;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Work extends Element {

    private static Logger LOG = LoggerFactory.getLogger(Work.class);

    @ManyToOne
    private Template template;

    public Work(String name, String units, Double quantity, Double unitPrice, Double amount, Template template) {
        super(name, units, quantity, unitPrice, amount);
        this.template = template;
    }

    public Work() {}

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

}
