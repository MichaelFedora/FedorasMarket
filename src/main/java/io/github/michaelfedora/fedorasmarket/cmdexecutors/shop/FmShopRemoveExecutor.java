package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Michael on 3/13/2016.
 */
public class FmShopRemoveExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "shop remove";
    }

    public void OnInteractSecondary(InteractBlockEvent.Secondary event, Player player) {

        Sign sign;
        {
            Optional<Sign> opt_sign = FmUtil.getSignFromBlockSnapshot(event.getTargetBlock());
            if(!opt_sign.isPresent())
                return;
            sign = opt_sign.get();
        }

        if(!sign.get(FmDataKeys.SHOP_REFERENCE).isPresent())
            return;

        event.setCancelled(true);

        ShopReference shopReference = sign.get(FmDataKeys.SHOP_REFERENCE).get();

        if(shopReference.author != player.getUniqueId() && player.hasPermission(PluginInfo.DATA_ROOT + "admin.delete")) // TODO: make admin commands :3

        try(Connection conn = DatabaseManager.getConnection()) {

            boolean success = DatabaseManager.shopDataDB.delete(conn, shopReference.author, shopReference.name, shopReference.instance);
            if(success)
                msg(player, "Success in deleting the shop-reference from the database");
            else
                msg(player, "Could not delete the shop-reference from the database, make sure to run '/fm shop clean'!");

        } catch(SQLException e) {
            throwSafeException("SQL Error", e, player);
            return;
        }

        ItemStack SignIStack = ItemStack.of(ItemTypes.SIGN, 1);
        sign.getLocation().removeBlock();
        InventoryTransactionResult itr = player.getInventory().offer(SignIStack);
        switch(itr.getType()) {
            case FAILURE:
            case CANCELLED:
                Location<World> location = player.getLocation();
                World world = location.getExtent();
                Optional<Entity> opt_entity = world.createEntity(EntityTypes.ITEM, location.getPosition());
                if(opt_entity.isPresent()) {
                    Entity entity = opt_entity.get();
                    entity.offer(Keys.REPRESENTED_ITEM, SignIStack.createSnapshot());
                    world.spawnEntity(entity, Cause.of(NamedCause.source(EntitySpawnCause.builder().entity(entity).type(SpawnTypes.PLUGIN).build())));
                } // else we really done goofed
                break;
        }
        msg(player, "Removed shop!");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw sourceNotPlayerException;

        UUID playerId = ((Player) src).getUniqueId();

        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
