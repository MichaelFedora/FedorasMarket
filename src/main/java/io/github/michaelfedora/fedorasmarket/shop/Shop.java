package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

/**
 * Created by MichaelFedora on 1/23/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class Shop {

    protected Sign sign;
    protected Account account;
    protected Inventory inventory;
    protected TradeType tradeType;
    protected TradeForm tradeForm;
    protected ShopModifier shopModifier;

    protected ShopReference reference;

    //private Shop() { }
    public Shop(Sign sign, Account account, Inventory inventory, TradeType tradeType, TradeForm tradeForm, ShopModifier modifier) {

        // check enums to make sure they line up

        if(!modifier.isValidWith(tradeType)) {
            modifier = ShopModifier.NONE;
        }

        this.sign = sign;
        this.account = account;
        this.inventory = inventory;
        this.tradeType = tradeType;
        this.tradeForm = tradeForm;
        this.shopModifier = modifier;
    }

    public static Shop makeShop(Sign sign, Account account, Inventory inventory, TradeType tradeType, TradeForm tradeForm) {
        return new Shop(sign, account, inventory, tradeType, tradeForm, ShopModifier.NONE);
    }

    public static Shop makeShop(Sign sign, Account account, Inventory inventory, TradeType tradeType, TradeForm tradeForm, ShopModifier shopModifier) {
        return new Shop(sign, account, inventory, tradeType, tradeForm, shopModifier);
    }

    public void refresh() {
        // update with the db reference
    }

    public ShopReference toReference() {
        return new ShopReference();
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

        tradeForm.apply(owner, customer);

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

    /*public void tryMakeShop() {

        Direction dir;
        {
            Optional<Direction> opt_dir = sign_bs.get(Keys.DIRECTION);
            if (!opt_dir.isPresent())
                return;
            dir = opt_dir.get();
        }

        Location<World> chest_loc = sign.getLocation().getRelative(dir.getOpposite());
        BlockState chest_bs = chest_loc.getBlock();

        FedorasMarket.getLogger().info("Direction is " + dir + ", " +
                "Opposite is " + dir.getOpposite() + ", " +
                "Block is " + chest_bs.getType());

        {
            String name = chest_bs.getType().getName();
            getLogger().info("Chest's name is: `" + name + "`");

            boolean bad = true;

            for(String chestName : chestNames) {
                if(name.equals(chestName)) {
                    bad = false;
                    break;
                }
            }

            if(bad)
                return;
        }

        FedorasMarket.getLogger().info("Chest is in the list!");

        TileEntity te;
        {
            Optional<TileEntity> opt_te = chest_loc.getTileEntity();
            if(!opt_te.isPresent())
                return;
            te = opt_te.get();
        }

        if(!(te instanceof TileEntityCarrier))
            return;

        TileEntityCarrier tec = (TileEntityCarrier) te;

        FedorasMarket.getLogger().info("Chest is a tile entity carrier!");
    }*/

    public DataTransactionResult save() {

        // save the data to the sign
        DataTransactionResult dtr = sign.offer(FmDataKeys.SHOP_REFERENCE, this.toReference());

        FedorasMarket.getLogger().info("DTR: " + dtr);

        return dtr;
    }

}
