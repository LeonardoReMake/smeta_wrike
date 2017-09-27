package ru.simplex_software.smeta;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.simplex_software.smeta.dao.MaterialDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@Transactional
@Rollback
public class WrikeLoaderServiceTest {

    @Resource
    private TaskDAO taskDAO;

    @Resource
    private WorkDAO workDAO;

    @Resource
    private MaterialDAO materialDAO;

    @Test
    public void loadNewTasks() throws Exception {

        final List<Task> newTasks = taskDAO.findAllTasks();

        for (Task task : newTasks) {
            createNewWorks(task);
            createNewMaterials(task);
        }
    }

    private void createNewWorks(Task task) throws Exception {
        for (int i = 0; i < 5; i++) {
            final Work work = new Work();
            work.setName("Замена замка мебельного");
            work.setUnits("шт");
            work.setQuantity(3d);
            work.setUnitPrice(150d);
            work.setAmount(450d);
            work.setTask(task);

            workDAO.saveOrUpdate(work);
        }
    }

    private void createNewMaterials(Task task) throws Exception {
        for (int i = 0; i < 5; i++) {
            final Material material = new Material();
            material.setName("Замена замка мебельного");
            material.setUnits("шт");
            material.setQuantity(3d);
            material.setUnitPrice(150d);
            material.setAmount(450d);
            material.setTask(task);

            materialDAO.saveOrUpdate(material);
        }
    }

}