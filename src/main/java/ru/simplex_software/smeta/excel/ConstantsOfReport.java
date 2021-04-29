package ru.simplex_software.smeta.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantsOfReport {

    private static Logger LOG = LoggerFactory.getLogger(ConstantsOfReport.class);

    private ConstantsOfReport() {}

    /* количество строк для шапки. */
    public static final int COUNT_ROWS_HEADER = 12;

    /* количество столбцов сметы. */
    public static final int COUNT_CELLS_ESTIMATE = 11;

    /* Элемент каскада */
    public static final int INDEX_SHEET = 0;

    /* номер столбца сумм в заголовках. */
    public static final int CELL_NUM_AMOUNT_HEADER = 8;

    /* номер столбца для начала репорта задач. */
    public static final int CELL_NUM_FIRST_FOR_TASK_REPORT = 1;

    /* последний номер столбца репорта задач. */
    public static final int CELL_NUM_LAST_FOR_TASK = 10;

    /* строка для начала задачи работ и материлов. */
    public static final int ROW_NUM_FIRST_FOR_TASK = 1;

    /* номер столбца для суммы работ. */
    public static final int CELL_NUM_AMOUNT_FOR_WORK = 5;

    /* номер столбца для суммы материалов и итога всего репорт элемента. */
    public static final int CELL_NUM_AMOUNT_FOR_MATERIAL_OR_ESTIMATE = 9;

    /* номер строки для суммы строки в подвале. */
    public static final int ROW_POSITION_AMOUNT_FOR_WORK = 2;

    /* номер ячейки для заголовков итогов в подвале. */
    public static final int CELL_NUM_END_FOR_TITLE = 9;

    /* номер строки для заголовка сметы(верхняя). */
    public static final int ROW_FOR_HEADER_ESTIMATE = 3;

    /* номер строки для заголовка всего по смете без НДС. */
    public static final int ROW_FOR_AMOUNT_NOT_VAT = 6;

    /* номер строки для заголовка НДС . */
    public static final int ROW_FOR_VAT = 7;

    /* номер строки для заголовка всего по смете с НДС . */
    public static final int ROW_FOR_VAT_AMOUNT = 8;

    /* номер строки для заголовка сметы(нижняя). */
    public static final int ROW_FOR_ESTIMATE_PERIOD = 4;

    /* Интервал ночного выезда. */
    public static final int NIGHT_DEPARTURE_INTERVAL = 10;

    /* Строка рубля. */
    public static final String RU_STRING = "р.";

}
