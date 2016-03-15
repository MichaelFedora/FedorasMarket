package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.trade.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeParty;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;

import java.util.Arrays;
import java.util.Collections;

/**
 * For ITEM_BUY shops, allows them to have "sell" as their secondary
 */
public final class SellSwitchModifier extends ShopModifier {

    static {
        ShopModifier.register("SellSwitch", SellSwitchModifier.class);
    }

    public TradeParty ownerSellParty;

    public SellSwitchModifier(TradeParty ownerSellParty) {
        super(false, Collections.singletonList(TradeType.ITEM_BUY));
        this.ownerSellParty = ownerSellParty;
    }

    @Override
    public void execute(ShopData data, TradeActiveParty owner, TradeActiveParty customer) {

        TradeForm switchTradeForm = new TradeForm(data.tradeForm.getTradeType(), ownerSellParty, data.tradeForm.getCustomerParty());
        switchTradeForm.apply(owner, customer);
    }
}
