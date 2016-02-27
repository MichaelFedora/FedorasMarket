package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTradeFormExecutor implements CommandExecutor {

    public static CommandResult errorNotPlayer(CommandSource src) {
        src.sendMessage(FmUtil.makeMessageError("tradeform", "You're not a player! No transactions for you :<"));
        return CommandResult.empty();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        Optional<HashMap<List<String>, CommandSpec>> opt_subCommands = FedorasMarket.getGrandChildCommands("trade");
        if(!opt_subCommands.isPresent()) {
            src.sendMessage(FmUtil.makeMessageError("Sub commands failed!?!?"));
            FedorasMarket.getLogger().error("FmTradeFormExecutor, can't find subcommands!?");
            return CommandResult.empty();
        }

        Text.Builder tb = Text.builder();
        int i = 0;
        for(Map.Entry<List<String>, CommandSpec> entry : opt_subCommands.get().entrySet()) {
            tb.append(Text.of(TextColors.BLUE, entry.getKey()));
            if(++i < opt_subCommands.get().entrySet().size()) {
                tb.append(Text.of(TextColors.GRAY, ", "));
            }
        }
        src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GREEN, "[trade]: ", TextStyles.NONE, tb.build()));
        return CommandResult.success();
    }
}
