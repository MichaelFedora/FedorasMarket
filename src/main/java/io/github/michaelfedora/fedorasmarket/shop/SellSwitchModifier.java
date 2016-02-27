package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeParty;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;

import java.util.Arrays;

/**
 * For ITEM_BUY shops, allows them to have "sell" as their secondary
 */
public final class SellSwitchModifier extends ShopModifier {

    public TradeParty ownerSellParty;

    public SellSwitchModifier(TradeParty ownerSellParty) {
        super(false, Arrays.asList(TradeType.ITEM_BUY));
        this.ownerSellParty = ownerSellParty;
    }

    @Override
    public void execute(Shop shop, TradeActiveParty owner, TradeActiveParty customer) {

        TradeForm switchTradeForm = new TradeForm(shop.tradeType, ownerSellParty, shop.tradeForm.getCustomerParty());
        switchTradeForm.apply(owner, customer);
    }
}
