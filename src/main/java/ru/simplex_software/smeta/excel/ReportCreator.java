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
import ru.simplex_software.smeta.model.Work;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportCreator {


    private static Logger LOG = LoggerFactory.getLogger(ReportCreator.class);

    private List<Double> workAmountList = new ArrayList<>();

    private List<Double> materialAmountList = new ArrayList<>();

    private Workbook wb;

    private Sheet sheet;

    private Workbook template;

    private int freeRowPosition = 0;

    public ReportCreator() {
        wb = new XSSFWorkbook();
        sheet = wb.createSheet();
        sheet.setDisplayGridlines(false);
    }

    public void copyFromTemplateHeader(List<Task> taskList) throws InvalidFormatException {
        int numberNumber = 0;
        int numberOfReport = 0;
        Date dateOfReport = null;

        double simpleVAT = 0.18;
        double departures = amountDepartures(taskList);
        double estimateWithVAT = departures * simpleVAT;
        double estimateWithoutVAT = estimateWithVAT + departures;

        Date dateBegin = null;
        Date dateEnd = null;

        final String contract = " к Договору подряда №" + numberOfReport + " от "  + dateOfReport;
        final String localCalculation = "Локальный сметный расчет №" + numberNumber + contract;
        final String estimatePeriod = "Отчетный период с " + dateBegin + " июня 2017г. по " + dateEnd
                                                                                        + " июня 2017г.";

        copyStylesOfElement(getTemplateHeader(), ConstantsOfReport.INDEX_SHEET,
                ConstantsOfReport.COUNT_ROWS_HEADER, ConstantsOfReport.INDEX_SHEET,
                ConstantsOfReport.COUNT_CELLS_ESTIMATE, ConstantsOfReport.INDEX_SHEET, true);

        final Cell cellEstimateAmount = addCellType(ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.ROW_FOR_AMOUNT_NOT_VAT,
                                                    ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellEstimateAmount.setCellValue(getAmountsOfEstimate(workAmountList, materialAmountList) + ConstantsOfReport.RU_STRING);

        final Cell cellContract = addCellType(ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                CellType.STRING);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK,
                ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK,
                ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellContract.setCellValue(contract);

        final Cell cellLocalEstimate = addCellType(ConstantsOfReport.ROW_FOR_LOCAL_ESTIMATE,
                ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK, CellType.STRING);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_LOCAL_ESTIMATE,
                ConstantsOfReport.ROW_FOR_LOCAL_ESTIMATE,
                ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellLocalEstimate.setCellValue(localCalculation);

        final Cell cellEstimatePeriod = addCellType(ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK, CellType.STRING);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                ConstantsOfReport.ROW_FOR_ESTIMATE_PERIOD,
                ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                ConstantsOfReport.CELL_NUM_END_FOR_TITLE)));
        cellEstimatePeriod.setCellValue(estimatePeriod);

        final Cell cellVAT = addCellType(ConstantsOfReport.ROW_FOR_VAT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_VAT,
                ConstantsOfReport.ROW_FOR_VAT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellVAT.setCellValue(estimateWithVAT + ConstantsOfReport.RU_STRING);

        final Cell cellDeparture = addCellType(ConstantsOfReport.AMOUNT_DEPARTURES,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.AMOUNT_DEPARTURES,
                ConstantsOfReport.AMOUNT_DEPARTURES,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellDeparture.setCellValue(departures + ConstantsOfReport.RU_STRING);

        final Cell cellEstimateWithVAT = addCellType(ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER, CellType.NUMERIC);
        sheet.addMergedRegion((new CellRangeAddress(ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                ConstantsOfReport.ROW_FOR_VAT_AMOUNT,
                ConstantsOfReport.CELL_NUM_AMOUNT_HEADER,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));
        cellEstimateWithVAT.setCellValue(estimateWithoutVAT);
    }

    private double amountDepartures(List<Task> tasks) {
        final double price = 600;
        double amounts = 0;
        for (Task task : tasks) {
            amounts += task.getAmount();
        }
        return amounts * price;
    }

    public void copyFromTemplateTask(List<Task> tasks)  throws InvalidFormatException {
        template = getTemplateTasks();
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);

        freeRowPosition = 12;

        int workRowPosition;
        int materialRowPosition;

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
            createAmountForWorks(workRowPosition, tSheet, worksAmount);

            int newMaterialRowPosition = createMaterials(task, materialRowPosition, workRowPosition);
            materialRowPosition = newMaterialRowPosition + 1;
            createAmountForMaterials(materialRowPosition, workRowPosition, tSheet, materialsAmount);

            matchingRowPosition(workRowPosition, materialRowPosition);
            freeRowPosition++;
        }

    }

    public void copyFromTemplateFooter() throws InvalidFormatException {
        final int rowPositionOfEstimate = 4;
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
        cellAmountOfWorks.setCellValue(amountWorks);

        final Cell cellAmountOfMaterials = addCellType(row.getRowNum(),
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                CellType.NUMERIC);
        cellAmountOfMaterials.setCellValue(amountMaterials);

        double amountOfEstimate = amountMaterials + amountWorks;

        row = sheet.getRow(freeRowPosition + rowPositionOfEstimate);

        final Cell cellAmountOfEstimate = addCellType(row.getRowNum(),
                ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE,
                CellType.NUMERIC);

        sheet.addMergedRegion((new CellRangeAddress(row.getRowNum(),
                row.getRowNum(),
                ConstantsOfReport.CELL_NUM_END_FOR_TITLE,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));

        sheet.addMergedRegion((new CellRangeAddress(row.getRowNum() + 1,
                row.getRowNum() + 1,
                ConstantsOfReport.CELL_NUM_END_FOR_TITLE,
                ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));

        cellAmountOfEstimate.setCellValue(String.valueOf(amountOfEstimate));

    }

    public void write(OutputStream fileOut) {
        try {
            wb.write(fileOut);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        try {
            fileOut.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void matchingRowPosition(int workRowPosition, int materialRowPosition) {
        if (workRowPosition < materialRowPosition) {
            freeRowPosition = materialRowPosition;
        } else {
            freeRowPosition = workRowPosition;
        }

    }

    private void createAmountForWorks(int workRowPosition, Sheet tSheet, double worksAmount) {
        if (workRowPosition > freeRowPosition) {
            final int cellStart = 1;
            final int cellEnd = 6;
            final double numericCell = 5;

            Row row = sheet.createRow(workRowPosition);
            Row tRow = tSheet.getRow(ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK);
            copyStylesForElements(cellStart, cellEnd, tRow,
                    row, numericCell,
                    worksAmount, tSheet);
        }
    }

    private void createAmountForMaterials(int materialRowPosition, int workRowPosition, Sheet tSheet, double materialsAmount) {
        if (materialRowPosition > freeRowPosition) {
            final int cellStart = 6;
            final int cellEnd = 11;
            final double numericCell = 10;
            Row row;
            if (materialRowPosition > workRowPosition) {
                row = sheet.createRow(materialRowPosition);
            } else {
                row = sheet.getRow(materialRowPosition);
            }
            Row tRow = tSheet.getRow(ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK);

            copyStylesForElements(cellStart, cellEnd, tRow,
                    row, numericCell,
                    materialsAmount, tSheet);
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
            Row row = sheet.createRow(workRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            createTaskElementCell(1, work.getName(), row);
            createTaskElementCell(2, work.getUnits(), row);
            createTaskElementCell(3, String.valueOf(work.getQuantity()), row);
            createTaskElementCell(4, String.valueOf(work.getUnitPrice()), row);
            createTaskElementCell(5, String.valueOf(workAmount), row);
            workRowPosition++;
        }
        return workRowPosition;
    }

    private int createMaterials(Task task, int materialRowPosition, int workRowPosition) {
        for(Material material : task.getMaterials()) {
            double materialAmount = material.getAmount();
            Row row;
            if (materialRowPosition >= workRowPosition) {
                row = sheet.createRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            } else {
                row = sheet.getRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            }
            createTaskElementCell(6, material.getName(), row);
            createTaskElementCell(7, material.getUnits(), row);
            createTaskElementCell(8, String.valueOf(material.getQuantity()), row);
            createTaskElementCell(9, String.valueOf(material.getUnitPrice()), row);
            createTaskElementCell(10, String.valueOf(materialAmount), row);
            materialRowPosition++;
        }
        return materialRowPosition;
    }

    private void copyStylesForElements(int cellStart, int cellEnd, Row tRow,
                                       Row row, double numericCell,
                                       double worksAmount, Sheet tSheet) {
        for (int cellI = cellStart; cellI < cellEnd; cellI++) {
            sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
            Cell tCell = tRow.getCell(cellI);
            Cell cell = row.createCell(cellI);

            copyCell(tCell, cell);
            if (cellI == numericCell) {
                cell.setCellType(CellType.NUMERIC);
                cell.setCellValue(worksAmount);
            }
        }
    }

    private void copyStylesOfElement(Workbook template, int startRow, int endRow,
                                     int startCell, int endCell, int rowShift, boolean isHeader) {
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

    private double getAmountsOfEstimate(List<Double> worksAmount, List<Double> materialsAmount) {
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

    private void createTaskElementCell(int i, String value, Row row){
        Cell cell = row.createCell(i);
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        Cell tCell = tSheet.getRow(ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK).getCell(i);
        copyCell(tCell, cell);
        cell.setCellValue(value);
    }

    private Cell addCellType(int rowNum, int cellNum, CellType cellType) {
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(cellNum);
        cell.setCellType(cellType);
        return cell;
    }

    private Workbook getTemplateHeader() {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource("template/header_template.xlsx").getFile()));
        } catch (IOException | InvalidFormatException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private Workbook getTemplateTasks() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource("template/task_template.xlsx").getFile()));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private Workbook getTemplateFooter() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource("template/footer_template.xlsx").getFile()));
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
            CellStyle newCellStyle = wb.createCellStyle();
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
