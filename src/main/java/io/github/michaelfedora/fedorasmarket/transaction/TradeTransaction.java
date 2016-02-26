package io.github.michaelfedora.fedorasmarket.transaction;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.TradeType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.VirtualAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Created by MichaelFedora on 1/24/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class TradeTransaction {

    public static class Data implements java.io.Serializable {
        public TradeType tradeType;
        public TradeParty.Data ownerPartyData;
        public TradeParty.Data customerPartyData;

        public TradeTransaction deserialize() {
            return TradeTransaction.fromData(this);
        }

        public String toString() {
            return "TradeType: " + tradeType + ", Owner Party Data: {" + ownerPartyData + "}, Customer Party Data: {" + customerPartyData + "}";
        }
    }

    private TradeType tradeType;
    private TradeParty ownerParty;
    private TradeParty customerParty;

    public transient Optional<TransferResult> lastTransferResult;
    public transient Optional<InventoryTransactionResult> lastInventoryTransactionResult;

    public TradeTransaction(TradeType tradeType, TradeParty ownerParty, TradeParty customerParty) {
        this.tradeType = tradeType;
        setOwnerParty(ownerParty);
        setCustomerParty(customerParty);
    }

    public Data toData() {
        Data data = new Data();

        data.tradeType = this.tradeType;
        data.ownerPartyData = this.ownerParty.toData();
        data.customerPartyData = this.customerParty.toData();

        return data;
    }

    public static TradeTransaction fromData(Data data) {
        return new TradeTransaction(data.tradeType, data.ownerPartyData.deserialize(), data.customerPartyData.deserialize());
    }

    public TradeType getTradeType() { return this.tradeType; }
    public TradeParty getOwnerParty() { return this.ownerParty; }
    public TradeParty getCustomerParty() { return this.customerParty; }
    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
        trim();
    }
    public void setOwnerParty(TradeParty ownerParty) {
        this.ownerParty = ownerParty.trim(tradeType.ownerGoodType);
    }
    public void setCustomerParty(TradeParty customerParty) {
        this.customerParty = customerParty.trim(tradeType.customerGoodType);
    }

    protected void trim() {
        this.ownerParty.trim(this.tradeType.ownerGoodType);
        this.customerParty.trim(this.tradeType.customerGoodType);
    }

    //TODO: FIX
    private static InventoryTransactionResult transferItem(Inventory from, Inventory to, ItemType type, int amount) {
        InventoryTransactionResult result;

        ItemStack itemStack = from.query(type);
        itemStack.setQuantity(itemStack.getQuantity() - amount);
        result = from.offer(itemStack);

        itemStack = ItemStack.builder().itemType(type).quantity(amount).build();
        result = to.offer(itemStack);

        return result;
    }

    private Optional<TransferResult> tryApplyCurrency(Account owner, Account customer, Account owner_v, Account customer_v) {
        TransferResult result;

        // Be VERY careful here; we need to make sure nothing gets transferred if there is an error later on
        for(Map.Entry<Currency,BigDecimal> entry : ownerParty.currencies.entrySet()) {
            result = owner.transfer(owner_v, entry.getKey(), entry.getValue(), Cause.of(this));

            if(result.getResult() != ResultType.SUCCESS)
                return Optional.of(result);
        }

        for(Map.Entry<Currency, BigDecimal> entry : customerParty.currencies.entrySet()) {
            result =  customer.transfer(customer_v, entry.getKey(), entry.getValue(), Cause.of(this));

            if(result.getResult() != ResultType.SUCCESS)
                return Optional.of(result);
        }

        return Optional.empty();
    }

    private Optional<InventoryTransactionResult> tryApplyItems(Inventory owner, Inventory customer, Inventory owner_v, Inventory customer_v) {

        /*InventoryTransactionResult result;

        // Be VERY careful here; we need to make sure nothing gets transferred if there is an error later on
        for(Map.Entry<ItemType,Integer> entry : ownerParty.getItems().entrySet()) {
            result = transferItem(owner, owner_v, entry.getKey(), entry.getValue());

            if(result.getType() != InventoryTransactionResult.Type.SUCCESS)
                return Optional.of(result);
        }

        for(Map.Entry<ItemType,Integer> entry : customerParty.getItems().entrySet()) {
            result = transferItem(customer, customer_v, entry.getKey(), entry.getValue());

            if(result.getType() != InventoryTransactionResult.Type.SUCCESS)
                return Optional.of(result);
        }*/

        return Optional.empty();

    }

    private boolean tryApply(TradeActiveParty owner, TradeActiveParty customer, TradeActiveParty owner_v, TradeActiveParty customer_v) {

        lastTransferResult = tryApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        if (lastTransferResult.isPresent())
            if (lastTransferResult.get().getResult() != ResultType.SUCCESS)
                return true;

        lastInventoryTransactionResult = tryApplyItems(owner.inventory, customer.inventory, owner_v.inventory, customer_v.inventory);

        if (lastInventoryTransactionResult.isPresent())
            if (lastInventoryTransactionResult.get().getType() != InventoryTransactionResult.Type.SUCCESS)
                return true;

        return false;
    }

    private void finishApplyCurrency(Account owner, Account customer, Account owner_v, Account customer_v) {

        //TODO: Add error handling
        for(Map.Entry<Currency, BigDecimal> entry : owner_v.getBalances().entrySet())
            owner_v.transfer(customer, entry.getKey(), entry.getValue(), Cause.of(this));

        for(Map.Entry<Currency, BigDecimal> entry : owner_v.getBalances().entrySet())
             customer_v.transfer(owner, entry.getKey(), entry.getValue(), Cause.of(this));
    }

    private boolean finishApply(TradeActiveParty owner, TradeActiveParty customer, TradeActiveParty owner_v, TradeActiveParty customer_v) {

        finishApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        return false;

        /*lastTransferResult = finishApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        if (lastTransferResult.isPresent())
            if (lastTransferResult.get().getResult() != ResultType.SUCCESS)
                return true;

        return false;*/
    }

    public boolean apply(TradeActiveParty owner, TradeActiveParty customer) {
        EconomyService eco = FedorasMarket.getEconomyService();

        TradeActiveParty owner_v;
        TradeActiveParty customer_v;
        {
            Optional<VirtualAccount> opt_owner_acc = eco.createVirtualAccount(FedorasMarket.ACCOUNT_VIRTUAL_OWNER_ID_PREFIX + owner.account.getIdentifier());
            Optional<VirtualAccount> opt_customer_acc = eco.createVirtualAccount(FedorasMarket.ACCOUNT_VIRTUAL_CUSTOMER_ID_PREFIX + customer.account.getIdentifier());
            if(!(opt_owner_acc.isPresent() && opt_customer_acc.isPresent()))
                return true;

            CustomInventory owner_v_inv = CustomInventory.builder().size(FedorasMarket.getMaxItemStacks()).build();
            CustomInventory customer_v_inv = CustomInventory.builder().size(FedorasMarket.getMaxItemStacks()).build();

            owner_v = new TradeActiveParty(opt_owner_acc.get(), owner_v_inv);
            customer_v = new TradeActiveParty(opt_customer_acc.get(), customer_v_inv);
        }

        // Be VERY careful here; we need to make sure nothing gets transferred if there is an error later on

        boolean failed = tryApply(owner, customer, owner_v, customer_v);

        if (failed) {
            for (Map.Entry<Currency, BigDecimal> entry : owner_v.account.getBalances().entrySet()) {
                owner_v.account.transfer(owner.account, entry.getKey(), entry.getValue(), Cause.of(this));
            }

            for (Map.Entry<Currency, BigDecimal> entry : customer_v.account.getBalances().entrySet()) {
                customer_v.account.transfer(customer.account, entry.getKey(), entry.getValue(), Cause.of(this));
            }

            //TODO: dis gonna be messy
            /*while(!owner_v.inventory.isEmpty()) {
                ItemStack itemStack;
                {
                    Optional<ItemStack> opt_itemStack = owner_v.inventory.poll();
                    if(opt_itemStack.isPresent())
                        break;
                    itemStack = opt_itemStack.get();
                }


                InventoryTransactionResult itr = owner.inventory.offer(itemStack);
                for(ItemStackSnapshot itemStackSnapshot : itr.getRejectedItems()) {
                    // Mail the items to them (i.e. set up a special shop that they have to purchase... or something
                }
            }*/

            /*for (Map.Entry<Currency, BigDecimal> entry : customer_v.account.getBalances().entrySet()) {
                customer_v.account.transfer(customer.account, entry.getKey(), entry.getValue(), Cause.of(this));
            }*/

            return true;
        }

        return finishApply(owner, customer, owner_v, customer_v);
    }

    public String toString() {
        return "TradeType: " + tradeType + ", Owner Party: {" + ownerParty + "}, Customer Party: {" + customerParty + "}";
    }
}
