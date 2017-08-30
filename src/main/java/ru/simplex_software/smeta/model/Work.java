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
    private Task task;

    @ManyToOne
    private Template template;

    public Work(String name, String units, Double quantity, Double unitPrice, Double amount, Task task, Template template) {
        super(name, units, quantity, unitPrice, amount);
        this.task = task;
        this.template = template;
    }

    public Work(Task task) {
        this.task = task;
    }

    public Work() {}

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

}
