package io.github.michaelfedora.fedoraschestshop;

/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */

import com.google.inject.Inject;

import io.github.michaelfedora.fedoraschestshop.shop.transaction.ShopTransaction;
import io.github.michaelfedora.fedoraschestshop.shop.Shop;
import org.slf4j.Logger;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Plugin(id = "FedorasChestShop", name = "Fedora's Chest Shop", version = "0.1")
public class FedorasChestShop {

    @Inject
    private Logger logger;
    public Logger getLogger() {
        return logger;
    }

    List<String> chestNames = new ArrayList<>();

    @Listener
    public void onServerInit(GameStartedServerEvent event) {
        getLogger().info("== Loading... ==");

        //TODO: Add read-config for chest names
        chestNames.add("minecraft:chest");
        chestNames.add("minecraft:trapped_chest");

        getLogger().info("== Loaded! Enjoy! ==");
    }

    @Listener void onSignChangeevent(ChangeSignEvent event) {

    }

    public void setupShop(Sign sign, List<String> lines) {
        getLogger().info("Setting up shop for sign `" + sign + "`!");

        BlockState sign_bs = sign.getBlock();

        //TODO: Check if sign text is correct, if false exit
        //TODO: == If both same action (buy/sell), put larger quantity on top
        //TODO: == If different actions, put sell on bottom (primary)
        //TODO: == Register a shop by placing a book with parameters inside the chest, and then a sign outside with [FCS-Register]
        //TODO: == == Book Shop Params: item ; buy/sell:amount:price ; buy/sell:amount:price
        //TODO: == == Book Trade Params: take_item ; give_item ; take:give ratio
        //== == == == i.e.: minecraft:trapped_chest ; buy:1:10 ; sell:1:25 ;
        //== == == == i.e.: minecraft:iron_ingot ; minecraft:gold_nugget ; 1:14
        //TODO: == == Sign Params: [FedChestShop] ; flags
        //= == == == flags
        //TODO: == == Shop-Sign becomes: [FCS][Shop] ; [ScrollingItemName] ; B/S:#:$ /*Secondary*/ ; B/S:#:$ /*Primary*/
        //== == == == Colors:            Black Bold  ; Black               ; Green/Red ] ; Green/Red [Bold & Dark if Larger Quantity & same op]
        //TODO: == == Trade-Sign becomes: [FCS][Trade] ; [ScrollingTakeItemName] ; ratio ; [ScrollingGiveItemName]
        //== == == == Colors:             Purple Bold  ; Dark Red (Bold)         ; red:green ; Dark Green (Bold)

        {
            boolean attached;
            {
                Optional<Boolean> opt_attached = sign_bs.get(Keys.ATTACHED);
                if(!opt_attached.isPresent())
                    return;
                attached = opt_attached.get();
            }
            getLogger().info("Attached: " + attached);
            if (!attached)
                return;
        }

        Direction dir;
        {
            Optional<Direction> opt_dir = sign_bs.get(Keys.DIRECTION);
            if (!opt_dir.isPresent())
                return;
            dir = opt_dir.get();
        }

        Location<World> chest_loc = sign.getLocation().getRelative(dir.getOpposite());
        BlockState chest_bs = chest_loc.getBlock();

        getLogger().info("Direction is " + dir + ", " +
                "Opposite is " + dir.getOpposite() + ", " +
                "Block is " + chest_bs.getType());

        /*getLogger().info("Chest's key's are: " + chest_bs.getKeys());

        {
            String s = "Chest's Traits: [ ";

            for (BlockTrait<?> bt : chest_bs.getTraits()) {
                s += bt.getName() + " ";
            }
            s += "]";
            getLogger().info(s);

        }*/

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

        getLogger().info("Chest is in the list!");

        TileEntity te;
        {
            Optional<TileEntity> opt_te = chest_loc.getTileEntity();
            if(!opt_te.isPresent())
                return;
            te = opt_te.get();
        }

        if(!(te instanceof TileEntityCarrier))
            return;

        getLogger().info("Chest is a tile entity carrier!");


        //=== SETTING UP THE SIGN


        ListValue<Text> sign_lines = sign.getSignData().lines(); // get the text


        Shop.Type shopType;
        {
            String s = lines.get(0).toLowerCase();
            if(s.equals("[shop]"))
                shopType = Shop.Type.ECON;
            else if(s.equals("[trade]"))
                shopType = Shop.Type.TRADE;
            else {
                getLogger().warn("Not a correct type: " + lines.get(0));
                return;
            }
        }

        switch(shopType) {

            case ECON:
                sign_lines.set(0, Text.of(TextColors.BLACK, TextStyles.BOLD, "[FCS]", TextColors.DARK_GRAY, "[Shop]"));
                break;

            case TRADE:
                sign_lines.set(0, Text.of(TextColors.BLACK, TextStyles.BOLD, "[FCS]", TextColors.DARK_PURPLE, "[Trade]"));
                break;

            default:
                getLogger().error("Unsupported shop type: " + shopType);
                return;
        }

        //TileEntityCarrier tec = (TileEntityCarrier) te;
        //Inventory inv = tec.getInventory(); //TODO: Implement Inventory (once sponge has it)
    }

    private void trySetup(Sign sign, ItemStack is) {

        List<String> lines = new ArrayList<String>();
        {
            List<Text> pages;
            {
                Optional<List<Text>> opt_pages = is.get(Keys.BOOK_PAGES);
                if (!opt_pages.isPresent())
                    return;
                pages = opt_pages.get();
            }
            String page = pages.get(0).toPlain();
            String[] list = page.split("\n");
            for (int i = 0; i < list.length; i++)
                lines.set(i, list[i]);
        }

        setupShop(sign, lines);
    }

    private void doTransaction(boolean isSecondary, Shop shop) {
        // then lets do a transaction
        switch(shop.getType()) {
            case ECON:
                ShopTransaction st = shop.getShopData().get().tData[0];
                switch(st.op) {
                    case BUY:
                        // check to make sure chest has the materials
                        // check to make sure we have the money
                        // transfer materials
                        break;

                    case SELL:
                        // check to make sure we have the materials
                        // check to make sure shop-owner has the money
                        // transfer materials
                        break;

                    default:
                        getLogger().error("Something went wrong with getting op's type...");
                        return;
                }
                break;

            case TRADE:
                // check to make sure player has the materials
                // check to make sure chest has the materials
                // do a confirm?? enforce user to be holding the item??
                // transfer materials
                break;

            default:
                getLogger().error("Something went wrong with getting shop's type...");
                return;
        }
    }

    public static Optional<Sign> getSignFromLocation(Location<World> loc) {
        TileEntity te;
        {
            Optional<TileEntity> opt_te = loc.getTileEntity();
            if(!opt_te.isPresent())
                return Optional.empty(); //TODO: Add errors to all these optional-returns
            te = opt_te.get();
        }
        if(te instanceof Sign)
            return Optional.of((Sign) te);

        return Optional.empty();
    }

    public static Optional<Sign> getSignFromBlockSnapshot(BlockSnapshot bsnap) {

        Optional<Location<World>> opt_loc = bsnap.getLocation();
        if (opt_loc.isPresent())
            return getSignFromLocation(opt_loc.get());

        return Optional.empty();
    }

    @Listener
    public void onBlockInteractPrimary(InteractBlockEvent.Primary event) {

        BlockSnapshot sign_bsnap = event.getTargetBlock();

        Sign sign;
        {
            Optional<Sign> opt_sign = getSignFromBlockSnapshot(sign_bsnap);
            if(!opt_sign.isPresent())
                return;
            sign = opt_sign.get();
        }

        Shop shop;
        {
            Optional<Shop> opt_shop = Shop.make(sign);
            if (!opt_shop.isPresent())
                return;
            shop = opt_shop.get();
        }

        doTransaction(false, shop);
    }

    @Listener
    public void onBlockInteractSecondary(InteractBlockEvent.Secondary event) {

        BlockSnapshot sign_bsnap = event.getTargetBlock();
        Sign sign;
        {
            Optional<Sign> opt_sign = getSignFromBlockSnapshot(sign_bsnap);
            if(!opt_sign.isPresent())
                return; //TODO: Add note to these things?
            sign = opt_sign.get();
        }

        Player plr;
        {
            Optional<Player> opt_plr = event.getCause().first(Player.class);
            if(!opt_plr.isPresent())
                return;
            plr = opt_plr.get();
        }


        {
            Optional<ItemStack> opt_is = plr.getItemInHand();
            if (opt_is.isPresent()) {
                if (opt_is.get().getItem().getName().equals("minecraft:writable_book")) {
                    trySetup(sign, opt_is.get());
                    return;
                }
            }
        }

        // if not setting up shop, lets check if the sign is valid in the first place
        Shop shop;
        {
            Optional<Shop> opt_shop = Shop.make(sign);
            if (!opt_shop.isPresent())
                return;
            shop = opt_shop.get();
        }

        doTransaction(true, shop);

    }
}
