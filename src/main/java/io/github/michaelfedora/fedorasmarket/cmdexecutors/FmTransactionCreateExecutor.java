package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.TradeType;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.transaction.TradeParty;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTransactionCreateExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            src.sendMessage(FmUtil.makeMessageError("Your not a player! No Transactions for you :<"));

            return CommandResult.empty();
        }

        Player player = (Player) src;

        String trans_name;
        {
            Optional<String> opt_trans_name = ctx.<String>getOne("trans_name");
            if (!opt_trans_name.isPresent())
                return CommandResult.empty();
            trans_name = opt_trans_name.get();
        }
        TradeType tradeType;
        {
            Optional<TradeType> opt_tradeType = ctx.<TradeType>getOne("trade_type");
            if(opt_tradeType.isPresent())
                tradeType = opt_tradeType.get();
            else
                tradeType = TradeType.CUSTOM;
        }



        TradeTransaction transaction = new TradeTransaction(tradeType, new TradeParty(), new TradeParty());

        try {
            Connection conn = FedorasMarket.getDataSource(FedorasMarket.DB_TRANSACTION_ID).getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM fm_transactions WHERE id=? AND trans_name=?");
                preparedStatement.setObject(1, player.getUniqueId());
                preparedStatement.setString(2, trans_name);
                preparedStatement.execute();

                preparedStatement = conn.prepareStatement("INSERT INTO fm_transactions(id, trans_name, data) values (?, ?, ?)");
                preparedStatement.setObject(1, player.getUniqueId());
                preparedStatement.setString(2, trans_name);
                preparedStatement.setObject(3, transaction.toData());
                preparedStatement.execute();
            } finally {
                conn.close();
            }

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("FmTransactionCreateExecutor", e);
            src.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
            return CommandResult.empty();
        }

        /*src.sendMessage(FmUtil.makeMessage("Attempted to offer you data c:"));

        Optional<Map<String,TradeTransaction>> opt_map = player.get(FmDataKeys.TRANSACTION_LIST);
        Map<String,TradeTransaction> map;
        if(!opt_map.isPresent())
            map = new HashMap<>();
        else
            map = opt_map.get();

        map.put(name, transaction);

        DataTransactionResult dtr = player.offer(FmDataKeys.TRANSACTION_LIST, map);

        src.sendMessage(FmUtil.makeMessage(dtr.toString()));*/

        return CommandResult.success();
    }
}
