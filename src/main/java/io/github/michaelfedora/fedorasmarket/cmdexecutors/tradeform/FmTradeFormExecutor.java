package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
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
public class FmTradeFormExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "tradeform";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        HashMap<List<String>, CommandSpec> subCommands = FedorasMarket.getGrandChildCommands("tradeform").orElseThrow(makeExceptionSupplier("Can't find subcommands?!"));

        Text.Builder tb = Text.builder();
        int i = 0;
        for(Map.Entry<List<String>, CommandSpec> entry : subCommands.entrySet()) {
            tb.append(Text.of(TextColors.BLUE, entry.getKey()));
            if(++i < subCommands.entrySet().size()) {
                tb.append(Text.of(TextColors.GRAY, ", "));
            }
        }
        src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GREEN, "[tradeform]: ", TextStyles.RESET, tb.build()));
        return CommandResult.success();
    }
}
