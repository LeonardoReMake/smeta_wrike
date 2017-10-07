package ru.simplex_software.smeta.servlet;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import ru.simplex_software.smeta.dao.PriceDepartureDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.dao.TaskFilterDAO;
import ru.simplex_software.smeta.dao.TaskFilterImplDAO;
import ru.simplex_software.smeta.excel.ReportCreator;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class ReportServlet implements HttpRequestHandler {

    private static Logger LOG = LoggerFactory.getLogger(ReportServlet.class);

    @Autowired
    private TaskDAO taskDAO;

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

        try {
            TaskFilter filter = taskFilterDAO.findAllFilters().get(0);
            final List<Task> taskFilterList = taskFilterImplDAO.findTasksByFilter(filter);

            setDeparture(taskFilterList);
            reportCreator.copyFromTemplateTask(taskFilterList, priceDepartureDAO.findAllDepartures());
            reportCreator.copyFromTemplateHeader(taskFilterList, filter);
            reportCreator.copyFromTemplateFooter(taskFilterList);
            reportCreator.write(outputStream);
        } catch (InvalidFormatException e) {
           LOG.error(e.getMessage());
        } catch (ParseException e) {
            LOG.error(e.getMessage());
        }
    }

    private void setDeparture(List<Task> taskFilterList) {
        Task t;
        t= taskFilterList.get(0);
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
