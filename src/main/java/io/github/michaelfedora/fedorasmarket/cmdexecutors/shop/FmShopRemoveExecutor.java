package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 3/13/2016.
 */
public class FmShopRemoveExecutor extends FmExecutorBase {

    public static List<String> ALIASES = Arrays.asList("remove", "rem");

    public static final String NAME = FmShopExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmShopExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Removes a shop (sign & reference)"))
                .permission(PERM)
                .executor(new FmShopRemoveExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void OnInteractSecondary(InteractBlockEvent.Secondary event, Player player) {

        Sign sign;
        {
            Optional<Sign> opt_sign = FmUtil.getShopSignFromBlockSnapshot(event.getTargetBlock());
            if(!opt_sign.isPresent())
                return;
            sign = opt_sign.get();
        }

        if(!sign.get(FmDataKeys.SHOP_REFERENCE).isPresent())
            return;

        event.setCancelled(true);

        ShopReference shopReference = sign.get(FmDataKeys.SHOP_REFERENCE).get();

        if( !(shopReference.author == player.getUniqueId() || player.hasPermission(PluginInfo.DATA_ROOT + ".admin.removeshop")) ) // TODO: make admin commands :3
            return;

        try(Connection conn = DatabaseManager.getConnection()) {

            boolean success = DatabaseManager.delete(conn, shopReference.author, DatabaseCategory.SHOPDATA, shopReference.instance);
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
        FmUtil.giveItem(SignIStack, player);

        msg(player, "Removed shop!");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
