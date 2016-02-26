package io.github.michaelfedora.fedorasmarket.transaction;

import io.github.michaelfedora.fedorasmarket.data.GoodType;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Transaction data
 */
public class TradeParty implements java.io.Serializable {

    public Map<ItemType,Integer> items = new HashMap<ItemType,Integer>(); //TODO: ITEM TYPE IS NOT SERIALIZABLE
    public Map<Currency,BigDecimal> currencies = new HashMap<Currency,BigDecimal>(); // TODO: CURRENCY IS NOT SERIALIZABLE

    public TradeParty() { }

    public TradeParty addItem(ItemType itemType, int amt) {

        if(items.containsKey(itemType))
            amt += items.get(itemType);

        items.put(itemType, amt);

        return this;
    }

    public TradeParty trim(GoodType filter) {
        switch(filter) {
            case CURRENCY:
                return (new TradeParty()).setCurrencies(this.currencies);
            case ITEM:
                return (new TradeParty()).setItems(this.items);
            default:
                return this;
        }
    }

    public TradeParty setItem(ItemType itemType, int amt) {
        items.put(itemType, amt);
        return this;
    }

    public TradeParty removeItem(ItemType itemType) {
        items.remove(itemType);
        return this;
    }

    public TradeParty addCurrency(Currency currency, BigDecimal amt) {

        if(currencies.containsKey(currency))
            amt = amt.add(currencies.get(currency));

        currencies.put(currency, amt);

        return this;
    }

    public TradeParty setCurrency(Currency currency, BigDecimal amt) {
        currencies.put(currency, amt);
        return this;
    }

    public TradeParty removeCurrency(Currency currency) {
        currencies.remove(currency);
        return this;
    }

    public TradeParty addDefaultCurrency(BigDecimal amt) {
        return addCurrency(FmUtil.getDefaultCurrency(), amt);
    }

    public TradeParty setDefaultCurrency(BigDecimal amt) {
        return setCurrency(FmUtil.getDefaultCurrency(), amt);
    }

    public TradeParty setItems(Map<ItemType,Integer> items) { this.items = items; return this; }

    public TradeParty setCurrencies(Map<Currency,BigDecimal> currencies) { this.currencies = currencies; return this; }

    public String toString() {
        return "Items: " + items + ", Currencies: " + currencies;
    }
}
