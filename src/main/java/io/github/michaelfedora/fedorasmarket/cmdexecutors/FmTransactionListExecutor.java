package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTransactionListExecutor implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(
                    TextColors.RED, TextStyles.BOLD, "[ERROR]: ",
                    TextStyles.NONE, "Your not a player! No Transactions for you :<"));

            return CommandResult.empty();
        }

        Player player = (Player) src;

        try {
            Connection conn = FedorasMarket.getDataSource(FedorasMarket.DB_TRANSACTION_ID).getConnection();

            try {

                //conn.prepareStatement("CREATE TABLE IF NOT EXISTS fm_transactions(id uuid, trans_name varchar(255), data object)").execute();
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM fm_transactions WHERE id=?");
                preparedStatement.setObject(1, player.getUniqueId());
                ResultSet resultSet = preparedStatement.executeQuery();

                src.sendMessage(FmUtil.makeMessage("All Transactions (for your id);"));

                Text.Builder tb = Text.builder();
                while(resultSet.next()) {
                    tb.append(Text.of(TextColors.BLUE, "[", TextColors.WHITE, resultSet.getString("trans_name"), TextColors.BLUE, "]"));
                    if(!resultSet.isLast()) {
                        tb.append(Text.of(TextColors.GRAY, ", "));
                    }
                }
                src.sendMessage(tb.build());

            } finally {
                conn.close();
            }

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("FmTransactionCreateExecutor", e);
            src.sendMessage(FmUtil.makeMessageError("SQL ERROR: See console :c"));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }
}
