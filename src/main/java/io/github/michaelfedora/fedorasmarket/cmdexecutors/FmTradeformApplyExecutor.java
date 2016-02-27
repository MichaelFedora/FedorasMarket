package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 2/27/2016.
 */
public class FmTradeFormApplyExecutor implements CommandExecutor {

    public static Map<UUID,String> to_apply;

    public void error(MessageReceiver receiver, String msg) {
        receiver.sendMessage(FmUtil.makeMessageError("tradeform apply", msg));
    }

    @Listener
    public void OnInteractSecondary(InteractBlockEvent.Secondary event, @First Player player) {
        if(!to_apply.containsKey(player.getUniqueId()))
            return;

        UUID playerId = player.getUniqueId();
        String name = to_apply.get(playerId);
        to_apply.remove(playerId);

        BlockSnapshot blockSnapshot = event.getTargetBlock();
        /*if(blockSnapshot.getState().getType() != BlockTypes.WALL_SIGN) {
            error(player, "Bad block :c . I need a wall-sign!");
            return;
        }*/

        Sign sign;
        {
            Optional<Sign> opt_sign = FmUtil.getSignFromBlockSnapshot(blockSnapshot);
            if(!opt_sign.isPresent()) {
               error(player, "Bad block :c . I need a sign!");
                return;
            }
            sign = opt_sign.get();
        }

        if(sign.supports(Keys.IN_WALL)) {
            if (!sign.get(Keys.IN_WALL).get()) {
                error(player, "Sign needs to be on a wall (i.e. chest)");
                return;
            }
        } else {
            error(player, "Sign needs to be on a wall (i.e. chest)");
            return;
        }

        UUID dataId;

        while(true) {

            dataId = UUID.randomUUID();

            try {

                ResultSet resultSet = DatabaseManager.shopForms.select(playerId, name);

                if(!resultSet.next()) {
                    break;
                }

                boolean breakFlag = true;
                while(resultSet.next()) {

                    UUID otherId = (UUID) resultSet.getObject("instance");

                    if(dataId.equals(otherId)) {
                        breakFlag = false;
                        break;
                    }
                }

                if(breakFlag)
                    break;

            } catch(SQLException e) {
                FedorasMarket.getLogger().error("SQL Error: ", this, e);
                player.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
                return;
            }
        }

        sign.offer(FmDataKeys.SHOP_REFERENCE, new ShopReference(playerId, name, dataId));
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            return FmTradeFormExecutor.errorNotPlayer(src);
        }

        UUID uuid = ((Player) src).getUniqueId();

        String name;
        {
            Optional<String> opt_name = ctx.<String>getOne("name");
            if (!opt_name.isPresent())
                return CommandResult.empty();
            name = opt_name.get();
        }

        try {

            ResultSet resultSet = DatabaseManager.tradeForms.selectWithMore(uuid, name, "LIMIT 1");

            if(!resultSet.next()) {
                src.sendMessage(FmUtil.makeMessage("tradeform apply", "Bad name! :c"));
                return CommandResult.empty();
            }

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error: ", this, e);
            src.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
            return CommandResult.empty();
        }

        to_apply.put(uuid, name);

        src.sendMessage(FmUtil.makeMessage("tradeform apply","Select a block."));

        return CommandResult.success();
    }
}
