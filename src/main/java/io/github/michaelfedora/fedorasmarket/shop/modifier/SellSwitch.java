package io.github.michaelfedora.fedorasmarket.shop.modifier;

import io.github.michaelfedora.fedorasmarket.data.TradeType;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.transaction.TradeParty;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.transaction.TradeActiveParty;

import java.util.Arrays;

/**
 * For ITEM_BUY shops, allows them to have "sell" as their secondary
 */
public final class SellSwitch extends ShopModifier {

    public TradeParty ownerSellParty;

    public SellSwitch(TradeParty ownerSellParty) {
        super(false, Arrays.asList(TradeType.ITEM_BUY));
        this.ownerSellParty = ownerSellParty;
    }

    @Override
    public void execute(Shop shop, TradeActiveParty owner, TradeActiveParty customer) {

        TradeTransaction switchTradeTransaction = new TradeTransaction(shop.tradeType, ownerSellParty, shop.tradeTransaction.getCustomerParty());
        switchTradeTransaction.apply(owner, customer);
    }
}
