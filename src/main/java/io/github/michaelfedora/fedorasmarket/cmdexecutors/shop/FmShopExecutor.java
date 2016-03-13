package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael on 2/29/2016.
 */
public class FmShopExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "shop";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
        HashMap<List<String>, CommandSpec> subCommands = FedorasMarket.getGrandChildCommands("shop").orElseThrow(makeExceptionSupplier("Can't find subcommands?!"));

        Text.Builder tb = Text.builder();
        int i = 0;
        for(Map.Entry<List<String>, CommandSpec> entry : subCommands.entrySet()) {
            tb.append(Text.of(TextColors.BLUE, entry.getKey()));
            if(++i < subCommands.entrySet().size()) {
                tb.append(Text.of(TextColors.GRAY, ", "));
            }
        }
        src.sendMessage(Text.of(TextStyles.BOLD, TextColors.GREEN, "[shop]: ", TextStyles.RESET, tb.build()));
        return CommandResult.success();
    }
}
