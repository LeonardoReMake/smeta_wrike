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
import ru.simplex_software.smeta.model.Material;
import ru.simplex_software.smeta.model.Task;
import ru.simplex_software.smeta.model.Work;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ReportCreator {

    private static Logger LOG = LoggerFactory.getLogger(ReportCreator.class);

    private List<Double> workAmountList = new ArrayList<>();

    private List<Double> materialAmountList = new ArrayList<>();

    private Workbook wb;

    private Sheet sheet;

    private Workbook template;

    private int rowI = 0;

    private int freeRowPosition = 0;

    public ReportCreator() {
        wb = new XSSFWorkbook();
        sheet = wb.createSheet();
        sheet.setDisplayGridlines(false);
    }

    public void copyFromTemplateHeader() throws InvalidFormatException {
        copyStylesOfElement(getTemplateHeader(), 0, ConstantsOfReport.COUNT_ROWS_HEADER,
                            0, ConstantsOfReport.COUNT_CELLS_ESTIMATE, 0, true);
        Row row = sheet.getRow(ConstantsOfReport.ROW_NUM_AMOUNT_HEADER);
        Cell cell = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_HEADER);

        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(getAmountsOfEstimate(workAmountList, materialAmountList) + "р.");
    }

    public void copyFromTemplateTask(List<Task> tasks)  throws InvalidFormatException {
        template = getTemplateTasks();
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);

        freeRowPosition = 12;

        int workRowPosition;
        int materialRowPosition;

        for (Task task : tasks) {
            double worksAmount = 0;
            double materialsAmount = 0;

            Row row = sheet.createRow(freeRowPosition);
            Row tRow = tSheet.getRow(0);

            sheet.addMergedRegion((new CellRangeAddress(freeRowPosition, freeRowPosition,
                                                        ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                                                        ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));

            Cell tCell = tRow.getCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);

            createOneTaskFromTemplate(row, tRow, task, tCell);

            workRowPosition = freeRowPosition;
            materialRowPosition = freeRowPosition;

            createWorks(task, row, workRowPosition, worksAmount);
            workAmountList.add(worksAmount);
            workRowPosition++;
            createAmountForWorks(workRowPosition, row, tRow, tSheet, tCell, worksAmount);

            createMaterials(task, row, materialRowPosition, workRowPosition, materialsAmount);
            materialAmountList.add(materialsAmount);
            materialRowPosition++;
            createAmountForMaterials(materialRowPosition, workRowPosition, row, tRow, tSheet, tCell, materialsAmount);

            matchingRowPosition(workRowPosition, materialRowPosition);

            freeRowPosition++;
        }

    }

    public void copyFromTemplateFooter() throws InvalidFormatException {
        final int rowPositionOfEstimate = 5;
        final int maxRowPositionOfFooter = 16;

        copyStylesOfElement(getTemplateFooter(), freeRowPosition,
                    freeRowPosition + maxRowPositionOfFooter, 0,
                            ConstantsOfReport.COUNT_CELLS_ESTIMATE,
                            freeRowPosition, false);

        double amountWorks = getAmountOfElements(workAmountList);
        double amountMaterials = getAmountOfElements(materialAmountList);

        Row row = sheet.getRow(freeRowPosition + ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);

        Cell cellAmountOfWorks = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_FOR_WORK);
        cellAmountOfWorks.setCellType(CellType.NUMERIC);
        cellAmountOfWorks.setCellValue(String.valueOf(amountWorks));

        Cell cellAmountOfMaterials = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE);
        cellAmountOfMaterials.setCellType(CellType.NUMERIC);
        cellAmountOfMaterials.setCellValue(String.valueOf(amountMaterials));

        double amountOfEstimate = amountMaterials + amountWorks;

        row = sheet.getRow(freeRowPosition + rowPositionOfEstimate);

        Cell cellAmountOfEstimate = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE);
        cellAmountOfEstimate.setCellType(CellType.NUMERIC);
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

    private void createAmountForWorks(int workRowPosition, Row row, Row tRow, Sheet tSheet,
                                      Cell tCell, double worksAmount) {
        if (workRowPosition > freeRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK) {
            final int cellStart = 0;
            final int cellEnd = 6;
            final double numericCell = 5;

            row = sheet.createRow(workRowPosition);
            tRow = tSheet.getRow(ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK);
            copyStylesForElements(cellStart, cellEnd, tRow,
                    row, tCell, numericCell, worksAmount, tSheet);
        }
    }

    private void createAmountForMaterials(int materialRowPosition, int workRowPosition, Row row, Row tRow, Sheet tSheet,
                                            Cell tCell, double materialsAmount) {
        if (materialRowPosition > freeRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK) {
            final int cellStart = 6;
            final int cellEnd = 11;
            final double numericCell = 10;

            if (materialRowPosition > workRowPosition) {
                row = sheet.createRow(materialRowPosition);
            } else {
                row = sheet.getRow(materialRowPosition);
            }
            copyStylesForElements(cellStart, cellEnd, tRow,
                    row, tCell, numericCell, materialsAmount, tSheet);

        }
    }

    private void createOneTaskFromTemplate(Row row, Row tRow, Task task, Cell tCell) {
        Cell cellTask = row.createCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);
        tCell = tRow.getCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);
        copyCell(tCell, cellTask);
        cellTask.setCellValue(task.getName());
    }

    private void createWorks(Task task, Row row, int workRowPosition, double worksAmount) {
        for (Work work : task.getWorks()) {
            double workAmount = work.getAmount();
            row = sheet.createRow(workRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            createTaskElementCell(row, 1, work.getName());
            createTaskElementCell(row, 2, work.getUnits());
            createTaskElementCell(row, 3, String.valueOf(work.getQuantity()));
            createTaskElementCell(row, 4, String.valueOf(work.getUnitPrice()));
            createTaskElementCell(row, 5, String.valueOf(workAmount));

            worksAmount += workAmount;
            workRowPosition++;
        }
    }

    private void createMaterials(Task task, Row row, int materialRowPosition,
                                                     int workRowPosition, double materialsAmount) {
        for(Material material : task.getMaterials()) {
            double materialAmount = material.getAmount();
            if (materialRowPosition > workRowPosition) {
                row = sheet.createRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            } else {
                row = sheet.getRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
            }
            createTaskElementCell(row,6, material.getName());
            createTaskElementCell(row, 7, material.getUnits());
            createTaskElementCell(row, 8, String.valueOf(material.getQuantity()));
            createTaskElementCell(row, 9, String.valueOf(material.getUnitPrice()));
            createTaskElementCell(row, 10, String.valueOf(materialAmount));
            materialsAmount += materialAmount;
            materialRowPosition++;
        }
    }

    private void copyStylesForElements(int cellStart, int cellEnd, Row tRow,
                                       Row row, Cell tCell, double numericCell, double worksAmount, Sheet tSheet) {
        for (int cellI = cellStart; cellI < cellEnd; cellI++) {
            sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
            if (tRow != null) {
                tCell = tRow.getCell(cellI);
            }

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
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        for (rowI = startRow; rowI < endRow; rowI++) {
            Row row = sheet.createRow(rowI);
            Row tRow = tSheet.getRow(rowI - rowShift);
            if (rowI == ConstantsOfReport.COUNT_ROWS_HEADER - 1 && isHeader)
                copyRowStyle(tRow, row);
            for (int cellI = startCell; cellI < endCell; cellI++) {
                sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
                Cell tCell = null;
                if (tRow != null)
                    tCell = tRow.getCell(cellI);
                Cell cell = row.createCell(cellI);
                copyCell(tCell, cell);
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

    private Cell createTaskElementCell(Row row, int i, String value){
        Cell cell = row.createCell(i);
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        copyCell(tSheet.getRow(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK).getCell(i), cell);
        cell.setCellValue(value);
        return cell;
    }

    private Workbook getTemplateHeader() {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource("template/header_template.xlsx").getFile()));
        } catch (IOException e) {
            return null;
        } catch (InvalidFormatException e) {
            return null;
        }
    }

    private Workbook getTemplateTasks() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource("template/task_template.xlsx").getFile()));
        } catch (IOException e) {
            return null;
        }
    }

    private Workbook getTemplateFooter() throws InvalidFormatException {
        try {
            return WorkbookFactory.create(new File(getClass().getClassLoader().getResource("template/footer_template.xlsx").getFile()));
        } catch (IOException e) {
            return null;
        }
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

}
