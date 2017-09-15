package ru.simplex_software.smeta.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantsOfReport {
    private static Logger LOG = LoggerFactory.getLogger(ConstantsOfReport.class);

    private ConstantsOfReport() {}

    public static final int COUNT_ROWS_HEADER = 12;

    public static final int COUNT_CELLS_ESTIMATE = 12;

    public static final int INDEX_SHEET = 0;

    public static final int ROW_NUM_AMOUNT_HEADER = 6;

    public static final int CELL_NUM_AMOUNT_HEADER = 8;

    public static final int CELL_NUM_FIRST_FOR_TASK = 1;

    public static final int CELL_NUM_LAST_FOR_TASK = 10;

    public static final int ROW_NUM_FIRST_FOR_TASK = 1;

    public static final int CELL_NUM_AMOUNT_FOR_WORK = 5;

    public static final int CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE = 10;

    public static final int TOTAL_NUM_ROW_AMOUNT_FOR_ELEMENT = 2;

    public static final int ROW_POSITION_AMOUNT_FOR_WORK = 2;

    public static final String TOTAL = "Итого";

}
