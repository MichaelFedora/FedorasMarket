package io.github.michaelfedora.fedorasmarket.transaction;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by MichaelFedora on 1/24/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 *
 * TODO: Serialize.
 */
public class TradeTransaction {

    public TransactionParty ownerParty;
    public TransactionParty customerParty;
    private Optional<TransferResult> lastTransferResult;
    private Optional<InventoryTransactionResult> lastInventoryTransactionResult;

    public Optional<TransferResult> getLastTransferResult() { return this.lastTransferResult; }
    public Optional<InventoryTransactionResult> getLastInventoryTransactionResult() { return this.lastInventoryTransactionResult; }


    public TradeTransaction(TransactionParty owner, TransactionParty customer) {
        this.ownerParty = owner;
        this.customerParty = customer;
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
        for(Map.Entry<Currency,BigDecimal> entry : ownerParty.getCurrencies().entrySet()) {
            result = owner.transfer(owner_v, entry.getKey(), entry.getValue(), Cause.of(this));

            if(result.getResult() != ResultType.SUCCESS)
                return Optional.of(result);
        }

        for(Map.Entry<Currency, BigDecimal> entry : customerParty.getCurrencies().entrySet()) {
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

    private boolean tryApply(TransactionActiveParty owner, TransactionActiveParty customer, TransactionActiveParty owner_v, TransactionActiveParty customer_v) {

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

    private boolean finishApply(TransactionActiveParty owner, TransactionActiveParty customer, TransactionActiveParty owner_v, TransactionActiveParty customer_v) {

        finishApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        return false;

        /*lastTransferResult = finishApplyCurrency(owner.account, customer.account, owner_v.account, customer_v.account);

        if (lastTransferResult.isPresent())
            if (lastTransferResult.get().getResult() != ResultType.SUCCESS)
                return true;

        return false;*/
    }

    public boolean apply(TransactionActiveParty owner, TransactionActiveParty customer) {
        EconomyService eco = FedorasMarket.getEconomyService();

        TransactionActiveParty owner_v;
        TransactionActiveParty customer_v;
        {
            Optional<VirtualAccount> opt_owner_acc = eco.createVirtualAccount(FedorasMarket.ACCOUNT_VIRTUAL_OWNER_ID_PREFIX + owner.account.getIdentifier());
            Optional<VirtualAccount> opt_customer_acc = eco.createVirtualAccount(FedorasMarket.ACCOUNT_VIRTUAL_CUSTOMER_ID_PREFIX + customer.account.getIdentifier());
            if(!(opt_owner_acc.isPresent() && opt_customer_acc.isPresent()))
                return true;

            //TODO: Find a good size of things
            CustomInventory owner_v_inv = CustomInventory.builder().size(FedorasMarket.getMaxItemStacks()).build();
            CustomInventory customer_v_inv = CustomInventory.builder().size(FedorasMarket.getMaxItemStacks()).build();

            owner_v = new TransactionActiveParty(opt_owner_acc.get(), owner_v_inv);
            customer_v = new TransactionActiveParty(opt_customer_acc.get(), customer_v_inv);
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
}
