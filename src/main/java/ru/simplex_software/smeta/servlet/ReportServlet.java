package ru.simplex_software.smeta.servlet;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
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
import java.util.List;


public class ReportServlet implements HttpRequestHandler {

    private static Logger LOG = LoggerFactory.getLogger(ReportServlet.class);

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private TaskFilterImplDAO taskFilterImplDAO;

    @Autowired
    private TaskFilterDAO taskFilterDAO;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final ReportCreator reportCreator = new ReportCreator();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");

        ServletOutputStream outputStream = response.getOutputStream();

        try {
            TaskFilter filter = taskFilterDAO.findAllFilters().get(0);
            final List<Task> taskFilterList = taskFilterImplDAO.findTasksByFilter(filter);
            reportCreator.copyFromTemplateTask(taskFilterList);
            reportCreator.copyFromTemplateFooter(taskFilterList);
            reportCreator.copyFromTemplateHeader(taskFilterList);
            reportCreator.write(outputStream);
        } catch (InvalidFormatException e) {
           LOG.error(e.getMessage());
        }
    }

}
