package io.github.michaelfedora.fedorasmarket.cmdexecutors.trade;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmTradeExecutor extends FmExecutorBase {

    public static List<String> aliases = Arrays.asList("trade", "tr");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Do trade things! (lists subcommands)"))
                .permission(PluginInfo.DATA_ROOT + ".trade")
                .executor(new FmTradeExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "trade";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmExecutor.listSubCommandsFunc(src, FedorasMarket.getGrandChildCommands("trade").orElseThrow(makeExceptionSupplier("Couldn't find subcommands!?")), "trade");

        return CommandResult.success();
    }
}
