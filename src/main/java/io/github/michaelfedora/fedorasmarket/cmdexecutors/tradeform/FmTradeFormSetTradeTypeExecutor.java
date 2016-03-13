package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Michael on 2/26/2016.
 */
public class FmTradeFormSetTradeTypeExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "tradeform settradetype";
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if (!(src instanceof Player)) {
            throw sourceNotPlayerException;
        }

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        TradeType tradeType = ctx.<TradeType>getOne("type").orElseThrow(makeParamExceptionSupplier("type"));

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.tradeFormDB.selectWithMore(conn, player.getUniqueId(), name, "LIMIT 1");

            TradeForm tradeForm;
            if(resultSet.next()) {
                tradeForm = ((SerializedTradeForm) resultSet.getObject("data")).safeDeserialize().get();

                tradeForm.setTradeType(tradeType); // TODO: Why is this not going through very well?

                DatabaseManager.tradeFormDB.update(conn, tradeForm.serialize(), player.getUniqueId(), name);
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
