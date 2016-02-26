package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.PartyType;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
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
public class FmTransactionRemoveItemExecutor implements CommandExecutor {
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
                return CommandResult.empty(); // TODO: Call error() function, then return
            trans_name = opt_trans_name.get();
        }

        PartyType partyType;
        {
            Optional<PartyType> opt_partyType = ctx.<PartyType>getOne("party");
            if (!opt_partyType.isPresent())
                return CommandResult.empty();
            partyType = opt_partyType.get();
        }

        ItemType itemType;
        {
            Optional<ItemType> opt_itemType = ctx.<ItemType>getOne("item");
            if (!opt_itemType.isPresent())
                return CommandResult.empty();
            itemType = opt_itemType.get();
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
                    tradeTransaction = (TradeTransaction) resultSet.getObject("data");

                    switch(partyType) {
                        case OWNER:
                            tradeTransaction.setOwnerParty(tradeTransaction.getOwnerParty().removeItem(itemType));
                            break;
                        case CUSTOMER:
                            tradeTransaction.setCustomerParty(tradeTransaction.getCustomerParty().removeItem(itemType));
                            break;
                    }

                    preparedStatement = conn.prepareStatement("UPDATE fm_transactions SET data=? WHERE id=? AND trans_name=?");
                    preparedStatement.setObject(2, player.getUniqueId());
                    preparedStatement.setString(3, trans_name);
                    preparedStatement.setObject(1, tradeTransaction);
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
