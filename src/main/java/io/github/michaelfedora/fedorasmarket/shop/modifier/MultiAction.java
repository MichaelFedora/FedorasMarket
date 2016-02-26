package io.github.michaelfedora.fedorasmarket.shop.modifier;

import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.transaction.TradeActiveParty;

import java.util.ArrayList;

/**
 * For doing a transaction multiple times.
 */
public final class MultiAction extends ShopModifier {

    public final int number;

    public MultiAction(int number) {
        super(true, new ArrayList<>());
        this.number = number;
    }

    @Override
    public void execute(Shop shop, TradeActiveParty owner, TradeActiveParty customer) {
        for(int i = 0; i < number && shop.tradeTransaction.apply(owner, customer); i++);
    }
}
