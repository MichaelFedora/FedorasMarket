package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.transaction.TradeTransaction;
import io.github.michaelfedora.fedorasmarket.transaction.TransactionParty;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTransactionDeleteExecutor implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            src.sendMessage(Text.of(
                    TextColors.RED, TextStyles.BOLD, "[ERROR]: ",
                    TextStyles.NONE, "Your not a player! No Transactions for you :<"));

            return CommandResult.empty();
        }

        Player player = (Player) src;

        Optional<String> opt_name = ctx.<String>getOne("name");
        if(!opt_name.isPresent())
            return CommandResult.empty();

        String name = opt_name.get();

        Optional<Map<String,TradeTransaction>> opt_map = player.get(FmDataKeys.TRANSACTION_LIST);
        Map<String,TradeTransaction> map;

        if(!opt_map.isPresent()) {

            map = new HashMap<String, TradeTransaction>();

        } else {

            map = opt_map.get();
            map.remove(name);
        }

        player.offer(FmDataKeys.TRANSACTION_LIST, map);

        return CommandResult.success();
    }
}
