package io.github.michaelfedora.fedorasmarket.trade;

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.economy.account.Account;

/**
 * Data for actually doing the trade (accounts & inventories)
 */
public class TradeActiveParty {
    public final Account account;
    public final Inventory inventory;

    public TradeActiveParty(Account account, Inventory inventory) {
        this.account = account;
        this.inventory = inventory;
    }
}
