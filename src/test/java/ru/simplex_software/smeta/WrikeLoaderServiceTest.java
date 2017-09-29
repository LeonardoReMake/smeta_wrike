package ru.simplex_software.smeta;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
@Transactional
@Rollback
public class WrikeLoaderServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(WrikeLoaderServiceTest.class);

    @Resource
    private TaskDAO taskDAO;

    @Resource
    private WorkDAO workDAO;

    @Resource
    private MaterialDAO materialDAO;

    @Resource
    private WrikeLoaderService wrikeLoaderService;

    @Test
    public void loadWorksAndMaterials() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        context.registerShutdownHook();
    }

    private void createNewWorks(Task task, int n) throws Exception {
        for (int i = 0; i < n; i++) {
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

    private void createNewMaterials(Task task, int n) throws Exception {
        for (int i = 0; i < n; i++) {
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