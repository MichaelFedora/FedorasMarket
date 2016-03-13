package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeParty;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTradeFormCreateExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "tradeform create";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw sourceNotPlayerException;
        }

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        TradeType tradeType = ctx.<TradeType>getOne("type").orElse(TradeType.CUSTOM);

        TradeForm tradeForm = new TradeForm(tradeType, new TradeParty(), new TradeParty());

        try(Connection conn = DatabaseManager.getConnection()) {

            DatabaseManager.tradeFormDB.delete(conn, player.getUniqueId(), name);
            DatabaseManager.tradeFormDB.insert(conn, player.getUniqueId(), name, tradeForm.serialize());

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
