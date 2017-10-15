package ru.simplex_software.smeta.servlet;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import ru.simplex_software.smeta.dao.CityDAO;
import ru.simplex_software.smeta.dao.MaterialDAO;
import ru.simplex_software.smeta.dao.PriceDepartureDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.TaskFilterDAO;
import ru.simplex_software.smeta.dao.TaskFilterImplDAO;
import ru.simplex_software.smeta.dao.WorkDAO;
import ru.simplex_software.smeta.excel.ReportCreator;
import ru.simplex_software.smeta.model.City;
import ru.simplex_software.smeta.model.Element;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.ReportElement;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;
import ru.simplex_software.smeta.model.Work;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class ReportServlet implements HttpRequestHandler {

    private static Logger LOG = LoggerFactory.getLogger(ReportServlet.class);

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private CityDAO cityDAO;

    @Autowired
    private MaterialDAO materialDAO;

    @Autowired
    private WorkDAO workDAO;

    @Autowired
    private TaskFilterImplDAO taskFilterImplDAO;

    @Autowired
    private TaskFilterDAO taskFilterDAO;

    @Autowired
    private PriceDepartureDAO priceDepartureDAO;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final ReportCreator reportCreator = new ReportCreator();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");

        ServletOutputStream outputStream = response.getOutputStream();

        TaskFilter filter = taskFilterDAO.findAllFilters().get(0);
        List<ReportElement> reportElements = removeEmptyReportElement(getReportElements(filter));

        try {
            setDeparture(reportElements);
            reportCreator.copyFromTemplateTask(reportElements, priceDepartureDAO.findAllDepartures());
            reportCreator.copyFromTemplateHeader(reportElements, filter);
            reportCreator.copyFromTemplateFooter();
            reportCreator.write(outputStream);
        } catch (InvalidFormatException e) {
            LOG.error(e.getMessage());
        } catch (ParseException e) {
            LOG.error(e.getMessage());
        }

    }

    private List<ReportElement> removeEmptyReportElement(List<ReportElement> reportElements) {
        List<ReportElement> removeReportElements = new ArrayList<>();
        for (ReportElement reportElement : reportElements) {
            if (reportElement.getWorks().isEmpty() && reportElement.getMaterials().isEmpty()) {
                removeReportElements.add(reportElement);
            }
        }
        reportElements.removeAll(removeReportElements);
        return reportElements;
    }

    private List<ReportElement> getReportElements(TaskFilter filter) {
        final List<ReportElement> reportElements = new ArrayList<>();

        final List<Object[]> shopNameAndCityList = taskFilterImplDAO.findShopNameAndCity(filter);
        for (Object[] tObjects : shopNameAndCityList) {
            final ReportElement reportElement = new ReportElement();

            final String shopName = String.valueOf(tObjects[0]);
            reportElement.setShopName(shopName);

            final Long cityID = (Long) tObjects[1];
            City city = cityDAO.findCityByID(cityID);
            reportElement.setCity(city);

            final List<Task> mergedTasks =
                    taskFilterImplDAO.findOrderNumberByShopNameAndCity(filter, shopName, cityID);
            reportElement.setMergedTasks(mergedTasks);
            LOG.info(String.valueOf(reportElement.getMergedTasks()));

            final List<Work>  works = workDAO.findWorkByShopName(shopName, cityID);
            addElements(works, reportElement, true);

            final List<Material>  materials = materialDAO.findMaterialByShopName(shopName, cityID);
            addElements(materials, reportElement, false);

            reportElements.add(reportElement);
        }
        return reportElements;
    }

    private void addElements(List<? extends Element> elements, ReportElement reportElement, boolean checkElem) {
        LOG.debug(String.valueOf(elements.size()));
        addNewElements(elements, reportElement, checkElem);
    }

    private void addNewElements(List<? extends Element> inElements,
                                ReportElement reportElement, boolean checkElem) {
        for (Element elem : inElements) {
            Element element;
            if (checkElem) {
                element = addNewElement(new Work(), elem);
                reportElement.getWorks().add((Work) element);
            } else {
                element = addNewElement(new Material(), elem);
                reportElement.getMaterials().add((Material) element);
            }
        }
    }

    private Element addNewElement(Element newElement, Element oldElement) {
        newElement.setName(oldElement.getName());
        newElement.setUnits(oldElement.getUnits());
        newElement.setQuantity(oldElement.getQuantity());
        newElement.setUnitPrice(oldElement.getUnitPrice());
        newElement.setAmount(oldElement.getAmount());
        return newElement;
    }

    private void setDeparture(List<ReportElement> reportElements) {
        for (ReportElement reportElement : reportElements) {
            final List<Task> taskList = reportElement.getMergedTasks();
            Task beginTask = taskList.get(0);
            beginTask.setDeparture(true);
            if (beginTask.getCompletedDate() != null) {
                addDepartureByList(taskList, beginTask);
            }
        }
    }

    private void addDepartureByList(List<Task> taskList, Task beginTask) {
        LocalDateTime toDate = beginTask.getCompletedDate().plus(10, ChronoUnit.HOURS);
        for (Task innerTask : taskList.subList(1, taskList.size())) {
            final LocalDateTime taskCDT = innerTask.getCompletedDate();
            toDate = addDepartureByDateTime(taskCDT, toDate, innerTask);
        }
    }

    private LocalDateTime addDepartureByDateTime(LocalDateTime taskCDT, LocalDateTime toDate, Task innerTask) {
        if (taskCDT != null) {
            Task newTask;
            if (taskCDT.isAfter(toDate)) {
                newTask = innerTask;
                newTask.setDeparture(true);
                toDate = newTask.getCompletedDate().plus(10, ChronoUnit.HOURS);
            }
        }
        return toDate;
    }

}
