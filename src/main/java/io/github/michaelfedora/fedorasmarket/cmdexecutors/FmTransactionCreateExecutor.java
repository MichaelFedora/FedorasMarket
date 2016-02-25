package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.transaction.TransactionParty;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
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

        Optional<String> opt_name = ctx.<String>getOne("name");
        if(!opt_name.isPresent())
            return CommandResult.empty();

        String name = opt_name.get();

        TradeTransaction transaction = new TradeTransaction(new TransactionParty(), new TransactionParty());

        try {
            Connection conn = FedorasMarket.getDataSource("jdbc:h2:transactions.db").getConnection();

            try {
                conn.prepareStatement("IF NOT EXISTS (SELECT name FROM users) CREATE TABLE " + player.getUniqueId().toString());
            } finally {
                conn.close();
            }

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("FmTransactionCreateExecutor", e);
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
