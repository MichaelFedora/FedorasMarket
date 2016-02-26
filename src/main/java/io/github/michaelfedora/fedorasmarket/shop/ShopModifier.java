package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.data.TradeType;
import io.github.michaelfedora.fedorasmarket.transaction.TradeActiveParty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 2/23/2016.
 */
public class ShopModifier {

    public final boolean negate;
    public final List<TradeType> tradeTypes;

    protected ShopModifier(boolean negate, List<TradeType> tradeTypes) {
        this.negate = negate;
        this.tradeTypes = tradeTypes;
    }

    public static final ShopModifier NONE = new ShopModifier(true, new ArrayList<>());

    public boolean isValidWith(TradeType type) {
        if(this.negate)
            return (!tradeTypes.contains(type));
        else
            return tradeTypes.contains(type);
    }

    public void execute(Shop shop, TradeActiveParty owner, TradeActiveParty customer) { }
}
