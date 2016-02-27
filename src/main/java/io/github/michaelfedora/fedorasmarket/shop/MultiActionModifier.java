package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;

import java.util.ArrayList;

/**
 * For doing a trade multiple times.
 */
public final class MultiActionModifier extends ShopModifier {

    public final int number;

    public MultiActionModifier(int number) {
        super(true, new ArrayList<>());
        this.number = number;
    }

    @Override
    public void execute(Shop shop, TradeActiveParty owner, TradeActiveParty customer) {
        for(int i = 0; i < number && shop.tradeForm.apply(owner, customer); i++);
    }
}
