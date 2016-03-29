package io.github.michaelfedora.fedorasmarket.trade;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.database.FmSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
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
public class TradeForm implements FmSerializable<SerializedTradeForm> {

    private Cause thisAsCause() { return Cause.of(NamedCause.of("TradeForm", this)); }

    private TradeType tradeType;
    private TradeParty ownerParty;
    private TradeParty customerParty;

    public transient Optional<TransferResult> lastTransferResult;
    public transient Optional<InventoryTransactionResult> lastInventoryTransactionResult;

    public TradeForm(TradeType tradeType, TradeParty ownerParty, TradeParty customerParty) {
        this.tradeType = tradeType;
        setOwnerParty(ownerParty);
        setCustomerParty(customerParty);
    }

    public SerializedTradeForm serialize() {
        SerializedTradeForm data = new SerializedTradeForm();

        data.tradeType = this.tradeType;
        data.ownerPartyData = this.ownerParty.serialize();
        data.customerPartyData = this.customerParty.serialize();

        return data;
    }

    public static TradeForm fromSerializedData(SerializedTradeForm data) {
        return new TradeForm(data.tradeType, data.ownerPartyData.safeDeserialize().get(), data.customerPartyData.safeDeserialize().get());
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
        if(owner != null) {
            for (Map.Entry<Currency, BigDecimal> entry : this.ownerParty.getCurrencies().entrySet()) {
                result = owner.transfer(owner_v, entry.getKey(), entry.getValue(), thisAsCause());

                if (result.getResult() != ResultType.SUCCESS)
                    return Optional.of(result);
            }
        }

        for(Map.Entry<Currency, BigDecimal> entry : this.customerParty.getCurrencies().entrySet()) {
            result =  customer.transfer(customer_v, entry.getKey(), entry.getValue(), thisAsCause());

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

        this.lastTransferResult = tryApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        if (this.lastTransferResult.isPresent())
            if (this.lastTransferResult.get().getResult() != ResultType.SUCCESS)
                return true;

        this.lastInventoryTransactionResult = tryApplyItems(owner.inventory, customer.inventory, owner_v.inventory, customer_v.inventory);

        if (this.lastInventoryTransactionResult.isPresent())
            if (this.lastInventoryTransactionResult.get().getType() != InventoryTransactionResult.Type.SUCCESS)
                return true;

        return false;
    }

    private void finishApplyCurrency(Account owner, Account customer, Account owner_v, Account customer_v) {

        //TODO: Add error handling
        for(Map.Entry<Currency, BigDecimal> entry : owner_v.getBalances().entrySet())
            owner_v.transfer(customer, entry.getKey(), entry.getValue(), thisAsCause());

        if(owner != null)
            for(Map.Entry<Currency, BigDecimal> entry : customer_v.getBalances().entrySet())
                customer_v.transfer(owner, entry.getKey(), entry.getValue(), thisAsCause());
    }

    private boolean finishApply(TradeActiveParty owner, TradeActiveParty customer, TradeActiveParty owner_v, TradeActiveParty customer_v) {

        //EconomyService EconomyService eco = FedorasMarket.getEconomyService();

        finishApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        return false;

        /*lastTransferResult = finishApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        if (lastTransferResult.isPresent())
            if (lastTransferResult.get().getResult() != ResultType.SUCCESS)
                return true;

        return false;*/
    }

    /**
     * Applies the transaction
     * @param owner the owner data
     * @param customer the customer data
     * @return whether or not it succeeded
     */
    public boolean apply(TradeActiveParty owner, TradeActiveParty customer) {

        EconomyService eco = FedorasMarket.getEconomyService();

        TradeActiveParty owner_v;
        if(owner == TradeActiveParty.SERVER)
            owner_v = TradeActiveParty.SERVER;
        else {
            Optional<Account> opt_owner_acc = eco.getOrCreateAccount(FedorasMarket.ACCOUNT_VIRTUAL_OWNER_PREFIX + owner.account.getIdentifier());
            if(!opt_owner_acc.isPresent())
                return true;
            CustomInventory owner_v_inv = null;//CustomInventory.builder().size(FedorasMarket.getMaxItemStacks()).build();
            owner_v = new TradeActiveParty(opt_owner_acc.get(), owner_v_inv);
        }

        TradeActiveParty customer_v;
        {

            Optional<Account> opt_customer_acc = eco.getOrCreateAccount(FedorasMarket.ACCOUNT_VIRTUAL_CUSTOMER_PREFIX + customer.account.getIdentifier());
            if(!opt_customer_acc.isPresent())
                return true;

            CustomInventory customer_v_inv = null;//CustomInventory.builder().size(FedorasMarket.getMaxItemStacks()).build();
            customer_v = new TradeActiveParty(opt_customer_acc.get(), customer_v_inv);
        }

        // Be VERY careful here; we need to make sure nothing gets transferred if there is an error later on

        boolean success = tryApply(owner, customer, owner_v, customer_v);

        if(success) {

            success = finishApply(owner, customer, owner_v, customer_v);

        } else {
            if (owner != TradeActiveParty.SERVER) {
                for (Map.Entry<Currency, BigDecimal> entry : owner_v.account.getBalances().entrySet()) {
                    owner_v.account.transfer(owner.account, entry.getKey(), entry.getValue(), thisAsCause());
                }
            }

            for (Map.Entry<Currency, BigDecimal> entry : customer_v.account.getBalances().entrySet()) {
                customer_v.account.transfer(customer.account, entry.getKey(), entry.getValue(), thisAsCause());
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
        }

        return success;
    }

    public String toString() {
        return "tradeType: " + this.tradeType + ", ownerParty: {" + this.ownerParty + "}, customerParty: {" + this.customerParty + "}";
    }
}
