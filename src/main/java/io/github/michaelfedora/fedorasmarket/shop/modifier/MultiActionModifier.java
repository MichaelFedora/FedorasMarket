package io.github.michaelfedora.fedorasmarket.shop.modifier;

import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;

import java.util.ArrayList;

/**
 * For doing a trade multiple times.
 */
public final class MultiActionModifier extends ShopModifier {

    static {
        ShopModifier.register("MultiAction", MultiActionModifier.class);
    }

    public final int number;

    public MultiActionModifier(int number) {
        super(true, new ArrayList<>());
        this.number = number;
    }

    @Override
    public void execute(ShopData data, TradeActiveParty owner, TradeActiveParty customer) {
        for(int i = 0; i < number; i++)
            if(!data.getTradeForm().apply(owner, customer))
                break;
    }
}
