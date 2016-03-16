package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

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

import java.util.*;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTradeFormExecutor extends FmExecutorBase {

    public static final List<String> aliases = Arrays.asList("tradeform", "tf");

    public static CommandSpec create(HashMap<List<String>, CommandSpec> children) {
        return CommandSpec.builder()
                .description(Text.of("Do tradeform things (lists sub commands)"))
                .permission(PluginInfo.DATA_ROOT + "." + aliases.get(0) + ".use")
                .executor(new FmTradeFormExecutor())
                .children(children)
                .build();
    }

    @Override
    protected String getName() {
        return aliases.get(0);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmExecutor.listSubCommandsFunc(src, FedorasMarket.getGrandChildCommands(aliases.get(0)).orElseThrow(makeExceptionSupplier("Can't find subcommands?!")), aliases.get(0));

        return CommandResult.success();
    }
}
