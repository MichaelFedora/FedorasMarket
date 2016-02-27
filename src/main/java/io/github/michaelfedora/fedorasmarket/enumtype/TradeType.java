package io.github.michaelfedora.fedorasmarket.enumtype;

/**
 * The Shop Type is what the customer is doing; Are they Buying Items? Selling Items? Trading Currency? Etc.
 */
public enum TradeType {

    /**
     * The customer buys an item.
     * May only have one item and one currency type.
     * ownerGoodType is GoodType.ITEM, customerGoodType is GoodType.CURRENCY.
     */
    ITEM_BUY(GoodType.ITEM, GoodType.CURRENCY, "ItemBuy"),

    /**
     * The customer sells an item.
     * May only have one item and one currency type.
     * ownerGoodType is GoodType.CURRENCY, customerGoodType is GoodType.ITEM.
     */
    ITEM_SELL(GoodType.CURRENCY, GoodType.ITEM, "ItemSell"),

    /**
     * The customer trades one item for another.
     * May have one item for each party. May be the same item.
     * ownerGoodType is GoodType.ITEM, customerGoodType is GoodType.ITEM.
     */
    ITEM_TRADE(GoodType.ITEM, GoodType.ITEM, "ItemTrade"),

    /**
     * The customer trades on currency for another.
     * May have one currency for each party. May be the same currency.
     * ownerGoodType is GoodType.CURRENCY, customerGoodType is GoodType.CURRENCY.
     */
    CURRENCY_TRADE(GoodType.CURRENCY, GoodType.CURRENCY, "CurrencyTrade"),

    /**
     * This one's for you, Jack.
     */
    CUSTOM(GoodType.ALL, GoodType.ALL, "Custom");

    public final GoodType ownerGoodType;
    public final GoodType customerGoodType;
    public final String niceName;

    TradeType(GoodType ownerGoodType, GoodType customerGoodType, String niceName) {
        this.ownerGoodType = ownerGoodType;
        this.customerGoodType = customerGoodType;
        this.niceName = niceName;
    }

    public boolean hasWeak(GoodType goodType) {
        return (ownerGoodType.equalsWeak(goodType) || customerGoodType.equalsWeak(goodType));
    }

    public boolean has(GoodType goodType) {
        return (ownerGoodType == goodType || customerGoodType == goodType);
    }
}
