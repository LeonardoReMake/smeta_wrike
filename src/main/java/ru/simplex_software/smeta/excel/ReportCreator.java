package ru.simplex_software.smeta.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.smeta.model.Element;
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.PriceDeparture;
import ru.simplex_software.smeta.model.ReportElement;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.TaskFilter;
import ru.simplex_software.smeta.model.Work;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportCreator {

    private static Logger LOG = LoggerFactory.getLogger(ReportCreator.class);

    private static final String TEMPLATE_HEADER_PATH = "template/header_template.xlsx";

    private static final String TEMPLATE_TASKS_PATH = "template/task_template.xlsx";

    private static final String TEMPLATE_FOOTER_PATH = "template/footer_template.xlsx";

    private final DecimalFormat decimalFormat = new DecimalFormat("###,###.###");

    private final int[] workCells = {1, 2, 3, 4, 5};

    private final int[] materialCells = {6, 7, 8, 9, 10};

    private List<Double> workAmountList = new ArrayList<>();

    private List<Double> materialAmountList = new ArrayList<>();

    private Workbook wb;

    private Sheet sheet;

    private Workbook template;

    // указатель на строчку всего отчета.
    private int freeRowPosition = 0;

    private int dayDepartures = 0;

    private int nightDepartures = 0;

    private int departures = 0;

    private int totalDayDepartures = 0;

    private int totalNightDepartures = 0;

    private double totalAmountDayDepOfReportElem = 0;

    private double totalAmountNightDepOfReport = 0;

    private double totalAmountDepOfReportElem = 0;

    private  double totalAmountDepartures = 0;

    private double totalAmountDayDepartures = 0;

    private double totalAmountNightDepartures = 0;

    private int totalDepartures = 0;

    public ReportCreator() {
        wb = new XSSFWorkbook();
        sheet = wb.createSheet();
        sheet.setDisplayGridlines(false);
    }

    // метод копирования заголовков
    public void copyFromTemplateHeader(List<ReportElement> reportElements, TaskFilter taskFilter) throws InvalidFormatException, ParseException {
        template = getTemplateHeader();
        int numberNumber = 0;
        int numberOfReport = 0;

        final Locale russianLocale = new Locale.Builder().setLanguage("ru").build();
        final Date dateBegin = new Date();
        Date dateEnd = taskFilter.getEndDate();

        final String monthBegin = DateFormat.getDateInstance(SimpleDateFormat.LONG, russianLocale).format(dateBegin);
        if (dateEnd == null) {
            dateEnd = dateBegin;
        }
        final String monthEnd = DateFormat.getDateInstance(SimpleDateFormat.LONG, russianLocale).format(dateEnd);

        final StringBuilder contractBuilder = new StringBuilder().append(" к Договору подряда №")
                                                                 .append(numberOfReport)
                                                                 .append(" ")
                                                                 .append(monthBegin);

        final StringBuilder localCalcBuilder = new StringBuilder().append("Локальный сметный расчет № ")
                                                                  .append(numberNumber)
                                                                  .append(contractBuilder);

        final StringBuilder estimatePerBuilder = new StringBuilder().append("Отчетный период с ")
                                                                    .append(monthBegin)
                                                                    .append(" по ")
                                                                    .append(monthEnd);

        copyStylesOfElement(getTemplateHeader(), ConstantsOfReport.INDEX_SHEET,
                            ConstantsOfReport.COUNT_ROWS_HEADER, ConstantsOfReport.INDEX_SHEET,
                            ConstantsOfReport.COUNT_CELLS_ESTIMATE, ConstantsOfReport.INDEX_SHEET, true);

        final Cell cellContract = addCellType(  ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                                                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                                                CellType.STRING );
        sheet.addMergedRegion((new CellRangeAddress( ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK,
                                                     ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK,
                                                     ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT,
                                                     ConstantsOfReport.CELL_NUM_LAST_FOR_TASK )));
        cellContract.setCellValue(String.valueOf(contractBuilder)); // добавление заголовка в правом верхнем углу.
        LOG.info("добавление заголовка в правом верхнем углу: " + localCalcBuilder);

        final Cell cellLocalEstimate = addCellType( ConstantsOfReport.ROW_FOR_HEADER_ESTIMATE,
                                                    ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT,
                                                    CellType.STRING );
        sheet.addMergedRegion((new CellRangeAddress( ConstantsOfReport.ROW_FOR_HEADER_ESTIMATE,
                                                     ConstantsOfReport.ROW_FOR_HEADER_ESTIMATE,
                                                     ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT,
                                                     ConstantsOfReport.CELL_NUM_LAST_FOR_TASK )));
        cellLocalEstimate.setCellValue(String.valueOf(localCalcBuilder)); // Главные заголовок по середине вверху.
        LOG.info("Главные заголовок по середине вверху: " + localCalcBuilder);

        final Cell cellEstimatePeriod = addCellType( ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                                                     ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT,
                                                     CellType.STRING );

        sheet.addMergedRegion((new CellRangeAddress( ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                                                     ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                                                     ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT,
                                                     ConstantsOfReport.CELL_NUM_END_FOR_TITLE )));
        cellEstimatePeriod.setCellValue(String.valueOf(estimatePerBuilder));
        LOG.info("Главные заголовок по середине внизу: : " + estimatePerBuilder);

        /*строки с суммами*/

        /* всего по смете без НДС */
        final double amountReports = getTotalAmount(reportElements);

        /* НДС = (всего по смете без НДС + Всего выездов на сумму без НДС) * 0.18 */
        final double vat = (amountReports + totalAmountDepartures) * 0.18;

        /* Всего по смете с НДС  =  всего по смете без НДС + Всего выездов на сумму без НДС + НДС*/
        final double estimateWithVAT = amountReports + totalAmountDepartures + vat;

        final Cell cellEstimateAmount = addCellType(ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellEstimateAmount.setCellValue(decimalFormat.format(amountReports)
                                        + ConstantsOfReport.RU_STRING);
        LOG.info("всего по смете без НДС: " + amountReports);

        /* Колонка "Всего выездов на сумму без НДС" */
        final Cell cellVAT = addCellType(ConstantsOfReport.ROW_FOR_VAT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_VAT,
                                                    ConstantsOfReport.ROW_FOR_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellVAT.setCellValue(decimalFormat.format(totalAmountDepartures) + ConstantsOfReport.RU_STRING);
        LOG.info("Всего выездов на сумму без НДС: " + totalAmountDepartures);

        /* Колонка "Всего по смете с НДС" */
        final Cell cellEstimateWithVAT = addCellType(ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.STRING);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                                                    ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellEstimateWithVAT.setCellValue(decimalFormat.format(estimateWithVAT));
        LOG.info("Всего по смете с НДС: " + estimateWithVAT);
    }

    /* Получает все суммы */
    private double getTotalAmount(List<ReportElement> reportElements) {
        double amounts = 0;
        for (ReportElement reportElement : reportElements) {
            List<Task> tasks = reportElement.getMergedTasks();
            for (Task task : tasks) {
                if (task.getAmount() != null)
                    amounts += task.getAmount();
            }
        }
        return amounts;
    }

    // метод копирования заданий
    public void copyFromTemplateTask(List<ReportElement> reportElements, PriceDeparture priceDeparture)
                                                            throws InvalidFormatException, ParseException {
        template = getTemplateTasks();
        final Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        int workRowPosition; // Указатель строк на работы
        int materialRowPosition; // Указатель строк на материалы
        freeRowPosition = 11; // по-умолчанию начинаем с 11 строки

        for (ReportElement reportElement : reportElements) {
            final List<Task> taskList = reportElement.getMergedTasks();
            double worksAmount = getAmount(reportElement.getWorks());
            double materialsAmount = getAmount(reportElement.getMaterials());

            Row tRow = tSheet.getRow(ConstantsOfReport.INDEX_SHEET);
            Row row = sheet.createRow(freeRowPosition);
            workAmountList.add(worksAmount);
            materialAmountList.add(materialsAmount);

            sheet.addMergedRegion((new CellRangeAddress(freeRowPosition, freeRowPosition,
                                                        ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT,
                                                        ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
            // создаем заголовок задачи
            createHeaderTaskFromTemplate(row, tRow, reportElement);
            // работам и материалам присваеваем свободной строке
            workRowPosition = freeRowPosition;
            materialRowPosition = freeRowPosition;

            // создаем работы и материалы.
            workRowPosition = createReportWork(reportElement, workRowPosition);
            materialRowPosition = createReportMaterial(reportElement, materialRowPosition, workRowPosition);
            // создает буфферные(промежуточные) строки для работ или материалов
            if (matchingRowPosition(workRowPosition, materialRowPosition)) {
                workRowPosition = createRowsForElement(workRowPosition, materialRowPosition, workCells);
            } else {
                materialRowPosition = createRowsForElement(materialRowPosition, workRowPosition, materialCells);
            }

            // общее колчиество суммы выездов
            totalAmountDepOfReportElem = 0;
            // общее количество дневных выездов
            totalAmountDayDepOfReportElem = 0;
            //общее количество ночных выездов
            totalAmountNightDepOfReport = 0;

            // дневные выезды
            dayDepartures = 0;
            // ночные выезды
            nightDepartures = 0;
            // общее количество выездов
            departures = 0;

            double dayTimePrice = priceDeparture.getDayTimePrice();
            LOG.info("Цена дневного выезда: " + dayTimePrice);

            double nightlyTimePrice = priceDeparture.getNightlyTimePrice();
            LOG.info("Цена ночного выезда: " + nightlyTimePrice);
            setDateByDepartures(taskList, dayTimePrice, nightlyTimePrice);

            // создаем сумму для работ, материалов и итог репорта.
            freeRowPosition = createAmountForWorks(workRowPosition, tSheet, worksAmount);
            createAmountForMaterials(materialRowPosition, tSheet, materialsAmount);
        }
    }

    // создает строки для элемента(либо работы, либо материалов в зависимости от перестановки аргументов)
    // и возвращает свободную строку
    private int createRowsForElement(int elementOne, int elementTwo, final int[] partCells) {
        Row row;
        while (elementOne < elementTwo) {
            row = sheet.getRow(elementOne);
            createTaskCells(partCells, null, row);
            elementOne++;
        }
        return elementOne;
    }

    // устанавливает дату для выезов.
    private void setDateByDepartures(List<Task> taskList, double dayPriceDep, double nightPriceDep) {
        LocalDateTime startCompletedDate = taskList.get(0).getCompletedDate();
        for (Task task : taskList) {;
            final List<Work> workList = task.getWorks();
            final List<Material> materialList = task.getMaterials();
            startCompletedDate = getStartCompletedDate(workList, materialList, task, startCompletedDate);
        }
        counterDepartures(startCompletedDate);
        counterTotalAmountDepartures(dayPriceDep, nightPriceDep);
        counterTotalDepartures();
        counterAmountDepartures();
    }

    private void counterDepartures(LocalDateTime startCompletedDate) {
        if (isNightDeparture(startCompletedDate))
            nightDepartures++;
        else
            dayDepartures++;
        departures = dayDepartures + nightDepartures;
    }

    // получает дату для сравнения в методе установки даты.
    private LocalDateTime getStartCompletedDate(List<Work> workList, List<Material> materialList,
                                      Task task, LocalDateTime startCompletedDate) {
        if (!workList.isEmpty() || !materialList.isEmpty()) {
            final LocalDateTime completedDate = task.getCompletedDate();
            startCompletedDate = pointerDateForDeparture(startCompletedDate, completedDate);
        }
        return startCompletedDate;
    }

    // возвращает указатель на дату выезда.
    private LocalDateTime pointerDateForDeparture(LocalDateTime startCompletedDate, LocalDateTime completedDate) {
        if (isNightDeparture(startCompletedDate)) {
            if (startCompletedDate.isBefore(completedDate.minusHours(ConstantsOfReport.NIGHT_DEPARTURE_INTERVAL))) {
                nightDepartures++;
                startCompletedDate = completedDate;
            }
        } else {
            if (startCompletedDate.isBefore(completedDate.minusHours(ConstantsOfReport.NIGHT_DEPARTURE_INTERVAL))) {
                dayDepartures++;
                startCompletedDate = completedDate;
            }
        }
        return startCompletedDate;
    }

    // счетчик итого сумм выезов
    private void counterTotalAmountDepartures(double dayPriceDep, double nightPriceDep) {
        totalAmountDayDepOfReportElem = dayDepartures * dayPriceDep;
        totalAmountNightDepOfReport = nightDepartures * nightPriceDep;
        totalAmountDepOfReportElem = totalAmountDayDepOfReportElem + totalAmountNightDepOfReport;
    }

    // общий суммы выездов
    private void counterTotalDepartures() {
        totalDayDepartures += dayDepartures;
        totalNightDepartures += nightDepartures;
        totalDepartures += departures;
    }

    //счетчик суммы выездов
    private void counterAmountDepartures() {
        totalAmountDayDepartures += totalAmountDayDepOfReportElem;
        totalAmountNightDepartures += totalAmountNightDepOfReport;
        totalAmountDepartures = totalAmountDayDepartures + totalAmountNightDepartures;
    }

    // проверяет какому дню суток принадлежит выезд
    private boolean isNightDeparture(LocalDateTime completedDate) {
        final int morning = 6;
        final int evening = 20;
        if ((completedDate.getHour() < morning || completedDate.getHour() >= evening)) {
            return true;
        } else {
            return false;
        }
    }

    // создает работу.
    private int createReportWork(ReportElement reportElement, int workRowPosition) {
        int newWorkRowPosition = createWorks(reportElement, workRowPosition);
        return newWorkRowPosition + 1;
    }

    // создает материалы.
    private int createReportMaterial(ReportElement reportElement, int materialRowPosition, int workRowPosition) {
        int newMaterialPosition = createMaterials(reportElement, materialRowPosition, workRowPosition);
        return newMaterialPosition + 1;
    }

    // метод копирование подвала
    public void copyFromTemplateFooter() throws InvalidFormatException {
        final int rowPositionOfEstimate = 4;
        final int rowPosOfEstimateWithDep = 5;
        final int maxRowPositionOfFooter = 16;

        copyStylesOfElement(getTemplateFooter(), freeRowPosition,
                freeRowPosition + maxRowPositionOfFooter,
                ConstantsOfReport.INDEX_SHEET,
                ConstantsOfReport.COUNT_CELLS_ESTIMATE,
                freeRowPosition, false);

        double amountWorks = getAmountOfElements(workAmountList);
        double amountMaterials = getAmountOfElements(materialAmountList);

        Row row = sheet.getRow(freeRowPosition);
        setTotalsForFooter(row, amountWorks, amountMaterials);


        double amountOfEstimate = amountMaterials + amountWorks;

        row = sheet.getRow(freeRowPosition + rowPositionOfEstimate);

        final Cell cellAmountOfEstimate = addCellType(row.getRowNum(),
                            ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                            CellType.NUMERIC);

        final Cell cellAmountOfEstimateWithDep = addCellType(row.getRowNum(),
                            ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                            CellType.NUMERIC);

        setAmountsForFooter(row, rowPositionOfEstimate, rowPosOfEstimateWithDep,
                            cellAmountOfEstimate, cellAmountOfEstimateWithDep);

        final double amountOfEstimateWithoutDepartures = getAmountsOfEstimate(workAmountList, materialAmountList);

        cellAmountOfEstimate.setCellValue(decimalFormat.format(amountOfEstimate));
        cellAmountOfEstimateWithDep.setCellValue(decimalFormat.format(amountOfEstimateWithoutDepartures));
    }

    // устанавливает итог для подвала.
    private void setTotalsForFooter(Row row, double amountWorks, double amountMaterials) {
        final Cell cellAmountOfWorks = addCellType(row.getRowNum(),
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_WORK,
                CellType.NUMERIC);
        cellAmountOfWorks.setCellValue(decimalFormat.format(amountWorks));

        final Cell cellAmountOfMaterials = addCellType( row.getRowNum(),
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE + 1,
                CellType.NUMERIC);
        cellAmountOfMaterials.setCellValue(decimalFormat.format(amountMaterials));

        final Cell cellTotalDep = addCellType(row.getRowNum() + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE + 1,
                CellType.NUMERIC);
        final StringBuilder departuresBuilder = new StringBuilder().append(totalDepartures)
                .append(" (")
                .append(totalDayDepartures)
                .append("/")
                .append(totalNightDepartures)
                .append(")");
        cellTotalDep.setCellValue(String.valueOf(departuresBuilder));

        final Cell cellAmountTotalDep = addCellType(row.getRowNum() + 2,
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE + 1,
                CellType.NUMERIC);
        final StringBuilder amountTotalBuilder = new StringBuilder().append(decimalFormat.format(totalAmountDepartures))
                .append("(")
                .append(totalAmountDayDepartures)
                .append("/")
                .append(totalAmountNightDepartures)
                .append(")");
        cellAmountTotalDep.setCellValue(String.valueOf(amountTotalBuilder));
    }

    // устанавливает суммы(ниже итогов) для сметы в подвале.
    private void setAmountsForFooter(Row row, int rowPositionOfEstimate, int rowPosOfEstimateWithDep,
                                     Cell cellAmountOfEstimate, Cell cellAmountOfEstimateWithDep) {
        row = sheet.getRow(freeRowPosition + rowPositionOfEstimate);

        sheet.addMergedRegion((new CellRangeAddress(row.getRowNum(),
                row.getRowNum(),
                ConstantsOfReport.CELL_NUM_END_FOR_TITLE,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));

        sheet.addMergedRegion((new CellRangeAddress(row.getRowNum() + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                row.getRowNum() + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_END_FOR_TITLE,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
    }
    
    // Записывает в файл
    public void write(OutputStream fileOut) {
        try {
            wb.write(fileOut);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }finally {
            try {
                fileOut.close();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    // сравнение количества строк по позиции работ и материалов.
    private boolean matchingRowPosition(int workRowPosition, int materialRowPosition) {
        freeRowPosition = workRowPosition < materialRowPosition ? materialRowPosition : workRowPosition;
        return workRowPosition < materialRowPosition;
    }

    // Добавляется сумма работ.
    private int createAmountForWorks(int workRowPosition, Sheet tSheet, double worksAmount) {
        final int cellStart = 1;
        final int cellEnd = 6;
        final int numericCell = 5;

        for (int i = ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK;
             i < ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK + 3; i++) {
            final Row row = sheet.createRow(workRowPosition);
            final Row tRow = tSheet.getRow(i);

            copyStylesCellsOfElement(cellStart, cellEnd, tRow, row, tSheet);
            addValueForAmountWorks(i, row, numericCell, worksAmount);
            workRowPosition++;
        }
        return workRowPosition;
    }

    // добавляет значение для суммы работ
    private void addValueForAmountWorks(int i, Row row, int numericCell, double worksAmount) {
        if (i == ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK) {
            row.getCell(numericCell).setCellValue(decimalFormat.format(worksAmount));
        }
    }

    /**
     * Добавляется сумма материалов...
     * @param materialRowPosition
     * @param tSheet
     * @param materialsAmount
     */
    private void createAmountForMaterials(int materialRowPosition, Sheet tSheet,
                                          double materialsAmount) throws ParseException {
        final int cellStart = 6;
        final int cellEnd = 11;
        final int numericCell = 10;
        // стринг билдер дневных выездов
        final StringBuilder stateDepartures = new StringBuilder().append(" ( ")
                                                                 .append(dayDepartures)
                                                                 .append("/")
                                                                 .append(nightDepartures)
                                                                 .append(")");
        // стринг билдер суммы выездов
        final StringBuilder stateAmountDepartures = new StringBuilder().append(" (")
                                                                        .append(totalAmountDayDepOfReportElem)
                                                                        .append(",00/")
                                                                        .append(totalAmountNightDepOfReport)
                                                                        .append("0,00)");

        final String[] totals = { decimalFormat.format(materialsAmount),
                                  departures + "" + stateDepartures,
                                  totalAmountDepOfReportElem + "" + stateAmountDepartures };
        copyRowTotals(tSheet, materialRowPosition, cellStart, cellEnd, numericCell, totals);
    }

    // метод копирует итог в материалах.
    private void copyRowTotals(Sheet tSheet, int materialRowPosition,
                               int cellStart, int cellEnd, int numericCell, String[] totals) {
        for (int i = ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK;
             i < ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK + 3; i++) {
            Row row = sheet.getRow(materialRowPosition);
            Row tRow = tSheet.getRow(i);
            copyStylesCellsOfElement(cellStart, cellEnd, tRow, row, tSheet);
            row.getCell(numericCell).setCellValue(totals[i - ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK]);
            materialRowPosition++;
        }
    }

    /** метод заголовка задачи.*/
    private void createHeaderTaskFromTemplate(Row row, Row tRow, ReportElement reportElement) {
        final Cell cellTask = row.createCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT);
        Cell tCell = tRow.getCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK_REPORT);
        copyStyleCell(tCell, cellTask);

        String shopName = "";
        final List<String> orderNumbers = new ArrayList<>();
        shopName = getShopName(shopName, orderNumbers, reportElement);
        LOG.debug("Название магазина: " + shopName);

        String joinOrderNum = getJoinOrderNum(orderNumbers);
        LOG.debug("Номер заявок: " + joinOrderNum);

        final StringBuilder taskStrBuilder = new StringBuilder();
        String cityName = reportElement.getCity().getName();
        LOG.debug("Номер города: " + cityName);

        taskBuilderAppend(taskStrBuilder, shopName, cityName, joinOrderNum);
        cellTask.setCellValue(String.valueOf(taskStrBuilder));
    }

    // Добавляет название и номер заказа в шапку задачи.
    private void taskBuilderAppend(StringBuilder taskBuilder, String shopName, String cityName, String joinOrderNum) {
        taskBuilder.append(shopName)
                    .append("(")
                    .append(cityName)
                    .append(");")
                    .append(" номера заявок: ")
                    .append(joinOrderNum)
                    .append(".");
    }

    // получает название магазина.
    private String getShopName(String shopName, List<String> orderNumbers, ReportElement reportElement) {
        final List<Task> mergedTasks = reportElement.getMergedTasks();
        for (Task task : mergedTasks) {
            shopName = task.getShopName();
            addOrderNumber(task, orderNumbers);
        }
        return shopName;
    }

    // добавляет номер заявок в список.
    private void addOrderNumber(Task task, List<String> orderNumbers) {
        String orderNumber = task.getOrderNumber();
        if (orderNumber != null && orderNumber.length() != 0) {
            orderNumbers.add(orderNumber);
            LOG.info("Номер заявок :" + String.valueOf(orderNumbers));
        }
    }

    // получает список номеров заявок.
    private String getJoinOrderNum(List<String> orderNumbers) {
        final String noOrderNum = "б/н";
        String joinOrderNum;
        if (orderNumbers.isEmpty()) {
            joinOrderNum = noOrderNum;
            LOG.debug("Номер заявок :" + joinOrderNum);
        } else {
            joinOrderNum = String.join(", ", orderNumbers);
            LOG.debug("Номер заявок :" + joinOrderNum);
        }
        return joinOrderNum;
    }

    // создает работы.
    private int createWorks(ReportElement reportElement, int workRowPosition) {
        for (Work work : reportElement.getWorks()) {
            double workAmount = work.getAmount();
            final Row row = sheet.createRow(workRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            final String[] workLineValues = { work.getName(), work.getUnits(),
                                     String.valueOf(work.getQuantity()),
                                     String.valueOf(work.getUnitPrice()),
                                     String.valueOf(workAmount) };
            LOG.debug("название работы: " + work.getName());
            createTaskCells(workCells, workLineValues, row);
            workRowPosition++;
        }
        return workRowPosition;
    }

    // создает ячейки для работ и материалов
    private void createTaskCells(int[] numbers, String[] names, Row row) {
        final int nCells = 5;
        for (int i = 0; i < nCells; i++) {
            if (names != null) {
                createTaskElementCell(numbers[i], names[i], row);
            } else {
                createTaskElementCell(numbers[i], null, row);
            }
        }
    }

    // создает материалы.
    private int createMaterials(ReportElement reportElement, int materialRowPosition, int workRowPosition) {
        for (Material material : reportElement.getMaterials()) {
            double materialAmount = material.getAmount();
            final Row row = createOrGetRow(materialRowPosition, workRowPosition);
            final String[] materialLineValues = { material.getName(), material.getUnits(),
                                     String.valueOf(material.getQuantity()),
                                     String.valueOf(material.getUnitPrice()),
                                     String.valueOf(materialAmount) };
            createTaskCells(materialCells, materialLineValues, row);
            materialRowPosition++;
        }
        return materialRowPosition;
    }

    // метод получает или создает строку.
    private Row createOrGetRow(int materialRowPosition, int workRowPosition) {
        if (materialRowPosition > workRowPosition) {
            return sheet.createRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
        } else {
            return sheet.getRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
        }
    }

    // метод копирует стили ячеек элементов.
    private void copyStylesCellsOfElement(int cellStart, int cellEnd, Row tRow,
                                          Row row, Sheet tSheet) {
        for (int cellI = cellStart; cellI < cellEnd; cellI++) {
            sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
            Cell tCell = tRow.getCell(cellI);
            Cell cell = row.createCell(cellI);
            copyStyleCell(tCell, cell);
        }
    }
    
    // копирует стили элемента(работы и материалов).
    private void copyStylesOfElement(Workbook template, int startRow, int endRow,
                                     int startCell,     int endCell,  int rowShift,
                                     boolean isHeader) {
        final Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        int rowI;
        for (rowI = startRow; rowI < endRow - 1; rowI++) {
            Row row = sheet.createRow(rowI);
            Row tRow = tSheet.getRow(rowI - rowShift);
            tRow = getTrow(tRow, tSheet, rowI, rowShift);
            copyCells(startCell, endCell, tSheet, tRow, row);
            copyRowStyle(rowI, isHeader, tRow, row);
        }
    }
    
    // возвращает строку шаблона.
    private Row getTrow(Row tRow, Sheet tSheet, int rowI, int rowShift) {
        final int numNonCopy = 8;
        if (tRow != null) {
            if (tRow.getRowNum() >= numNonCopy) {
                tRow = tSheet.getRow(rowI - rowShift + 1);
            }
        }
        return tRow;
    }

    // копирует строку сдвинутую на 1 вверх
    private void copyRowStyle(int rowI, boolean isHeader, Row tRow, Row row) {
        if (rowI == ConstantsOfReport.COUNT_ROWS_HEADER - 1 && isHeader) {
            copyRowStyle(tRow, row);
        }
    }

    // копирует ячейки
    private void copyCells(int startCell, int endCell, Sheet tSheet, Row tRow, Row row) {
        for (int cellI = startCell; cellI < endCell; cellI++) {
            sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
            Cell tCell;
            if (tRow != null) {
                tCell = tRow.getCell(cellI);
                Cell cell = row.createCell(cellI);
                copyStyleCell(tCell, cell);
            }
        }
    }

    // получает сумму работ + материалов по 2 спискам работ и материалов
    private double getAmountsOfEstimate(List<Double> worksAmount,
                                        List<Double> materialsAmount) {
        return  getAmountOfElements(worksAmount) +
                getAmountOfElements(materialsAmount);
    }

    // пергруженный метод (получает сумму работ + материалов по 1 спискам работ и материалов)
    private double getAmountOfElements(List<Double> elementAmountList) {
        double amountElements = 0;
        for (Double amount : elementAmountList) {
            amountElements += amount;
        }
        return amountElements;
    }

    // получет сумму работ и материалов по моделям
    private double getAmount(List<? extends Element> elements) {
        double res = 0;
        for (Element element : elements) {
            res += element.getAmount();
        }
        return res;
    }

    // создает ячейку задачи и кладет в нее значение.
    private void createTaskElementCell(int i, String value, Row row) {
        if (row != null) {
            final Cell cell = row.createCell(i);
            final Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
            final Cell tCell = tSheet.getRow(ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK).getCell(i);
            copyStyleCell(tCell, cell);
            cell.setCellValue(value);
        }
    }

    // добавляет тип ячейки.
    private Cell addCellType(int rowNum, int cellNum, CellType cellType) {
        final Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(cellNum);
        cell.setCellType(cellType);
        return cell;
    }

    // получает шаблон заголвков.
    private Workbook getTemplateHeader() {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource(TEMPLATE_HEADER_PATH).getFile()));
        } catch (IOException | InvalidFormatException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    // получает шаблон задач.
    private Workbook getTemplateTasks() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource(TEMPLATE_TASKS_PATH).getFile()));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    // получает шаблон подвала.
    private Workbook getTemplateFooter() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource(TEMPLATE_FOOTER_PATH).getFile()));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    // копирует высоту.
    private void copyRowStyle(Row from, Row to) {
        if (from != null) {
            to.setHeight(from.getHeight());
        }
    }

    // копирует стили ячейки.
    private void copyStyleCell(Cell from, Cell to) {
        if (from != null) {
            final CellStyle newCellStyle = wb.createCellStyle();
            newCellStyle.cloneStyleFrom(from.getCellStyle());
            setBorderCell(newCellStyle, from);
            to.setCellStyle(newCellStyle);
            addValueType(from, to);
        }
    }

    // копирует рамки
    private void setBorderCell(CellStyle newCellStyle, Cell from) {
        newCellStyle.setBorderBottom(from.getCellStyle().getBorderBottomEnum());
        newCellStyle.setBorderLeft(from.getCellStyle().getBorderLeftEnum());
        newCellStyle.setBorderRight(from.getCellStyle().getBorderRightEnum());
        newCellStyle.setBorderTop(from.getCellStyle().getBorderTopEnum());
    }

    // копирует значения ячеек по указанным типам
    private void addValueType(Cell from, Cell to) {
        final CellType cellType = from.getCellTypeEnum();
        if (cellType.equals(CellType.STRING)) {
            to.setCellType(CellType.STRING);
            String string = from.getStringCellValue();
            to.setCellValue(string);
            LOG.info("значение формата строки: " + string);
        } else if (cellType.equals(CellType.FORMULA)) {
            to.setCellType(CellType.FORMULA);
            String formula = from.getCellFormula();
            to.setCellValue(formula);
            LOG.info("значение формата формулы: " + formula);
        } else if (cellType.equals(CellType.NUMERIC)) {
            to.setCellType(CellType.NUMERIC);
            double numeric = from.getNumericCellValue();
            to.setCellValue(numeric);
            LOG.info("значение формата чисел: " + numeric);
        }
    }

}
