package ru.simplex_software.smeta.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.simplex_software.smeta.model.Element;
import ru.simplex_software.smeta.model.Task;

import java.util.List;


public class ReportCreator {

    private static Logger LOG = LoggerFactory.getLogger(ReportCreator.class);

    private static final String TITLE = "Локальный сметный расчет №4 к Договору подряда №1 jn 01.03.2017г. " + "\n" +
                                        "Отчетный период с '01' июня 2017г. по '30' июня 2017г.";

    private XlsxBuilder xlsxBuilder;

    private long resultAmount = 0;

    long elementsAmount = 0;

    public ReportCreator() {

        xlsxBuilder = new XlsxBuilder().
                                startSheet(TITLE).
                                startRow().
                                setNextColumnIdx(4).
                                addTextLeftAlignedColumn("Приложение №1" +
                                                         "к Договору подряда №1 от 01.03.2017г.").
                                startRow().
                                setRowTitleHeight().
                setColorForTitle(TITLE);
    }

    public void addFields() {
        xlsxBuilder.startRow().
                    setRowTitleHeight().
                    setRowThinBottomBorder().
                    startRow().
                    setRowTitleHeight().
                    setRowThickTopBorder().
                    setRowThickBottomBorder().
                    addBoldTextCenterAlignedColumn("Перечень работ").
                    addBoldTextCenterAlignedColumn("Ед. изм").
                    addBoldTextCenterAlignedColumn("Кол-во").
                    addBoldTextCenterAlignedColumn("Цена за ед.").
                    addBoldTextCenterAlignedColumn("Сумма").
                    addBoldTextCenterAlignedColumn("Перечень материалов").
                    addBoldTextCenterAlignedColumn("Ед. изм").
                    addBoldTextCenterAlignedColumn("Кол-во").
                    addBoldTextCenterAlignedColumn("Цена за ед.").
                    addBoldTextCenterAlignedColumn("Сумма");
    }

    public void allOnEstimate(long amount) {
        xlsxBuilder.addBoldTextCenterAlignedColumn("Всего по смете без НДС: " + amount);
    }

    public void newRow() {
        xlsxBuilder.startRow();
    }

    public void setTask(Task task) {
        xlsxBuilder.startRow();
        xlsxBuilder.setColorForTask(task.getName());
        xlsxBuilder.startRow();
    }

    public void setWorkOrMaterial(Element element) {
        xlsxBuilder.addTextLeftAlignedColumn(element.getName()).
                addTextLeftAlignedColumn(element.getUnits()).
                addDoubleCenterAlignedColumn(element.getQuantity()).
                addDoubleCenterAlignedColumn(element.getUnitPrice()).
                addDoubleCenterAlignedColumn(element.getAmount());
        resultAmount += element.getAmount();
    }

    public void setTotalAmountForWorks(long amount) {
        xlsxBuilder.addBoldTextCenterAlignedColumn("Итого " + amount);
    }

    public void setTotalAmountForMaterials(long amount) {
        xlsxBuilder.addBoldTextLeftAlignedColumn("Итого " + amount);
    }

    public void setTotalAmountAllElements(List<Long> listAmount, String text) {
        elementsAmount = 0;
        for (Long amount : listAmount) {
            elementsAmount += amount;
        }
        xlsxBuilder.setStylesForAmountElements(text + elementsAmount);
    }

    public long getTotalAmountAllElements() {
        return elementsAmount;
    }

    public void allAmountOfEstimate(long allAmounts) {
        xlsxBuilder.setStylesForAmountEstimate("Всего по смете: " + allAmounts);
    }

    public void setFooterEstimate() {

        final String signature = "_________________________\n"
                                 + "_________________________\n"
                                 + "М.П.";

        xlsxBuilder.addBoldTextCenterAlignedColumn("Исполнитель\n" + "ООО 'Фаворит'\n"
                                                  + signature);
        xlsxBuilder.setNextColumnIdx(6);
        xlsxBuilder.addBoldTextCenterAlignedColumn("Заказчик\n" + "ООО 'Адидас'\n"
                                                   + signature);
    }

    public XlsxBuilder build() {
        return xlsxBuilder.build();
    }

    public void setIndexRow(int indexRow) {
        xlsxBuilder.setNextRowIdx(indexRow);
    }

    public int getIndexRow() {
        return xlsxBuilder.getNextRowIdx();
    }

    public void setIndexColumn(int indexColumn) {
        xlsxBuilder.setNextColumnIdx(indexColumn);
    }

    public int getIndexColumn() {
        return xlsxBuilder.getNextRowIdx();
    }

    public XlsxBuilder getXlsxBuilder() {
        return xlsxBuilder;
    }

}
