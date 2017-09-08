package ru.simplex_software.smeta.excel;


import com.enterprisemath.utils.ValidationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/* Класс для простого использлвания apache poi + есть некоторые стили */
public class XlsxBuilder {

    private static Logger LOG = LoggerFactory.getLogger(XlsxBuilder.class);

    private static final String FILE_NAME = "/home/vadim/report.xlsx";

    /**
     * Underlined workbook.
     */
    private Workbook workbook;
    /**
     * Current sheet.
     */
    private Sheet sheet = null;
    /**
     * Current row.
     */
    private Row row = null;
    /**
     * Next row index.
     */
    private int nextRowIdx = 0;
    /**
     * Style attributes for row.
     */
    private Set<StyleAttribute> rowStyleAttributes;
    /**
     * Next column index.
     */
    private int nextColumnIdx = 0;
    private Map<Set<StyleAttribute>, CellStyle> styleBank = new HashMap<Set<StyleAttribute>, CellStyle>();
    /**
     * Creates new instance.
     */
    public XlsxBuilder() {
        workbook = new XSSFWorkbook();
    }
    /**
     * Starts sheet.
     *
     * @param name sheet name
     * @return this instance
     */
    public XlsxBuilder startSheet(String name) {
        sheet = workbook.createSheet(name);
        nextRowIdx = 0;
        nextColumnIdx = 0;
        rowStyleAttributes = new HashSet<StyleAttribute>();
        return this;
    }

    /**
     * Sets auto sizing columns.
     *
     * @param idx column index, starting from 0
     * @return this instance
     */
    public XlsxBuilder setAutoSizeColumn(int idx) {
        sheet.autoSizeColumn(idx);
        return this;
    }
    /**
     * Sets column size.
     *
     * @param idx column index, starting from 0
     * @param m number of 'M' standard characters to use for size calculation
     * @return this instance
     */
    public XlsxBuilder setColumnSize(int idx, int m) {
        sheet.setColumnWidth(idx, (m + 1) * 256);
        return this;
    }
    /**
     * Starts new row.
     *
     * @return this instance
     */
    public XlsxBuilder startRow() {
        row = sheet.createRow(nextRowIdx);
        nextRowIdx = nextRowIdx + 1;
        nextColumnIdx = 0;
        rowStyleAttributes = new HashSet<StyleAttribute>();
        return this;
    }
    /**
     * Sets row top border as thin.
     *
     * @return this instance
     */
    public XlsxBuilder setRowThinTopBorder() {
        ValidationUtils.guardEquals(0, nextColumnIdx, "must be called before inserting columns");
        row.setRowStyle(getCellStyle(StyleAttribute.THIN_TOP_BORDER));
        rowStyleAttributes.add(StyleAttribute.THIN_TOP_BORDER);
        return this;
    }
    /**
     * Sets row top border as thick.
     *
     * @return this instance
     */
    public XlsxBuilder setRowThickTopBorder() {
        ValidationUtils.guardEquals(0, nextColumnIdx, "must be called before inserting columns");
        row.setRowStyle(getCellStyle(StyleAttribute.THICK_TOP_BORDER));
        rowStyleAttributes.add(StyleAttribute.THICK_TOP_BORDER);
        return this;
    }
    /**
     * Sets row bottom border as thin.
     *
     * @return this instance
     */
    public XlsxBuilder setRowThinBottomBorder() {
        ValidationUtils.guardEquals(0, nextColumnIdx, "must be called before inserting columns");
        row.setRowStyle(getCellStyle(StyleAttribute.THIN_BOTTOM_BORDER));
        rowStyleAttributes.add(StyleAttribute.THIN_BOTTOM_BORDER);
        return this;
    }

    public XlsxBuilder setColorForTitle(final String text) {
        Cell cell = row.createCell(nextColumnIdx);
        CellStyle style = getCellStyle(StyleAttribute.THIN_BOTTOM_BORDER);
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontHeightInPoints((short) 16);
        font.setFontName("IMPACT");
        font.setItalic(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(StringUtils.stripToEmpty(text));
        nextColumnIdx = nextColumnIdx + 1;
        return this;
    }

    public XlsxBuilder setColorForTask(final String text) {
        Cell cell = null;
        for (int i = 0; i < 10; i++) {
            cell  = row.createCell(nextColumnIdx);
            CellStyle style = getCellStyle(StyleAttribute.BOLD);
            style.setFillForegroundColor(IndexedColors.GREEN.index);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setColor(IndexedColors.DARK_BLUE.getIndex());
            style.setFont(font);
            cell.setCellStyle(style);
            if (nextColumnIdx == 0) {
                cell.setCellValue(StringUtils.stripToEmpty(text));
            }
            nextColumnIdx++;
        }
        return this;
    }

    public XlsxBuilder setStylesForAmountElements(final String text) {
        Cell cell = null;
        for (int i = 0; i < 5; i++) {
            cell = row.createCell(nextColumnIdx);
            CellStyle style = cell.getCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setFontName("IMPACT");
            font.setItalic(true);
            style.setFont(font);
            cell.setCellStyle(style);
            if (nextColumnIdx == 0)
                cell.setCellValue(StringUtils.stripToEmpty(text));
            nextColumnIdx = nextColumnIdx + 1;
        }
        return this;
    }

    public XlsxBuilder setStylesForAmountEstimate(final String text) {
        Cell cell = null;
        for (int i = 0; i < 10; i++) {
            cell = row.createCell(nextColumnIdx);
            CellStyle style = cell.getCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            XSSFFont font = (XSSFFont) workbook.createFont();
            font.setFontHeightInPoints((short) 11);
            font.setFontName("IMPACT");
            font.setItalic(true);
            style.setFont(font);
            cell.setCellStyle(style);
            if (nextColumnIdx == 0)
                cell.setCellValue(StringUtils.stripToEmpty(text));
            nextColumnIdx = nextColumnIdx + 1;
        }
        return this;
    }
    
    /**
     * Sets row bottom border as thick.
     *
     * @return this instance
     */
    public XlsxBuilder setRowThickBottomBorder() {
        ValidationUtils.guardEquals(0, nextColumnIdx, "must be called before inserting columns");
        row.setRowStyle(getCellStyle(StyleAttribute.THICK_BOTTOM_BORDER));
        rowStyleAttributes.add(StyleAttribute.THICK_BOTTOM_BORDER);
        return this;
    }
    /**
     * Sets row height to capture the title.
     *
     * @return this instance
     */
    public XlsxBuilder setRowTitleHeight() {
        ValidationUtils.guardEquals(0, nextColumnIdx, "must be called before inserting columns");
        row.setHeightInPoints(30);
        return this;
    }
    /**
     * Adds title column.
     *
     * @param text text
     * @return this instance
     */
    public XlsxBuilder addTitleTextColumn(String text) {
        Cell cell = row.createCell(nextColumnIdx);
        CellStyle style = getCellStyle(StyleAttribute.TITLE_SIZE, StyleAttribute.BOLD);
        cell.setCellStyle(style);
        cell.setCellValue(StringUtils.stripToEmpty(text));
        nextColumnIdx = nextColumnIdx + 1;
        return this;
    }

    public XlsxBuilder addTextLeftAlignedColumn(String text) {
        Cell cell = row.createCell(nextColumnIdx);
        CellStyle style = getCellStyle(StyleAttribute.ALIGN_LEFT);
        cell.setCellStyle(style);
        cell.setCellValue(StringUtils.stripToEmpty(text));
        nextColumnIdx = nextColumnIdx + 1;
        return this;
    }

    public XlsxBuilder addTextCenterAlignedColumn(String text) {
        Cell cell = row.createCell(nextColumnIdx);
        CellStyle style = getCellStyle(StyleAttribute.ALIGN_CENTER);
        cell.setCellStyle(style);
        cell.setCellValue(StringUtils.stripToEmpty(text));
        nextColumnIdx = nextColumnIdx + 1;
        return this;
    }

    public XlsxBuilder addDoubleCenterAlignedColumn(double val) {
        Cell cell = row.createCell(nextColumnIdx);
        CellStyle style = getCellStyle(StyleAttribute.ALIGN_CENTER);
        cell.setCellStyle(style);
        cell.setCellValue(val);
        nextColumnIdx = nextColumnIdx + 1;
        return this;
    }

    public XlsxBuilder addBoldTextLeftAlignedColumn(String text) {
        Cell cell = row.createCell(nextColumnIdx);
        CellStyle style = getCellStyle(StyleAttribute.ALIGN_LEFT, StyleAttribute.BOLD);
        cell.setCellStyle(style);
        cell.setCellValue(StringUtils.stripToEmpty(text));
        nextColumnIdx = nextColumnIdx + 1;
        return this;
    }

    public XlsxBuilder addBoldTextCenterAlignedColumn(String text) {
        Cell cell = row.createCell(nextColumnIdx);
        CellStyle style = getCellStyle(StyleAttribute.ALIGN_CENTER, StyleAttribute.BOLD);
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        cell.setCellStyle(style);
        cell.setCellValue(StringUtils.stripToEmpty(text));
        nextColumnIdx = nextColumnIdx + 1;
        return this;
    }


    public XlsxBuilder build() {
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
    /**
     * Returns cell style.
     *
     * @param attrs attributes
     * @return cell style
     */
    private CellStyle getCellStyle(StyleAttribute... attrs) {
        Set<StyleAttribute> allattrs = new HashSet<StyleAttribute>();
        allattrs.addAll(rowStyleAttributes);
        allattrs.addAll(Arrays.asList(attrs));
        if (styleBank.containsKey(allattrs)) {
            return styleBank.get(allattrs);
        }
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        style.setFont(font);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        for (StyleAttribute attr : allattrs) {
            if (attr.equals(StyleAttribute.TITLE_SIZE)) {
                font.setFontHeightInPoints((short) 18);
            }
            else if (attr.equals(StyleAttribute.BOLD)) {
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            }
            else if (attr.equals(StyleAttribute.THIN_TOP_BORDER)) {
                style.setBorderTop(CellStyle.BORDER_THIN);
            }
            else if (attr.equals(StyleAttribute.THIN_BOTTOM_BORDER)) {
                style.setBorderBottom(CellStyle.BORDER_THIN);
            }
            else if (attr.equals(StyleAttribute.THICK_TOP_BORDER)) {
                style.setBorderTop(CellStyle.BORDER_THICK);
            }
            else if (attr.equals(StyleAttribute.THICK_BOTTOM_BORDER)) {
                style.setBorderBottom(CellStyle.BORDER_THICK);
            }
            else if (attr.equals(StyleAttribute.ALIGN_LEFT)) {
                style.setAlignment(CellStyle.ALIGN_LEFT);
            }
            else if (attr.equals(StyleAttribute.ALIGN_CENTER)) {
                style.setAlignment(CellStyle.ALIGN_CENTER);
            }
            else {
                throw new RuntimeException("unknown cell style attribute: " + attr);
            }
        }
        styleBank.put(allattrs, style);
        return style;
    }

    public XlsxBuilder setNextRowIdx(int nextRowIdx) {
        this.nextRowIdx = nextRowIdx;
        return this;
    }

    public int getNextRowIdx() {
        return nextRowIdx;
    }

    public XlsxBuilder setNextColumnIdx(int nextColumnIdx) {
        this.nextColumnIdx = nextColumnIdx;
        return this;
    }

    public Workbook getWorkBook() {
        return workbook;
    }

    public int getNextColumnIdx() {
        return nextColumnIdx;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    /**
     * Possible style attributes.
     */
    private static enum StyleAttribute {
        /**
         * Thin top border.
         */
        THIN_TOP_BORDER,
        /**
         * Thin bottom border.
         */
        THIN_BOTTOM_BORDER,
        /**
         * Thick top border.
         */
        THICK_TOP_BORDER,
        /**
         * Thick bottom border.
         */
        THICK_BOTTOM_BORDER,
        /**
         * Title font size.
         */
        TITLE_SIZE,
        /**
         * Bold font.
         */
        BOLD,
        /**
         * Left alignment.
         */
        ALIGN_LEFT,
        /**
         * Center alignment.
         */
        ALIGN_CENTER
    }

}