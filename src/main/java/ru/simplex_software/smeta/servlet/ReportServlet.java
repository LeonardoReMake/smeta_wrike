package ru.simplex_software.smeta.servlet;

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
            final List<Task> taskFilterList = taskFilterImplDAO.findTasksByFilter(filter);

            final List<ReportElement> reportElements = getReportElements(filter);

            /*
            *
            * вызов методов для добавления контента в отчет.
            *
            * */

    }

    private List<ReportElement> getReportElements(TaskFilter filter) {
        final List<ReportElement> reportElements = new ArrayList<>();

        List<Object[]> shopNameAndCityList = taskFilterImplDAO.findShopNameAndCity(filter);
        for (Object[] tObjects : shopNameAndCityList) {
            final ReportElement reportElement = new ReportElement();

            final String shopName = String.valueOf(tObjects[0]);
            reportElement.setShopName(shopName);

            final Long cityID = (Long) tObjects[1];
            City city = cityDAO.findCityByID(cityID);
            reportElement.setCity(city);

            final List<String> orderNumbers =
                    taskFilterImplDAO.findOrderNumberByShopName(filter, shopName, cityID);
            reportElement.setTaskOrders(orderNumbers);

            final List<Work>  works = workDAO.findWorkByShopName(shopName, cityID);
            LOG.debug(String.valueOf(works.size()));
            addNewElements(works, reportElement, true);

            final List<Material>  materials = materialDAO.findMaterialByShopName(shopName, cityID);
            LOG.debug(String.valueOf(materials.size()));
            addNewElements(materials, reportElement, false);

            reportElements.add(reportElement);
        }
        return reportElements;
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

    private void setDeparture(List<Task> taskFilterList) {
        Task t;
        t = taskFilterList.get(0);
        t.setDeparture(true);
        if (t.getCompletedDate() != null) {
            LocalDateTime toDate = t.getCompletedDate().plus(10, ChronoUnit.HOURS);
            for (Task task : taskFilterList.subList(1, taskFilterList.size())) {
                final LocalDateTime taskCDT = task.getCompletedDate();
                if (taskCDT != null) {
                    if (taskCDT.isAfter(toDate)) {
                        t = task; t.setDeparture(true);
                        toDate = t.getCompletedDate().plus(10, ChronoUnit.HOURS);
                    }
                }
            }
        }
    }

}
