package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Map;
import java.util.Optional;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTransactionListExecutor implements CommandExecutor {
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
        if(src instanceof Player) {
            src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GREEN, "Transactions: "));
            Optional<Map<String,TradeTransaction>> opt_transactions = ((Player) src).get(FmDataKeys.TRANSACTION_LIST);
            if(opt_transactions.isPresent()) {
                StringBuilder list = new StringBuilder("");
                for(String s : opt_transactions.get().keySet()) {
                    list.append(s + ", ");
                }
                list.delete(list.length() - 2, list.length());

                src.sendMessage(Text.of(list));
            }
        } else {
            src.sendMessage(Text.of(
                    TextColors.RED, TextStyles.BOLD, "[ERROR]",
                    TextStyles.NONE, "Your not a player! No Transactions for you :<"));
        }

        return CommandResult.success();
    }
}
