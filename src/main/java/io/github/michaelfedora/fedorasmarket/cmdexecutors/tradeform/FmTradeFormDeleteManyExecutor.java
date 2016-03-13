package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Michael on 2/26/2016.
 */
public class FmTradeFormDeleteManyExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "tradeform deletemany";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw sourceNotPlayerException;
        }

        Player player = (Player) src;

        Collection<String> names = ctx.getAll("names");

        try(Connection conn = DatabaseManager.getConnection()) {

            for(String name : names) {
                DatabaseManager.tradeFormDB.delete(conn, player.getUniqueId(), name);
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
