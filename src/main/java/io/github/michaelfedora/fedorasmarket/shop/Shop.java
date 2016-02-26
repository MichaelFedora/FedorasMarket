package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.data.TradeType;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.transaction.TradeActiveParty;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.Optional;

/**
 * Created by MichaelFedora on 1/23/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class Shop {

    public final Sign sign;
    public final Account account;
    public final Inventory inventory;
    public final TradeType tradeType;
    public final TradeTransaction tradeTransaction;
    public final ShopModifier shopModifier;

    //private Shop() { }
    private Shop(Sign sign, Account account, Inventory inventory, TradeType tradeType, TradeTransaction tradeTransaction, ShopModifier modifier) {

        // check enums to make sure they line up

        if(!modifier.isValidWith(tradeType)) {
            modifier = ShopModifier.NONE;
        }

        this.sign = sign;
        this.account = account;
        this.inventory = inventory;
        this.tradeType = tradeType;
        this.tradeTransaction = tradeTransaction;
        this.shopModifier = modifier;
    }

    public static Shop makeShop(Sign sign, Account account, Inventory inventory, TradeType tradeType, TradeTransaction tradeTransaction) {
        return new Shop(sign, account, inventory, tradeType, tradeTransaction, ShopModifier.NONE);
    }

    public static Shop makeShop(Sign sign, Account account, Inventory inventory, TradeType tradeType, TradeTransaction tradeTransaction, ShopModifier shopModifier) {
        return new Shop(sign, account, inventory, tradeType, tradeTransaction, shopModifier);
    }

    public void doPrimary(Player player) {

        EconomyService eco = FedorasMarket.getEconomyService();

        TradeActiveParty owner;
        TradeActiveParty customer;
        {
            Optional<UniqueAccount> opt_uacc = eco.getAccount(player.getUniqueId());
            if(!opt_uacc.isPresent())
                return;

            owner = new TradeActiveParty(account, inventory);
            customer = new TradeActiveParty(opt_uacc.get(), player.getInventory());
        }

        tradeTransaction.apply(owner, customer);

    }

    public void doSecondary(Player player) {

        if(shopModifier == ShopModifier.NONE)
            return;

        EconomyService eco = FedorasMarket.getEconomyService();

        TradeActiveParty owner;
        TradeActiveParty customer;
        {
            Optional<UniqueAccount> opt_uacc = eco.getAccount(player.getUniqueId());
            if(!opt_uacc.isPresent())
                return;

            owner = new TradeActiveParty(account, inventory);
            customer = new TradeActiveParty(opt_uacc.get(), player.getInventory());
        }

        shopModifier.execute(this, owner, customer);
    }

    public DataTransactionResult save() {

        // save the data to the sign
        DataTransactionResult dtr = sign.offer(FmDataKeys.SHOP_DATA, this);

        FedorasMarket.getLogger().info("DTR: " + dtr);

        return dtr;
    }

}
