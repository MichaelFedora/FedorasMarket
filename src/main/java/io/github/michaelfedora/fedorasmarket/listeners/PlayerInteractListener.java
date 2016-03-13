package io.github.michaelfedora.fedorasmarket.listeners;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Michael on 2/23/2016.
 */
public class PlayerInteractListener {

    static {
        FedorasMarket.toRegister.add(PlayerInteractListener.class);
    }

    @Listener
    public void onPlayerPrimary(InteractBlockEvent.Primary event, @First Player player) {

        //FedorasMarket.getLogger().info("Player interacted: " + ((player != null) ? player.getName() : "null") + ", event: " + event);

        if(player == null)
            return;

        /*BlockSnapshot sign_bsnap = event.getTargetBlock();

        Sign sign;
        {
            Optional<Sign> opt_sign = getSignFromBlockSnapshot(sign_bsnap);
            if(!opt_sign.isPresent())
                return;
            sign = opt_sign.get();
        }

        Shop shop;
        {
            Optional<Shop> opt_shop = sign.get(FmDataKeys.SHOP_DATA);
            if (!opt_shop.isPresent())
                return;
            shop = opt_shop.get();
        }

        Player player;
        {
            Optional<Player> opt_player = event.getCause().first(Player.class);
            if(!opt_player.isPresent())
                return;
            player = opt_player.get();
        }

        shop.doSecondary(player);*/
    }

    @Listener
    public void onPlayerSecondary(InteractBlockEvent.Secondary event, @First Player player) {

        //FedorasMarket.getLogger().info("Player interacted: " + ((player != null) ? player.getName() : "null") + ", event: " + event);

        if(player == null)
            return;

        /*BlockSnapshot sign_bsnap = event.getTargetBlock();
        Sign sign;
        {
            Optional<Sign> opt_sign = getSignFromBlockSnapshot(sign_bsnap);
            if(!opt_sign.isPresent())
                return; //TODO: Add note to these things?
            sign = opt_sign.get();
        }

        Player player;
        {
            Optional<Player> opt_player = event.getCause().first(Player.class);
            if(!opt_player.isPresent())
                return;
            player = opt_player.get();
        }


        {
            Optional<ItemStack> opt_is = player.getItemInHand();
            if (opt_is.isPresent()) {
                if (opt_is.get().getItem().getName().equals("minecraft:writable_book")) {
                    trySetup(sign, player, opt_is.get());
                    return;
                }
            }
        }

        // if not setting up shop, lets check if the sign is valid in the first place
        Shop shop;
        {
            Optional<Shop> opt_shop = sign.get(FmDataKeys.SHOP_DATA);
            if (!opt_shop.isPresent())
                return;
            shop = opt_shop.get();
        }

        shop.doPrimary(player);*/

    }
}
