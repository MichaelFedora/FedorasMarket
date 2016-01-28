package io.github.michaelfedora.fedoraschestshop.shop;

import io.github.michaelfedora.fedoraschestshop.FedorasChestShop;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MichaelFedora on 1/24/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class ShopTransaction {

    /*public enum Op {
        BUY, // Limited Trade (Owner has item and amount, Customer has price)
        SELL, // Switched-Buy (Customer becomes Owner, has item and amount, and Owner becomes Customer, has price)
        TRADE, // Full Trade (Owner and Customer do full trade (money and items)
    }*/

    public Party ownerParty;
    public Party customerParty;

    private static Currency getDefaultCurrency() {
        return FedorasChestShop.getEconomyService().getDefaultCurrency();
    }

    public static class Party {

        //public final String itemName;
        //public final int amtItem;
        //public final int amtMoney;
        private Map<ItemType,Integer> items = new HashMap<ItemType,Integer>(); // maps?
        private Map<Currency,BigDecimal> currencies = new HashMap<Currency,BigDecimal>();

        public Party() { }

        public Party addItem(ItemType name, int amt) {

            if(items.containsKey(name))
                amt += items.get(name);

            items.put(name, amt);

            return this;
        }

        public Party setItem(ItemType name, int amt) {
            items.put(name, amt);
            return this;
        }

        public Party addCurrency(Currency currency, BigDecimal amt) {

            if(currencies.containsKey(currency))
                amt = amt.add(currencies.get(currency));

            currencies.put(currency, amt);

            return this;
        }

        public Party setCurrency(Currency currency, BigDecimal amt) {
            currencies.put(currency, amt);
            return this;
        }

        public Party addDefaultCurrency(BigDecimal amt) {
            return addCurrency(getDefaultCurrency(), amt);
        }

        public Party setDefaultCurrency(BigDecimal amt) {
            return setCurrency(getDefaultCurrency(), amt);
        }

        public Map<ItemType,Integer> getItems() { return items; }
        public Map<Currency,BigDecimal> getCurrencies() { return currencies; }
    }


    public ShopTransaction(Party owner, Party customer) {
        this.ownerParty = owner;
        this.customerParty = customer;
    }

    public void apply(Account owner, Account customer) {
        EconomyService eco = FedorasChestShop.getEconomyService();

        // Be VERY careful here; we need to make sure nothing gets transferred if there is an error later on
        for(Map.Entry<Currency,BigDecimal> entry : ownerParty.getCurrencies().entrySet()) {
            owner.transfer(customer, entry.getKey(), entry.getValue(), Cause.of(this));
        }

        for(Map.Entry<Currency, BigDecimal> entry : customerParty.getCurrencies().entrySet()) {
            customer.transfer(owner, entry.getKey(), entry.getValue(), Cause.of(this));
        }

        for(Map.Entry<ItemType, Integer> entry : ownerParty.getItems().entrySet()) {

        }

        for(Map.Entry<ItemType, Integer> entry : customerParty.getItems().entrySet()) {

        }
    }
}
