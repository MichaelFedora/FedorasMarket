package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Michael on 2/25/2016.
 */
public class FmTransactionDetailsExecutor implements CommandExecutor {

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

        try {
            Connection conn = FedorasMarket.getDataSource(FedorasMarket.DB_TRANSACTION_ID).getConnection();

            try {

                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM fm_transactions WHERE id=? AND trans_name=? LIMIT 1");
                preparedStatement.setObject(1, player.getUniqueId());
                preparedStatement.setString(2, trans_name);
                ResultSet resultSet = preparedStatement.executeQuery();

                src.sendMessage(FmUtil.makeMessage("Transaction [" + trans_name + "] details: "));
                if(resultSet.next())
                    src.sendMessage(Text.of(TextColors.GREEN, "[",
                            TextColors.WHITE, trans_name,
                            TextColors.GREEN, "]: ",
                            TextColors.BLUE, (TradeTransaction.Data) resultSet.getObject("data")));

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
