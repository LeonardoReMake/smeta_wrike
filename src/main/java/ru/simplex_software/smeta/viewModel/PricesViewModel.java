package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import ru.simplex_software.smeta.dao.PriceDepartureDAO;
import ru.simplex_software.smeta.dao.TaskDAO;
import ru.simplex_software.smeta.model.PriceDeparture;

@VariableResolver(ru.simplex_software.zkutils.DaoVariableResolver.class)
public class PricesViewModel {

    private static Logger LOG = LoggerFactory.getLogger(PricesViewModel.class);

    @WireVariable
    private PriceDepartureDAO priceDepartureDAO;

    @WireVariable
    private TaskDAO taskDAO;

    private PriceDeparture priceDeparture;

    @Init
    public void init() {
        addOrGetPriceDeparture();
    }

    @Command
    @NotifyChange({"priceDeparture"})
    public void applyPriceDeparture() {
        priceDepartureDAO.saveOrUpdate(priceDeparture);
        Messagebox.show("Цены добавлены");
    }

    @Command
    public void comeBack() {
        Executions.sendRedirect("/index.zul");
    }

    public PriceDeparture getPriceDeparture() {
        return priceDeparture;
    }

    public void setPriceDeparture(PriceDeparture priceDeparture) {
        this.priceDeparture = priceDeparture;
    }

    private void addOrGetPriceDeparture() {
        if (priceDepartureDAO.findAllDepartures().isEmpty()) {
            priceDeparture = new PriceDeparture();
        } else {
            priceDeparture = priceDepartureDAO.findAllDepartures().get(0);
        }
    }

}
