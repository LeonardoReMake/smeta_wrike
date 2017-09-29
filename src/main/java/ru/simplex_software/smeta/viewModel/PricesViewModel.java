package ru.simplex_software.smeta.viewModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zul.ListModelList;
import ru.simplex_software.smeta.dao.PriceDepartureDAO;
import ru.simplex_software.smeta.model.PriceDeparture;

import javax.annotation.Resource;
import java.util.List;

@VariableResolver(ru.simplex_software.zkutils.DaoVariableResolver.class)
public class PricesViewModel {

    private static Logger LOG = LoggerFactory.getLogger(PricesViewModel.class);

    private ListModelList<PriceDeparture> priceListModel;

    @Resource
    private PriceDepartureDAO priceDepartureDAO;

    @Init
    public void init() {
        List<PriceDeparture> priceDepartures = priceDepartureDAO.findAllDepartures();
        priceListModel = new ListModelList<PriceDeparture>();
    }

}
