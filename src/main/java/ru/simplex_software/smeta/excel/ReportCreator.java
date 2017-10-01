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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportCreator {

    private static Logger LOG = LoggerFactory.getLogger(ReportCreator.class);

    private static final String TEMPLATE_HEADER_PATH = "template/header_template.xlsx";

    private static final String TEMPLATE_TASKS_PATH = "template/task_template.xlsx";

    private static final String TEMPLATE_FOOTER_PATH = "template/footer_template.xlsx";

    private static final double ONE_DEPARTURE = 600;

    private final DecimalFormat decimalFormat = new DecimalFormat("###,###.###");

    private final int[] onePartCells = {1, 2, 3, 4, 5};

    private final int[] twoPartCells = {6, 7, 8, 9, 10};

    private List<Double> workAmountList = new ArrayList<>();

    private List<Double> materialAmountList = new ArrayList<>();

    private Workbook wb;

    private Sheet sheet;

    private Workbook template;

    private int freeRowPosition = 0;

    private int departures = 0;
    
    private double amountDepartures = 0;

    private double totalAmountDepartures = 0;

    private double estimateWithoutVAT = 0;

    public ReportCreator() {
        wb = new XSSFWorkbook();
        sheet = wb.createSheet();
        sheet.setDisplayGridlines(false);
    }

    public void copyFromTemplateHeader(List<Task> taskList, TaskFilter taskFilter) throws InvalidFormatException, ParseException {
        template = getTemplateHeader();
        int numberNumber = 0;
        int numberOfReport = 0;

        final double simpleVAT = 0.18;
        final double departures = getTotalAmount(taskList);
        final double vat = (estimateWithoutVAT + departures) * simpleVAT;

        final double estimateWithVAT = vat + estimateWithoutVAT + departures;
        estimateWithoutVAT = totalAmountDepartures;

        final Locale russianLocale = new Locale.Builder().setLanguage("ru").build();
        final Date dateBegin = new Date();
        Date dateEnd = taskFilter.getEndDate();

        final String monthBegin = DateFormat.getDateInstance(SimpleDateFormat.LONG, russianLocale).format(dateBegin);
        if (dateEnd == null) {
            dateEnd = dateBegin;
        }
        final String monthEnd = DateFormat.getDateInstance(SimpleDateFormat.LONG, russianLocale).format(dateEnd);

        final String contract = " к Договору подряда №" + numberOfReport + " " + monthBegin;
        final String localCalculation = "Локальный сметный расчет № " + numberNumber + contract;
        final String estimatePeriod = "Отчетный период с " + monthBegin +  " по " + monthEnd;

        copyStylesOfElement(getTemplateHeader(), ConstantsOfReport.INDEX_SHEET,
                ConstantsOfReport.COUNT_ROWS_HEADER, ConstantsOfReport.INDEX_SHEET,
                ConstantsOfReport.COUNT_CELLS_ESTIMATE, ConstantsOfReport.INDEX_SHEET, true);

        final Cell cellContract = addCellType(ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                CellType.STRING);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK,
                ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK,
                ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellContract.setCellValue(contract);

        final Cell cellLocalEstimate = addCellType(ConstantsOfReport.ROW_FOR_LOCAL_ESTIMATE,
                                                   ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                                                   CellType.STRING);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_LOCAL_ESTIMATE,
                                                    ConstantsOfReport.ROW_FOR_LOCAL_ESTIMATE,
                                                    ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellLocalEstimate.setCellValue(localCalculation);

        final Cell cellEstimatePeriod = addCellType(ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                                                    ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                                                    CellType.STRING);

        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_END_FOR_TITLE)));
        cellEstimatePeriod.setCellValue(estimatePeriod);

        /*строки с суммами*/

        final Cell cellEstimateAmount = addCellType(ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    CellType.NUMERIC);

        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellEstimateAmount.setCellValue(decimalFormat.format(getAmountsOfEstimate(workAmountList, materialAmountList))
                                        + ConstantsOfReport.RU_STRING);

        final Cell cellDeparture = addCellType(ConstantsOfReport.AMOUNT_DEPARTURES,
                                               ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                               CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.AMOUNT_DEPARTURES,
                                                    ConstantsOfReport.AMOUNT_DEPARTURES,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellDeparture.setCellValue(decimalFormat.format(estimateWithoutVAT) + ConstantsOfReport.RU_STRING);

        final Cell cellVAT = addCellType(ConstantsOfReport.ROW_FOR_VAT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_VAT,
                                                    ConstantsOfReport.ROW_FOR_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellVAT.setCellValue(decimalFormat.format(vat) + ConstantsOfReport.RU_STRING);

        final Cell cellEstimateWithVAT = addCellType(ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.STRING);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                                                    ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellEstimateWithVAT.setCellValue(decimalFormat.format(estimateWithVAT));
    }

    private double getTotalAmount(List<Task> tasks) {
        double amounts = 0;
        for (Task task : tasks) {
            if (task.getAmount() != null)
                amounts += task.getAmount();
        }
        return amounts;
    }

    /* Создание списков заданий. */
    public void copyFromTemplateTask(List<Task> tasks)  throws InvalidFormatException {
        template = getTemplateTasks();
        final Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        int workRowPosition;
        int materialRowPosition;
        freeRowPosition = 12;

        for (Task task : tasks) {
            double worksAmount = getAmount(task.getWorks());
            double materialsAmount = getAmount(task.getMaterials());

            Row tRow = tSheet.getRow(ConstantsOfReport.INDEX_SHEET);
            Row row = sheet.createRow(freeRowPosition);
            workAmountList.add(worksAmount);
            materialAmountList.add(materialsAmount);

            sheet.addMergedRegion((new CellRangeAddress(freeRowPosition, freeRowPosition,
                                                        ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                                                        ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
            createOneTaskFromTemplate(row, tRow, task);
            workRowPosition = freeRowPosition;
            materialRowPosition = freeRowPosition;

            int newWorkRowPosition = createWorks(task, workRowPosition);
            workRowPosition = newWorkRowPosition + 1;

            int newMaterialRowPosition = createMaterials(task, materialRowPosition, workRowPosition);
            materialRowPosition = newMaterialRowPosition + 1;

            if (matchingRowPosition(workRowPosition, materialRowPosition)) {
                while (workRowPosition < materialRowPosition) {
                    row = sheet.getRow(workRowPosition);
                    createTaskCells(onePartCells, null, row);
                    workRowPosition++;
                }
            } else {
                while (materialRowPosition < workRowPosition) {
                    row = sheet.getRow(materialRowPosition);
                    createTaskCells(twoPartCells, null, row);
                    materialRowPosition++;
                }
            }
            freeRowPosition = createAmountForWorks(workRowPosition, tSheet, worksAmount);
            createAmountForMaterials(materialRowPosition, tSheet, materialsAmount, tasks);
        }

    }

    public void copyFromTemplateFooter(List<Task> tasks) throws InvalidFormatException {
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

        final Cell cellAmountOfWorks = addCellType(row.getRowNum(),
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_FOR_WORK,
                                                    CellType.NUMERIC);
        cellAmountOfWorks.setCellValue(decimalFormat.format(amountWorks));

        final Cell cellAmountOfMaterials = addCellType( row.getRowNum(),
                                                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE + 1,
                                                         CellType.NUMERIC);
        cellAmountOfMaterials.setCellValue(decimalFormat.format(amountMaterials));

        int totalDep = (int) (totalAmountDepartures / ONE_DEPARTURE);

        final Cell cellTotalDep = addCellType(row.getRowNum() + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                                              ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE + 1,
                                                      CellType.NUMERIC);
        cellTotalDep.setCellValue( totalDep + " (" + totalDep + "/0)");

        final Cell cellAmountTotalDep = addCellType(row.getRowNum() + 2,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE + 1,
                                                            CellType.NUMERIC);
        cellAmountTotalDep.setCellValue(decimalFormat.format(totalAmountDepartures));

        double amountOfEstimate = amountMaterials + amountWorks;

        row = sheet.getRow(freeRowPosition + rowPositionOfEstimate);

        final Cell cellAmountOfEstimate = addCellType(row.getRowNum(),
                                                        ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                                                        CellType.NUMERIC);

        sheet.addMergedRegion((new CellRangeAddress(row.getRowNum(),
                                                    row.getRowNum(),
                                                    ConstantsOfReport.CELL_NUM_END_FOR_TITLE,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));

        sheet.addMergedRegion((new CellRangeAddress(row.getRowNum() + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                                                    row.getRowNum() + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                                                            ConstantsOfReport.CELL_NUM_END_FOR_TITLE,
                                                            ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));

        row = sheet.getRow(freeRowPosition + rowPosOfEstimateWithDep);

        final Cell cellAmountOfEstimateWithDep = addCellType(row.getRowNum(),
                                                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                                                CellType.NUMERIC);

        final double amountOfEstWD = estimateWithoutVAT + getAmountsOfEstimate(workAmountList, materialAmountList);

        cellAmountOfEstimate.setCellValue(decimalFormat.format( amountOfEstimate));
        cellAmountOfEstimateWithDep.setCellValue(decimalFormat.format(amountOfEstWD));
    }

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

    private boolean matchingRowPosition(int workRowPosition, int materialRowPosition) {
        freeRowPosition = workRowPosition < materialRowPosition ? materialRowPosition : workRowPosition;
        return workRowPosition < materialRowPosition;
    }

    private int createAmountForWorks(int workRowPosition, Sheet tSheet, double worksAmount) {
        final int cellStart = 1;
        final int cellEnd = 6;
        final int numericCell = 5;

        for (int i = ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK;
             i < ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK + 3; i++) {
            final Row row = sheet.createRow(workRowPosition);
            final Row tRow = tSheet.getRow(i);

            copyStylesForElements(cellStart, cellEnd, tRow, row, tSheet);
            if (i == ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK) {
                row.getCell(numericCell).setCellValue(decimalFormat.format(worksAmount));
            }
            workRowPosition++;
        }
        return workRowPosition;
    }

    private void setOneDeparture(List<Task> taskList) {
        departures = 0;
        for (Task newTask : taskList) {
            if (newTask.isDeparture()) {
                departures = 1;
            }
        }
        amountDepartures = departures * ONE_DEPARTURE;
        totalAmountDepartures += amountDepartures;
    }

    /**
     * Добавляется сумма материалов...
     * @param materialRowPosition
     * @param tSheet
     * @param materialsAmount
     * @param taskList
     */
    private void createAmountForMaterials(int materialRowPosition, Sheet tSheet,
                                          double materialsAmount, List<Task> taskList) {
        final int cellStart = 6;
        final int cellEnd = 11;
        final int numericCell = 10;

        setOneDeparture(taskList);

        final String stateDepartures = " ( " + departures + "/0)";
        final String stateAmountDepartures = " (" + amountDepartures + ",00/0,00)";

        final String[] totals = { decimalFormat.format(materialsAmount),
                                  departures + stateDepartures,
                                  amountDepartures + stateAmountDepartures };

        for (int i = ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK;
                i < ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK + 3; i++) {
            Row row = sheet.getRow(materialRowPosition);
            Row tRow = tSheet.getRow(i);
            copyStylesForElements(cellStart, cellEnd, tRow, row, tSheet);
            row.getCell(numericCell).setCellValue(totals[i - ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK]);
            materialRowPosition++;
        }
    }

    private void createOneTaskFromTemplate(Row row, Row tRow, Task task) {
        Cell cellTask = row.createCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);
        Cell tCell = tRow.getCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);
        copyCell(tCell, cellTask);
        cellTask.setCellValue(task.getName());
    }

    private int createWorks(Task task, int workRowPosition) {
        for (Work work : task.getWorks()) {
            double workAmount = work.getAmount();
            final Row row = sheet.createRow(workRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            final String[] names = { work.getName(), work.getUnits(),
                                     String.valueOf(work.getQuantity()),
                                     String.valueOf(work.getUnitPrice()),
                                     String.valueOf(workAmount) };
            createTaskCells(onePartCells, names, row);
            workRowPosition++;
        }
        return workRowPosition;
    }

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

    private int createMaterials(Task task, int materialRowPosition, int workRowPosition) {
        for (Material material : task.getMaterials()) {
            double materialAmount = material.getAmount();
            Row row;
            if (materialRowPosition > workRowPosition) {
                row = sheet.createRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            } else {
                row = sheet.getRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            }
            final String[] names = { material.getName(), material.getUnits(),
                                     String.valueOf(material.getQuantity()),
                                     String.valueOf(material.getUnitPrice()),
                                     String.valueOf(materialAmount) };
            createTaskCells(twoPartCells, names, row);
            materialRowPosition++;
        }
        return materialRowPosition;
    }

    private void copyStylesForElements(int cellStart, int cellEnd, Row tRow,
                                       Row row, Sheet tSheet) {
        for (int cellI = cellStart; cellI < cellEnd; cellI++) {
            sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
            Cell tCell = tRow.getCell(cellI);
            Cell cell = row.createCell(cellI);
            copyCell(tCell, cell);
        }
    }

    private void copyStylesOfElement(Workbook template, int startRow, int endRow,
                                     int startCell,     int endCell,  int rowShift,
                                     boolean isHeader) {
        final Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        int rowI;
        for (rowI = startRow; rowI < endRow; rowI++) {
            Row row = sheet.createRow(rowI);
            Row tRow = tSheet.getRow(rowI - rowShift);
            if (rowI == ConstantsOfReport.COUNT_ROWS_HEADER - 1 && isHeader)
                copyRowStyle(tRow, row);
            for (int cellI = startCell; cellI < endCell; cellI++) {
                sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
                Cell tCell;
                if (tRow != null) {
                    tCell = tRow.getCell(cellI);
                    Cell cell = row.createCell(cellI);
                    copyCell(tCell, cell);
                }
            }
        }
    }

    private double getAmountsOfEstimate(List<Double> worksAmount,
                                        List<Double> materialsAmount) {
        return  getAmountOfElements(worksAmount) +
                getAmountOfElements(materialsAmount);
    }

    private double getAmountOfElements(List<Double> elementAmountList) {
        double amountElements = 0;
        for (Double amount : elementAmountList) {
            amountElements += amount;
        }
        return amountElements;
    }

    private void createTaskElementCell(int i, String value, Row row) {
        final int workBeginI = 2;
        final int workEndI = 6;
        final int materialBeginI = 7;
        final int materialEndI = 11;

        if (row != null) {
            final Cell cell = row.createCell(i);
            final Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
            final Cell tCell = tSheet.getRow(ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK).getCell(i);
            copyCell(tCell, cell);
            if ( value != null && (checkNumericFormatForElement(i,workBeginI, workEndI) ||
                                   checkNumericFormatForElement(i, materialBeginI, materialEndI))) {
                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("#.#"));
                cell.setCellStyle(cellStyle);
                cell.setCellValue(value);
            } else {
                cell.setCellValue(value);
            }
        }
    }

    private boolean checkNumericFormatForElement(int columnI, int elementBeginI, int elementEndI) {
        return ((columnI > elementBeginI && columnI < elementEndI));
    }

    private Cell addCellType(int rowNum, int cellNum, CellType cellType) {
        final Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(cellNum);
        cell.setCellType(cellType);
        return cell;
    }

    private Workbook getTemplateHeader() {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource(TEMPLATE_HEADER_PATH).getFile()));
        } catch (IOException | InvalidFormatException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private Workbook getTemplateTasks() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource(TEMPLATE_TASKS_PATH).getFile()));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private Workbook getTemplateFooter() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource(TEMPLATE_FOOTER_PATH).getFile()));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private void copyRowStyle(Row from, Row to) {
        if (from != null) {
            to.setHeight(from.getHeight());
        }
    }

    private void copyCell(Cell from, Cell to) {
        if (from != null) {
            final CellStyle newCellStyle = wb.createCellStyle();
            newCellStyle.cloneStyleFrom(from.getCellStyle());
            newCellStyle.setBorderBottom(from.getCellStyle().getBorderBottomEnum());
            newCellStyle.setBorderLeft(from.getCellStyle().getBorderLeftEnum());
            newCellStyle.setBorderRight(from.getCellStyle().getBorderRightEnum());
            newCellStyle.setBorderTop(from.getCellStyle().getBorderTopEnum());
            to.setCellStyle(newCellStyle);

            if (from.getCellTypeEnum().equals(CellType.STRING)) {
                to.setCellType(CellType.STRING);
                to.setCellValue(from.getStringCellValue());
            } else if (from.getCellTypeEnum().equals(CellType.FORMULA)) {
                to.setCellType(CellType.FORMULA);
                to.setCellValue(from.getCellFormula());
            } else if (from.getCellTypeEnum().equals(CellType.NUMERIC)) {
                to.setCellType(CellType.NUMERIC);
                to.setCellValue(from.getNumericCellValue());
            }

        }
    }

    private double getAmount(List<? extends Element> elements) {
        double res = 0;
        for (Element element : elements) {
            res += element.getAmount();
        }
        return res;
    }

}
