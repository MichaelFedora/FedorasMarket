package io.github.michaelfedora.fedorasmarket.data;

/**
 * The Shop Type is what the customer is doing; Are they Buying Items? Selling Items? Trading Currency? Etc.
 */
public enum ShopType {

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
    ITEM_TRADEShopType(GoodType.ITEM, GoodType.ITEM, "ItemTrade"),

    /**
     * The customer trades on currency for another.
     * May have one currency for each party. May be the same currency.
     * ownerGoodType is GoodType.CURRENCY, customerGoodType is GoodType.CURRENCY.
     */
    CURRENCY_TRADE(GoodType.CURRENCY, GoodType.CURRENCY, "CurrencyTrade");

    public final GoodType ownerGoodType;
    public final GoodType customerGoodType;
    public final String niceName;

    ShopType(GoodType ownerGoodType, GoodType customerGoodType, String niceName) {
        this.ownerGoodType = ownerGoodType;
        this.customerGoodType = customerGoodType;
        this.niceName = niceName;
    }

    public boolean has(GoodType goodType) {
        return (ownerGoodType == goodType || customerGoodType == goodType);
    }
}
