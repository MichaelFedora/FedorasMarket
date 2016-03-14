package io.github.michaelfedora.fedorasmarket.trade;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.database.FmSerializable;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;
import io.github.michaelfedora.fedorasmarket.enumtype.GoodType;
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
public class TradeParty implements FmSerializable<SerializedTradeParty> {

    public Map<ItemType,Integer> items = new HashMap<>();
    public Map<Currency,BigDecimal> currencies = new HashMap<>();

    public TradeParty() { }

    public SerializedTradeParty serialize() {
        SerializedTradeParty data = new SerializedTradeParty();

        for(Map.Entry<ItemType,Integer> entry : this.items.entrySet()) {
            data.items.put(entry.getKey().getId(), entry.getValue());
        }

        for(Map.Entry<Currency,BigDecimal> entry : this.currencies.entrySet()) {
            data.currencies.put(entry.getKey().getDisplayName().toPlain(), entry.getValue());
        }

        return data;
    }

    public static TradeParty fromSerializedData(SerializedTradeParty data) {
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

    public TradeParty trim(GoodType filter) {

        clean();

        switch(filter) {

            case CURRENCY:
                return (new TradeParty()).setCurrencies(this.currencies);

            case ITEM:
                return (new TradeParty()).setItems(this.items);

            default:
                return this;
        }
    }

    public TradeParty clean() {

        cleanItems();

        cleanCurrencies();

        return this;
    }

    public TradeParty addItem(ItemType itemType, int amt) {

        if(this.items.containsKey(itemType))
            amt += this.items.get(itemType);

        if(amt < 0)
            amt = 0;

        this.items.put(itemType, amt);

        return this;
    }

    public TradeParty setItem(ItemType itemType, int amt) {

        if(amt < 0)
            return this;

        this.items.put(itemType, amt);

        return this;
    }

    public TradeParty removeItem(ItemType itemType) {

        this.items.remove(itemType);

        return this;
    }

    public TradeParty cleanItems() {

        Set<ItemType> keys = items.keySet();

        for(ItemType i : keys)
            if(items.get(i) <= 0)
                items.remove(i);

        return this;
    }

    public TradeParty addCurrency(Currency currency, BigDecimal amt) {

        if(this.currencies.containsKey(currency))
            amt = amt.add(this.currencies.get(currency));

        if(amt.compareTo(BigDecimal.ZERO) < 0)
            amt = BigDecimal.ZERO;

        this.currencies.put(currency, amt);

        return this;
    }

    public TradeParty setCurrency(Currency currency, BigDecimal amt) {

        if(amt.compareTo(BigDecimal.ZERO) < 0)
            return this;

        this.currencies.put(currency, amt);

        return this;
    }

    public TradeParty removeCurrency(Currency currency) {

        this.currencies.remove(currency);

        return this;
    }

    public TradeParty cleanCurrencies() {

        Set<Currency> keys = currencies.keySet();

        for(Currency c : keys)
            if(currencies.get(c).compareTo(BigDecimal.ZERO) <= 0)
                currencies.remove(c);

        return this;
    }

    public TradeParty addDefaultCurrency(BigDecimal amt) {
        return addCurrency(FmUtil.getDefaultCurrency(), amt);
    }

    public TradeParty setDefaultCurrency(BigDecimal amt) {
        return setCurrency(FmUtil.getDefaultCurrency(), amt);
    }

    public TradeParty setItems(Map<ItemType,Integer> items) {

        this.items = items;

        return this;
    }

    public TradeParty setCurrencies(Map<Currency,BigDecimal> currencies) {

        this.currencies = currencies;

        return this;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("items: {");

        int i = 0;
        for(Map.Entry<ItemType,Integer> entry : this.items.entrySet()) {
            sb.append(entry.getKey().getName()).append("=").append(entry.getValue());
            if(++i < this.items.entrySet().size()) {
                sb.append(", ");
            }
        }

        sb.append("}, currencies: {");

        i = 0;
        for(Map.Entry<Currency,BigDecimal> entry : this.currencies.entrySet()) {
            sb.append(entry.getKey().getDisplayName().toPlain()).append("=").append(entry.getValue());
            if(++i < this.currencies.entrySet().size()) {
                sb.append("; ");
            }
        }

        return sb.append("}").toString();
    }
}
