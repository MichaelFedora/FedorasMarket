package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeParty;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTradeFormCreateExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            return FmTradeFormExecutor.errorNotPlayer(src);
        }

        Player player = (Player) src;

        String name;
        {
            Optional<String> opt_name = ctx.<String>getOne("name");
            if (!opt_name.isPresent())
                return CommandResult.empty();
            name = opt_name.get();
        }
        TradeType tradeType;
        {
            Optional<TradeType> opt_tradeType = ctx.<TradeType>getOne("trade_type");
            if(opt_tradeType.isPresent())
                tradeType = opt_tradeType.get();
            else
                tradeType = TradeType.CUSTOM;
        }



        TradeForm tradeForm = new TradeForm(tradeType, new TradeParty(), new TradeParty());

        try {

            DatabaseManager.tradeForms.delete(player.getUniqueId(), name);
            DatabaseManager.tradeForms.insert(player.getUniqueId(), name, tradeForm.toData());

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error: ", this, e);
            src.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
