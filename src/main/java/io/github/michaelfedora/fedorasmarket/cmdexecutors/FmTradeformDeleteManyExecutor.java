package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Michael on 2/26/2016.
 */
public class FmTradeFormDeleteManyExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            return FmTradeFormExecutor.errorNotPlayer(src);
        }

        Player player = (Player) src;

        Collection<String> names = ctx.getAll("names");

        try {

            for(String name : names) {
                DatabaseManager.tradeForms.delete(player.getUniqueId(), name);
            }

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error: ", this, e);
            src.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
