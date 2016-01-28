package io.github.michaelfedora.fedoraschestshop.shop;

import io.github.michaelfedora.fedoraschestshop.FedorasChestShop;
import javafx.util.Pair;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by MichaelFedora on 1/23/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class Shop {

    private enum OwnerType {
        USER,
        SERVER
    }

    public enum GoodType {
        ALL,
        ITEM,
        CURRENCY
    }

    /**
     * The Shop Type is what the customer is doing; Are they Buying Items? Selling Items? Trading Currency? Etc.
     */
    public enum ShopType {
        ITEM_BUY(GoodType.ITEM, GoodType.CURRENCY),
        ITEM_SELL(GoodType.CURRENCY, GoodType.ITEM),
        ITEM_TRADE(GoodType.ITEM, GoodType.ITEM),
        CURRENCY_TRADE(GoodType.CURRENCY, GoodType.CURRENCY),
        CUSTOM(GoodType.ALL, GoodType.ALL);

        public final GoodType ownerGoodType;
        public final GoodType customerGoodType;

        public boolean has(GoodType goodType) {
            return (
                    ownerGoodType == goodType
                    || customerGoodType == goodType
                    || ownerGoodType == GoodType.ALL
                    || customerGoodType == GoodType.ALL
                    || goodType == GoodType.ALL
            );
        }

        public boolean exclusiveHas(GoodType goodType) {
            return (ownerGoodType == goodType || customerGoodType == goodType);
        }

        ShopType(GoodType ownerGoodType, GoodType customerGoodType) {
            this.ownerGoodType = ownerGoodType;
            this.customerGoodType = customerGoodType;
        }
    }

    public enum SecondaryOp {
        NONE(GoodType.ALL, false, null),
        ITEM_SELL_SWITCH(GoodType.ITEM, true, ShopTransaction.Party.class, new ShopType[] {ShopType.ITEM_BUY}), // Only works for ITEM_BUY
        ITEM_AUTO_STACK(GoodType.ITEM, true, null), // Works for all item types (could be non-exclusive, but CUSTOM)
        FIXED_STACK(GoodType.ALL, false, Integer.class); // Works for all types except CUSTOM

        public final GoodType goodType;
        public boolean exclusiveGood;
        public final Class paramType;
        public final ShopType[] restrictedShopTypes;

        public boolean restrictedShopType() { return (restrictedShopTypes.length > 0); }
        public boolean requiresParam() { return (paramType != null); }

        public boolean isCompatibleWith(ShopType shopType) {

            boolean ret;

            if(exclusiveGood)
                ret = shopType.exclusiveHas(goodType);
            else
                ret = shopType.has(goodType);

            if(restrictedShopType())
                ret = ret && Arrays.asList(restrictedShopTypes).contains(shopType);

            return ret;
        }

        SecondaryOp(GoodType goodType, boolean exclusiveGood, Class classType) {
            this.goodType = goodType;
            this.exclusiveGood = exclusiveGood;
            this.paramType = classType;
            this.restrictedShopTypes = new ShopType[]{};
        }

        SecondaryOp(GoodType goodType, boolean exclusiveGood, Class classType, ShopType[] restrictedShopTypes) {
            this.goodType = goodType;
            this.exclusiveGood = exclusiveGood;
            this.paramType = classType;
            this.restrictedShopTypes = restrictedShopTypes;
        }

    }

    private Sign sign;
    private UUID owner;
    private OwnerType ownerType; // internal, to determine USER vs SERVER shop
    private ShopType shopType;
    private ShopTransaction shopTransaction;
    private SecondaryOp secondaryOp;
    private Object secondaryParam;

    private Optional<?> getSecondaryParam() {

        if(secondaryParam.getClass() == secondaryOp.paramType)
            return Optional.of(secondaryParam);

        return Optional.empty();
    }

    //private Shop() { }
    private Shop(Sign sign, UUID owner, OwnerType ownerType, ShopType shopType, ShopTransaction shopTransaction, SecondaryOp secondaryOp, Object secondaryParam) {

        // check enums to make sure they line up

        if(!secondaryOp.isCompatibleWith(shopType)) {

            secondaryOp = SecondaryOp.NONE;
            secondaryParam = null;

        } else if(secondaryParam.getClass() != secondaryOp.paramType) {

            secondaryParam = null;

            if(secondaryOp.requiresParam())
                secondaryOp = SecondaryOp.NONE;
        }

        this.sign = sign;
        this.owner = owner;
        this.ownerType = ownerType;
        this.shopType = shopType;
        this.shopTransaction = shopTransaction;
        this.secondaryOp = secondaryOp;
        this.secondaryParam = secondaryParam;
    }

    // no secondary ops
    public static Shop makeUserShop(Sign sign, UUID owner, ShopType shopType, ShopTransaction shopTransaction) {
        return new Shop(sign, owner, OwnerType.USER, shopType, shopTransaction, SecondaryOp.NONE, null);
    }

    public static Shop makeServerShop(Sign sign, ShopType shopType, ShopTransaction shopTransaction) {
        return new Shop(sign, null, OwnerType.SERVER, shopType, shopTransaction, SecondaryOp.NONE, null);
    }

    // no secondaryParams
    public static Shop makeUserShop(Sign sign, UUID owner, ShopType shopType, ShopTransaction shopTransaction, SecondaryOp secondaryOp) {
        return new Shop(sign, owner, OwnerType.USER, shopType, shopTransaction, secondaryOp, null);
    }

    public static Shop makeServerShop(Sign sign, ShopType shopType, ShopTransaction shopTransaction, SecondaryOp secondaryOp) {
        return new Shop(sign, null, OwnerType.SERVER, shopType, shopTransaction, secondaryOp, null);
    }

    // == everything
    public static Shop makeUserShop(Sign sign, UUID owner, ShopType shopType, ShopTransaction shopTransaction, SecondaryOp secondaryOp, Object secondaryParam) {
        return new Shop(sign, owner, OwnerType.USER, shopType, shopTransaction, secondaryOp, secondaryParam);
    }

    public static Shop makeServerShop(Sign sign, ShopType shopType, ShopTransaction shopTransaction, SecondaryOp secondaryOp, Object secondaryParam) {
        return new Shop(sign, null, OwnerType.SERVER, shopType, shopTransaction, secondaryOp, secondaryParam);
    }

    public Optional<Pair<Account, Account>> getAccounts(UUID customer) {
        EconomyService eco = FedorasChestShop.getEconomyService();

        Account owner_acc;
        switch(ownerType) {
            case USER:
            {
                Optional<UniqueAccount> opt_uacc = eco.getAccount(owner);
                if(!opt_uacc.isPresent())
                    return Optional.empty();
                owner_acc = opt_uacc.get();
            }
            break;

            case SERVER:
            {
                Optional<Account> opt_acc = eco.getAccount("fedoraschestshop:server");
                if(!opt_acc.isPresent())
                    return Optional.empty();
                owner_acc = opt_acc.get();
            }
            break;

            default:
                return Optional.empty();
        }

        Account customer_acc;
        {
            Optional<UniqueAccount> opt_uacc = eco.getAccount(customer);
            if(!opt_uacc.isPresent())
                return Optional.empty();
            customer_acc = opt_uacc.get();
        }

        return Optional.of(new Pair<Account,Account>(owner_acc, customer_acc));
    }

    public void doPrimary(Player player) {

        Account owner_acc;
        Account customer_acc;
        {
            Optional<Pair<Account,Account>> opt_accs = getAccounts(player.getUniqueId());
            if(!opt_accs.isPresent())
                return;
            owner_acc = opt_accs.get().getKey();
            customer_acc = opt_accs.get().getValue();
        }

        shopTransaction.apply(owner_acc, customer_acc);

    }

    public void doSecondary(Player player) {

        if(secondaryOp == SecondaryOp.NONE)
            return;

        Account owner_acc;
        Account customer_acc;
        {
            Optional<Pair<Account,Account>> opt_accs = getAccounts(player.getUniqueId());
            if(!opt_accs.isPresent())
                return;
            owner_acc = opt_accs.get().getKey();
            customer_acc = opt_accs.get().getValue();
        }

        switch(secondaryOp) {

            case NONE:
                // don't do anything :)
                break;

            case ITEM_SELL_SWITCH:

                ShopTransaction.Party ownerSellParty;
                {
                    Optional<?> opt = getSecondaryParam();
                    if(!opt.isPresent())
                        return;
                    ownerSellParty = (ShopTransaction.Party) opt.get();
                }

                ShopTransaction switchShopTransaction = new ShopTransaction(ownerSellParty, this.shopTransaction.customerParty);
                switchShopTransaction.apply(owner_acc, customer_acc);

                break;

            case ITEM_AUTO_STACK:

                // get size limit for inventories, the limit for items, and the limit for currencies
                // do transaction as many times as there are in the smallest item's stack size
                break;

            case FIXED_STACK:

                int stackSize = 1;
                {
                    Optional<?> opt = getSecondaryParam();
                    if(!opt.isPresent())
                        return;
                    stackSize = (Integer) opt.get();
                }

                // do transactions up-to or as close-to the stackSize in one swipe


                break;
        }
    }

    public DataTransactionResult save() {

        // save the data to the sign
        DataTransactionResult dtr = sign.offer(ShopKeys.DATA, this);

        FedorasChestShop.getLogger().info("DTR: " + dtr);

        return dtr;
    }

}
