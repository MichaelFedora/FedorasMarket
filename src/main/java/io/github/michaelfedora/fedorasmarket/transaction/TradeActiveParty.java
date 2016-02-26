package io.github.michaelfedora.fedorasmarket.transaction;

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.economy.account.Account;

/**
 * Data for actually doing the transaction (accounts & inventories)
 */
public class TradeActiveParty implements java.io.Serializable {
    public final Account account;
    public final Inventory inventory;

    public TradeActiveParty(Account account, Inventory inventory) {
        this.account = account;
        this.inventory = inventory;
    }
}
