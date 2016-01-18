package io.github.michaelfedora.fedoraschestshop;

/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */

import com.google.inject.Inject;

import jdk.nashorn.internal.runtime.options.Option;
import org.slf4j.Logger;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@Plugin(id = "FedorasChestShop", name = "Fedora's Chest Shop", version = "0.1")
public class FedorasChestShop {

    @Inject
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    @Listener
    public void onServerInit(GameStartedServerEvent event) {
        getLogger().info("== Loading... ==");

        getLogger().info("== Loaded! Enjoy! ==");
    }

    @Listener
    public void onSignChangeEvent(ChangeSignEvent event) {
        getLogger().info("Sign Found!");

        ListValue<Text> texts = event.getText().lines(); // get the text
        Sign sign = event.getTargetTile(); // get our tile
        Location<World> location = sign.getLocation(); // get the location
        BlockState blockState = sign.getBlock(); // get north/south/east/west

        getLogger().info("Texts: " + texts);
        getLogger().info("Sign: " + sign);
        getLogger().info("Location: " + location);
        getLogger().info("BlockState: " + blockState);

        //TODO: Use `blockstate` and `location` to get the anchor-block's loc
        //TODO: Check if anchor-block is chest, if false exit
        //TODO: Check if sign text is correct, if false exit
        //DISCUSS: Double chest - check, to make sure we won't sell more than
        //          one thing out of one chest? IS THIS NECESSARY?
        //TODO: Use both Trapped Chests and Standard Chests

    }
}
