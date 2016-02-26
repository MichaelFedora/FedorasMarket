package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.PartyType;
import io.github.michaelfedora.fedorasmarket.data.TradeType;
import io.github.michaelfedora.fedorasmarket.transaction.TradeParty;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Michael on 2/26/2016.
 */
public class FmTransactionSetTradeTypeExecutor implements CommandExecutor {

    // TODO: Make a better `error` function
    public CommandResult error(CommandSource src) {

        src.sendMessage(FmUtil.makeMessageError("Bad params, try again!"));

        return CommandResult.empty();
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if (!(src instanceof Player)) {
            src.sendMessage(FmUtil.makeMessageError("Your not a player! No Transactions for you :<"));

            return CommandResult.empty();
        }

        Player player = (Player) src;

        String trans_name;
        {
            Optional<String> opt_trans_name = ctx.<String>getOne("trans_name");
            if (!opt_trans_name.isPresent())
                return error(src);
            trans_name = opt_trans_name.get();
        }

        TradeType tradeType;
        {
            Optional<TradeType> opt_tradeType = ctx.<TradeType>getOne("type");
            if (!opt_tradeType.isPresent())
                return error(src);
            tradeType = opt_tradeType.get();
        }

        try {
            Connection conn = FedorasMarket.getDataSource(FedorasMarket.DB_TRANSACTION_ID).getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM fm_transactions WHERE id=? AND trans_name=? LIMIT 1");
                preparedStatement.setObject(1, player.getUniqueId());
                preparedStatement.setString(2, trans_name);
                ResultSet resultSet = preparedStatement.executeQuery();

                TradeTransaction tradeTransaction;
                if(resultSet.next()) {
                    tradeTransaction = ((TradeTransaction.Data) resultSet.getObject("data")).deserialize();

                    tradeTransaction.setTradeType(tradeType); // TODO: Why is this not going through very well?

                    preparedStatement = conn.prepareStatement("UPDATE fm_transactions SET data=? WHERE id=? AND trans_name=?");
                    preparedStatement.setObject(2, player.getUniqueId());
                    preparedStatement.setString(3, trans_name);
                    preparedStatement.setObject(1, tradeTransaction.toData());
                    preparedStatement.execute();
                }

            } finally {
                conn.close();
            }

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("FmTransactionDetailsExecutor", e);
            src.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
