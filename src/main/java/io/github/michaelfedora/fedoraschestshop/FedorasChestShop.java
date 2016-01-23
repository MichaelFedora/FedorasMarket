package io.github.michaelfedora.fedoraschestshop;

/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */

import com.google.inject.Inject;

import org.slf4j.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
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

    List<String> chestNames = new ArrayList<String>();

    @Listener
    public void onServerInit(GameStartedServerEvent event) {
        getLogger().info("== Loading... ==");

        //TODO: Add read-config for chest names
        chestNames.add("minecraft:chest");
        chestNames.add("minecraft:trapped_chest");

        getLogger().info("== Loaded! Enjoy! ==");
    }

    @Listener
    public void onSignChangeEvent(ChangeSignEvent event) {
        getLogger().info("Sign Found!");

        //ListValue<Text> texts = event.getText().lines(); // get the text
        Sign sign = event.getTargetTile(); // get our tile
        //Location<World> location = sign.getLocation(); // get the location
        BlockState sign_bs = sign.getBlock(); // get north/south/east/west

        //getLogger().info("Texts: " + texts);
        //getLogger().info("Sign: " + sign);
        //getLogger().info("Location: " + location);
        getLogger().info("BlockState: " + sign_bs);

        //FINISHED: Use `blockstate` and `location` to get the anchor-block's loc
        //FINISHED: Check if anchor-block is chest, if false exit
        //TODO: Check if sign text is correct, if false exit
        //RESOLVED: Double chest - check, to make sure we won't sell more than
        //          one thing out of one chest? IS THIS NECESSARY? - I think not :3
        //FINISHED: Use both Trapped Chests and Standard Chest

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

        getLogger().info("Chest is correct!");

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

        TileEntityCarrier tec = (TileEntityCarrier) te;
        Inventory inv = tec.getInventory(); // ERROR
        //AbstractMethodError: Method net/minecraft/tileentity/TileEntityChest.getInventory()Lorg/spongpowered/api/item/inventory/type/TileentityInventory; is abstract

        getLogger().info("Chest Inventory: " + inv);

        //TODO: Fix the get-inventory.. it's not working?

    }
}
