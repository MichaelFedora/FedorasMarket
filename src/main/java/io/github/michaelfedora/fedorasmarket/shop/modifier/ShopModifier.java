package io.github.michaelfedora.fedorasmarket.shop.modifier;

import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.trade.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;

import java.util.*;

/**
 * Created by Michael on 2/23/2016.
 */
public class ShopModifier implements java.io.Serializable {

    public static final ShopModifier NONE = new ShopModifier(true, new ArrayList<>());

    private static Map<String, Class<? extends ShopModifier>> modifiers = new HashMap<>();
    public static void register(String name, Class<? extends ShopModifier> type) {
        modifiers.put(name, type);
    }

    public static Optional<Class<? extends ShopModifier>> getShopModifier(String name) {
        if(modifiers.containsKey(name))
            return Optional.of(modifiers.get(name));

        return Optional.empty();
    }


    public final boolean negate;
    public final List<TradeType> tradeTypes;

    protected ShopModifier(boolean negate, List<TradeType> tradeTypes) {
        this.negate = negate;
        this.tradeTypes = tradeTypes;
    }

    public boolean isValidWith(TradeType type) {
        if(this.negate)
            return (!tradeTypes.contains(type));
        else
            return tradeTypes.contains(type);
    }

    public void execute(ShopData data, TradeActiveParty owner, TradeActiveParty customer) { }
}
