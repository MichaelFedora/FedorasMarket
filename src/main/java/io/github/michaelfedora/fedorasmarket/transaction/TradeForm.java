package io.github.michaelfedora.fedorasmarket.transaction;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.GoodType;
import io.github.michaelfedora.fedorasmarket.data.ShopType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Created by Michael on 2/24/2016.
 */
public final class TradeForm {
    public ShopType shopType;
    public String ownerGoodName;
    public double ownerGoodAmt;
    public String customerGoodName;
    public double customerGoodAmt;

    public Optional<TradeTransaction> makeTransaction() {

        TradeTransaction transaction = new TradeTransaction(new TransactionParty(), new TransactionParty());

        if(shopType.ownerGoodType == GoodType.ITEM) {

            Optional<ItemType> opt_type = FedorasMarket.getGame().getRegistry().getType(ItemType.class, ownerGoodName);
            if(!opt_type.isPresent())
                return Optional.empty();

            transaction.customerParty.addItem(opt_type.get(), BigDecimal.valueOf(ownerGoodAmt).intValueExact());

        } else if(shopType.ownerGoodType == GoodType.CURRENCY) {
            for(Currency currency : FedorasMarket.getEconomyService().getCurrencies()) {
                if(currency.getDisplayName().toString().equalsIgnoreCase(ownerGoodName))
                    transaction.ownerParty.addCurrency(currency, BigDecimal.valueOf(ownerGoodAmt).setScale(2, RoundingMode.HALF_UP));
            }

            if(transaction.ownerParty.getCurrencies().isEmpty())
                return Optional.empty();
        }

        if(shopType.customerGoodType == GoodType.ITEM) {

            Optional<ItemType> opt_type = FedorasMarket.getGame().getRegistry().getType(ItemType.class, customerGoodName);
            if(!opt_type.isPresent())
                return Optional.empty();

            transaction.customerParty.addItem(opt_type.get(), BigDecimal.valueOf(customerGoodAmt).intValueExact());

        } else if(shopType.customerGoodType == GoodType.CURRENCY) {

            for(Currency currency : FedorasMarket.getEconomyService().getCurrencies()) {
                if(currency.getDisplayName().toString().equalsIgnoreCase(customerGoodName))
                    transaction.customerParty.addCurrency(currency, BigDecimal.valueOf(customerGoodAmt).setScale(2, RoundingMode.HALF_UP));
            }

            if(transaction.customerParty.getCurrencies().isEmpty())
                return Optional.empty();
        }

        return Optional.of(transaction);
    }
}
