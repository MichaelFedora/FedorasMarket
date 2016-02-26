package io.github.michaelfedora.fedorasmarket.transaction;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.GoodType;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Transaction data
 */
public class TradeParty {

    public static class Data implements java.io.Serializable {
        public final Map<String,Integer> items = new HashMap<>();
        public final Map<String,BigDecimal> currencies = new HashMap<>();

        public TradeParty deserialize() {
            return TradeParty.fromData(this);
        }

        public String toString() {
            return "Items: " + items + ", Currencies: " + currencies;
        }
    }

    public Map<ItemType,Integer> items = new HashMap<ItemType,Integer>();
    public Map<Currency,BigDecimal> currencies = new HashMap<Currency,BigDecimal>();

    public TradeParty() { }

    public static TradeParty fromData(Data data) {
        TradeParty tradeParty = new TradeParty();

        GameRegistry gameRegistry = FedorasMarket.getGame().getRegistry();

        for(Map.Entry<String,Integer> entry : data.items.entrySet()) {
            Optional<ItemType> opt_itemType = gameRegistry.getType(ItemType.class, entry.getKey());
            if(opt_itemType.isPresent())
                tradeParty.addItem(opt_itemType.get(),entry.getValue());
        }

        Set<Currency> currencies = FedorasMarket.getEconomyService().getCurrencies();

        for(Map.Entry<String,BigDecimal> entry : data.currencies.entrySet()) {
            for(Currency currency : currencies) {
                if(entry.getKey().equals(currency.getDisplayName().toPlain()))
                    tradeParty.addCurrency(currency,entry.getValue());
            }
        }

        return tradeParty;
    }

    public Data toData() {
        Data data = new Data();

        for(Map.Entry<ItemType,Integer> entry : this.items.entrySet()) {
            data.items.put(entry.getKey().getId(), entry.getValue());
        }

        for(Map.Entry<Currency,BigDecimal> entry : this.currencies.entrySet()) {
            data.currencies.put(entry.getKey().getDisplayName().toPlain(), entry.getValue());
        }

        return data;
    }

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
        StringBuilder sb = new StringBuilder();
        sb.append("Items: {");
        int i = 0;
        for(Map.Entry<ItemType,Integer> entry : items.entrySet()) {
            sb.append(entry.getKey().getName()).append("=").append(entry.getValue());
            if(++i < items.entrySet().size()) {
                sb.append(", ");
            }
        }

        sb.append("}, Currencies: {");

        i = 0;
        for(Map.Entry<Currency,BigDecimal> entry : currencies.entrySet()) {
            sb.append(entry.getKey().getDisplayName().toPlain()).append("=").append(entry.getValue());
            if(++i < currencies.entrySet().size()) {
                sb.append("; ");
            }
        }

        return sb.append("}").toString();
    }
}
