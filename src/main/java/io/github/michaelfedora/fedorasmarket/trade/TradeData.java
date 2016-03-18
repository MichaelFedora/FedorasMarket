package io.github.michaelfedora.fedorasmarket.trade;

import java.util.UUID;

/**
 * Created by Michael on 3/17/2016.
 */
public class TradeData {

    public final UUID other;
    public final TradeForm tradeForm;
    public final boolean amSender;

    public TradeData(UUID other, TradeForm tradeForm, boolean amSender) {
        this.other = other;
        this.tradeForm = tradeForm;
        this.amSender = amSender;
    }
}