package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import ru.simplex_software.smeta.dao.PriceDepartureDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.model.PriceDeparture;
import ru.simplex_software.smeta.model.Task;

import java.util.List;

@VariableResolver(ru.simplex_software.zkutils.DaoVariableResolver.class)
public class PricesViewModel {

    private static Logger LOG = LoggerFactory.getLogger(PricesViewModel.class);

    private ListModelList<PriceDeparture> priceListModel;

    @WireVariable
    private PriceDepartureDAO priceDepartureDAO;

    @WireVariable
    private TaskDAO taskDAO;

    // задание для нахождения цены выездов
    private PriceDeparture priceDeparture = new PriceDeparture();

    @Init
    public void init() {
        final List<PriceDeparture> priceDepartures = priceDepartureDAO.findAllDepartures();
        final List<Task> taskList = taskDAO.findAllTasks();

        priceListModel = new ListModelList<>(priceDepartures);
    }

    public ListModelList<PriceDeparture> getPriceListModel() {
        return priceListModel;
    }

    public PriceDeparture getPriceDeparture() {
        return priceDeparture;
    }

    public void setPriceDeparture(PriceDeparture priceDeparture) {
        this.priceDeparture = priceDeparture;
    }

    @Command
    public void applyPriceDeparture() {
        PriceDeparture newPriceDeparture = new PriceDeparture();
        newPriceDeparture.setUrgentTimePrice(priceDeparture.getUrgentTimePrice());
        newPriceDeparture.setNightlyTimePrice(priceDeparture.getNightlyTimePrice());
        newPriceDeparture.setDayTimePrice(priceDeparture.getDayTimePrice());
        priceListModel.add(newPriceDeparture);
        priceDepartureDAO.create(newPriceDeparture);
        Messagebox.show("Цены добавлены");
    }

    @Command
    public void comeBack() {
        Executions.sendRedirect("/index.zul");
    }

}
