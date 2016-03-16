package io.github.michaelfedora.fedorasmarket.cmdexecutors.quicktrade;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.*;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmQuickTradeExecutor extends FmExecutorBase {

    public static List<String> aliases = Arrays.asList("quicktrade", "qt");

    public static CommandSpec create(Map<List<String>, ? extends CommandCallable> children) {
        return CommandSpec.builder()
                .description(Text.of("Make a trade, quickly!"))
                .permission(PluginInfo.DATA_ROOT + ".quicktrade")
                .children(children)
                .executor(new FmQuickTradeExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "quicktrade";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        FmExecutor.listSubCommandsFunc(src, FedorasMarket.getGrandChildCommands("quicktrade").orElseThrow(makeExceptionSupplier("Can't find subcommands!?")), "quicktrade");

        return CommandResult.success();
    }
}
