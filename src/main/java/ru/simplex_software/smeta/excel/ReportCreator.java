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
import java.util.List;

public class ReportCreator {

    private static Logger LOG = LoggerFactory.getLogger(ReportCreator.class);

    private Workbook wb;

    private Sheet sheet;

    private Workbook template;

    private int rowI = 0;

    public ReportCreator() {
        wb = new XSSFWorkbook();
        sheet = wb.createSheet();
    }

    public void copyFromTemplateHeader() throws InvalidFormatException {
        template = getTemplateHeader();
        Sheet tSheet = template.getSheetAt(0);
        for (rowI = 0; rowI < 12; rowI++) {
            Row tRow = tSheet.getRow(rowI);
            Row row = sheet.createRow(rowI);
            copyRowStyle(tRow, row);
            for (int cellI = 0; cellI < tRow.getLastCellNum(); cellI++) {
                if (rowI == 0) {
                    sheet.setColumnWidth(cellI, tSheet.getColumnWidth(cellI));
                }
                Cell tCell = tRow.getCell(cellI);
                Cell cell = row.createCell(cellI);
                if (tCell != null) {
                    copyCell(tCell, cell);
                }
            }
        }
    }

    public void copyFromTemplateTask(List<Task> tasks)  throws InvalidFormatException {
        template = getTemplateTasks();
        Sheet tSheet = template.getSheetAt(0);

        int freeCellsPosition = 12;

        int workCellsPosition;
        int materialCellsPosition;

        for (final Task task : tasks){
            Row row = sheet.createRow(freeCellsPosition);
            Row tRow = tSheet.getRow(0);
            copyRowStyle(tRow, row);

            sheet.addMergedRegion((new CellRangeAddress(freeCellsPosition, freeCellsPosition,1,10)));

            Cell cellTask = row.createCell(1);
            cellTask.setCellValue(task.getName());
            Cell tCell = tRow.getCell(1);
            copyCell(tCell, cellTask);

            workCellsPosition = freeCellsPosition;
            materialCellsPosition = freeCellsPosition;

            for(Work work : task.getWorks()) {
                LOG.info(work.getName());
                row = sheet.createRow(workCellsPosition + 1);
                copyRowStyle(tSheet.getRow(1), row);
                createTaskCell(row,1, work.getName());
                createTaskCell(row, 2, work.getUnits());
                createTaskCell(row, 3, String.valueOf(work.getQuantity()));
                createTaskCell(row, 4, String.valueOf(work.getUnitPrice()));
                createTaskCell(row, 5, String.valueOf(work.getAmount()));
                workCellsPosition++;
            }

            for(Material material : task.getMaterials()) {
                if (materialCellsPosition > workCellsPosition) {
                    row = sheet.createRow(materialCellsPosition + 1);
                } else {
                    row = sheet.getRow(materialCellsPosition + 1);
                }
                copyRowStyle(tSheet.getRow(1), row);
                createTaskCell(row,6, material.getName());
                createTaskCell(row, 7, material.getUnits());
                createTaskCell(row, 8, String.valueOf(material.getQuantity()));
                createTaskCell(row, 9, String.valueOf(material.getUnitPrice()));
                createTaskCell(row, 10, String.valueOf(material.getAmount()));
                materialCellsPosition++;
            }

            if (workCellsPosition < materialCellsPosition) {
                freeCellsPosition = materialCellsPosition;
            } else {
                freeCellsPosition = workCellsPosition;
            }

            freeCellsPosition++;
        }
    }

    public void createTaskCell(Row row, int i, String value){
        Cell cell = row.createCell(i);
        Sheet tSheet = template.getSheetAt(0);
        copyCell(tSheet.getRow(1).getCell(i), cell);
        cell.setCellValue(value);
    }

    private void copyFromTemplateWork(Task task, Cell cell) {
        for (Work work : task.getWorks()) {
            cell.setCellValue(work.getName());

        }
    }

    public void close() {
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("workbook.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            wb.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            return WorkbookFactory.create(new File("footer_template.xls"));
        } catch (IOException e) {
            return null;
        }
    }

    private void copyRowStyle(Row from, Row to) {
//        to.setRowStyle(from.getRowStyle());
        to.setHeight(from.getHeight());
    }

    private void copyCell(Cell from, Cell to) {
//        to.setCellFormula(from.getCellFormula());
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
        }
    }

}
