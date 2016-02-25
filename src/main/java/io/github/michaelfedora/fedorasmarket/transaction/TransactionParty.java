package io.github.michaelfedora.fedorasmarket.transaction;

import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Transaction data
 */
public class TransactionParty {

    private Map<ItemType,Integer> items = new HashMap<ItemType,Integer>();
    private Map<Currency,BigDecimal> currencies = new HashMap<Currency,BigDecimal>();

    public TransactionParty() { }

    public TransactionParty addItem(ItemType itemType, int amt) {

        if(items.containsKey(itemType))
            amt += items.get(itemType);

        items.put(itemType, amt);

        return this;
    }

    public TransactionParty setItem(ItemType itemType, int amt) {
        items.put(itemType, amt);
        return this;
    }

    public TransactionParty removeItem(ItemType itemType) {
        items.remove(itemType);
        return this;
    }

    public TransactionParty addCurrency(Currency currency, BigDecimal amt) {

        if(currencies.containsKey(currency))
            amt = amt.add(currencies.get(currency));

        currencies.put(currency, amt);

        return this;
    }

    public TransactionParty setCurrency(Currency currency, BigDecimal amt) {
        currencies.put(currency, amt);
        return this;
    }

    public TransactionParty removeCurrency(Currency currency) {
        currencies.remove(currency);
        return this;
    }

    public TransactionParty addDefaultCurrency(BigDecimal amt) {
        return addCurrency(FmUtil.getDefaultCurrency(), amt);
    }

    public TransactionParty setDefaultCurrency(BigDecimal amt) {
        return setCurrency(FmUtil.getDefaultCurrency(), amt);
    }

    public Map<ItemType,Integer> getItems() { return items; }
    public Map<Currency,BigDecimal> getCurrencies() { return currencies; }
}
