package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Michael on 2/26/2016.
 */
public class FmTradeFormSetTradeTypeExecutor implements CommandExecutor {

    // TODO: Make a better `error` function
    public CommandResult error(CommandSource src) {

        src.sendMessage(FmUtil.makeMessageError("Bad params, try again!"));

        return CommandResult.empty();
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if (!(src instanceof Player)) {
            return FmTradeFormExecutor.errorNotPlayer(src);
        }

        Player player = (Player) src;

        String name;
        {
            Optional<String> opt_name = ctx.<String>getOne("name");
            if (!opt_name.isPresent())
                return error(src);
            name = opt_name.get();
        }

        TradeType tradeType;
        {
            Optional<TradeType> opt_tradeType = ctx.<TradeType>getOne("type");
            if (!opt_tradeType.isPresent())
                return error(src);
            tradeType = opt_tradeType.get();
        }

        try {

            ResultSet resultSet = DatabaseManager.tradeForms.selectWithMore(player.getUniqueId(), name, "LIMIT 1");

            TradeForm tradeForm;
            if(resultSet.next()) {
                tradeForm = ((TradeForm.Data) resultSet.getObject("data")).deserialize();

                tradeForm.setTradeType(tradeType); // TODO: Why is this not going through very well?

                DatabaseManager.tradeForms.update(tradeForm.toData(), player.getUniqueId(), name);
            }


        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error: ", this, e);
            src.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
