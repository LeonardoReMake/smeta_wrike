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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    }

    public void copyFromTemplateHeader() throws InvalidFormatException {
        template = getTemplateHeader();
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        for (rowI = 0; rowI < ConstantsOfReport.COUNT_ROWS_HEADER; rowI++) {
            Row row = sheet.createRow(rowI);
            Row tRow = tSheet.getRow(rowI);
            copyRowStyle(tRow, row);
            for (int cellI = 0; cellI < ConstantsOfReport.COUNT_CELLS_ESTIMATE; cellI++) {
                sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
                Cell tCell = tRow.getCell(cellI);
                Cell cell = row.createCell(cellI);
                copyCell(tCell, cell);
            }
        }
        Row row = sheet.getRow(ConstantsOfReport.ROW_NUM_AMOUNT_HEADER);
        Cell cell = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_HEADER);
        cell.setCellValue(getAmountsOfEstimate(workAmountList, materialAmountList) + "р.");
    }

    public void copyFromTemplateTask(List<Task> tasks)  throws InvalidFormatException {
        template = getTemplateTasks();
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);

        freeRowPosition = 12;

        int workRowPosition;
        int materialRowPosition;

        for (final Task task : tasks) {

            double worksAmount = 0;
            double materialsAmount = 0;

            Row row = sheet.createRow(freeRowPosition);
            Row tRow = tSheet.getRow(ConstantsOfReport.INDEX_SHEET);
            copyRowStyle(tRow, row);

            sheet.addMergedRegion((new CellRangeAddress(freeRowPosition, freeRowPosition,
                                    ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK,
                                    ConstantsOfReport.CELL_NUM_LAST_FOR_TASK)));

            Cell cellTask = row.createCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);
            cellTask.setCellValue(task.getName());
            Cell tCell = tRow.getCell(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);
            copyCell(tCell, cellTask);

            workRowPosition = freeRowPosition;
            materialRowPosition = freeRowPosition;

            for(Work work : task.getWorks()) {
                double workAmount = work.getAmount();
                row = sheet.createRow(workRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
                copyRowStyle(tSheet.getRow(ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK), row);
                createTaskElementCell(row,1, work.getName());
                createTaskElementCell(row, 2, work.getUnits());
                createTaskElementCell(row, 3, String.valueOf(work.getQuantity()));
                createTaskElementCell(row, 4, String.valueOf(work.getUnitPrice()));
                createTaskElementCell(row, 5, String.valueOf(workAmount));
                worksAmount += workAmount;
                workRowPosition++;
            }
            workAmountList.add(worksAmount);
            workRowPosition++;

            if (workRowPosition > freeRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK) {
                row = sheet.createRow(workRowPosition);
                tRow = tSheet.getRow(ConstantsOfReport.ROW_POSITION_AMOUNT_FOR_WORK);
                copyRowStyle(tRow, row);
                createTaskElementCell(row, ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK, "Итого");
                createTaskElementCell(row, ConstantsOfReport.CELL_NUM_AMOUNT_FOR_WORK, String.valueOf(worksAmount));
            }

            for(Material material : task.getMaterials()) {
                double materialAmount = material.getAmount();
                if (materialRowPosition > workRowPosition) {
                    row = sheet.createRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
                } else {
                    row = sheet.getRow(materialRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK);
                }
                copyRowStyle(tSheet.getRow(ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK), row);
                createTaskElementCell(row,6, material.getName());
                createTaskElementCell(row, 7, material.getUnits());
                createTaskElementCell(row, 8, String.valueOf(material.getQuantity()));
                createTaskElementCell(row, 9, String.valueOf(material.getUnitPrice()));
                createTaskElementCell(row, 10, String.valueOf(materialAmount));
                materialsAmount += materialAmount;
                materialRowPosition++;
            }
            materialAmountList.add(materialsAmount);
            materialRowPosition++;

            if (materialRowPosition > freeRowPosition + ConstantsOfReport.ROW_NUM_FIRST_FOR_TASK) {
                if (materialRowPosition > workRowPosition) {
                    row = sheet.createRow(materialRowPosition);
                } else {
                    row = sheet.getRow(materialRowPosition);
                }

                tRow = tSheet.getRow(ConstantsOfReport.TOTAL_NUM_ROW_AMOUNT_FOR_ELEMENT);
                copyRowStyle(tRow, row);
                createTaskElementCell(row,ConstantsOfReport.CELL_NUM_AMOUNT_FOR_WORK +
                                             ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK, "Итого");
                createTaskElementCell(row, ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE, String.valueOf(materialsAmount));
            }

            if (workRowPosition < materialRowPosition) {
                freeRowPosition = materialRowPosition;
            } else {
                freeRowPosition = workRowPosition;
            }

            freeRowPosition++;
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

    public void copyFromTemplateFooter() throws InvalidFormatException {
        final int rowPositionOfEstimate = 5;
        final int maxRowPositionOfFooter = 16;
        template = getTemplateFooter();
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);

        for (rowI = freeRowPosition + ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK; rowI < freeRowPosition + maxRowPositionOfFooter; rowI++) {
            Row row = sheet.createRow(rowI);
            Row tRow = tSheet.getRow(rowI - (freeRowPosition + ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK));
            if (tRow != null) {
                copyRowStyle(tRow, row);
                for (int cellI = 0; cellI < ConstantsOfReport.COUNT_CELLS_ESTIMATE; cellI++) {
                    sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
                    Cell tCell = tRow.getCell(cellI);
                    Cell cell = row.createCell(cellI);
                    copyCell(tCell, cell);
                }
            }
        }

        double amountWorks = getAmountOfElements(workAmountList);
        double amountMaterials = getAmountOfElements(materialAmountList);

        Row row = sheet.getRow(freeRowPosition + ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK);

        Cell cellAmountOfWorks = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_FOR_WORK);
        cellAmountOfWorks.setCellValue(String.valueOf(amountWorks));

        Cell cellAmountOfMaterials = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE);
        cellAmountOfMaterials.setCellValue(String.valueOf(amountMaterials));

        double amountOfEstimate = amountMaterials + amountWorks;

        row = sheet.getRow(freeRowPosition + rowPositionOfEstimate);

        Cell cellAmountOfEstimate = row.getCell(ConstantsOfReport.CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE);
        cellAmountOfEstimate.setCellValue(String.valueOf(amountOfEstimate));

    }

    public void createTaskElementCell(Row row, int i, String value){
        Cell cell = row.createCell(i);
        Sheet tSheet = template.getSheetAt(ConstantsOfReport.INDEX_SHEET);
        copyCell(tSheet.getRow(ConstantsOfReport.CELL_NUM_FIRST_FOR_TASK).getCell(i), cell);
        cell.setCellValue(value);
    }

    public void write() {
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("workbook.xls");
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        }
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
            }

            if (from.getCellTypeEnum().equals(CellType.FORMULA)) {
                to.setCellType(CellType.FORMULA);
                to.setCellValue(from.getCellFormula());
            }

        }
    }

}
