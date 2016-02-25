package io.github.michaelfedora.fedorasmarket.shop.modifier;

import io.github.michaelfedora.fedorasmarket.data.ShopType;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.transaction.TransactionActiveParty;
import io.github.michaelfedora.fedorasmarket.transaction.TransactionParty;

import java.util.Arrays;
import java.util.List;

/**
 * For ITEM_BUY shops, allows them to have "sell" as their secondary
 */
public final class SellSwitch extends ShopModifier {

    public TransactionParty ownerSellParty;

    public SellSwitch(TransactionParty ownerSellParty) {
        super(false, Arrays.asList(ShopType.ITEM_BUY));
        this.ownerSellParty = ownerSellParty;
    }

    @Override
    public void execute(Shop shop, TransactionActiveParty owner, TransactionActiveParty customer) {

        TradeTransaction switchTradeTransaction = new TradeTransaction(ownerSellParty, shop.tradeTransaction.customerParty);
        switchTradeTransaction.apply(owner, customer);
    }
}
