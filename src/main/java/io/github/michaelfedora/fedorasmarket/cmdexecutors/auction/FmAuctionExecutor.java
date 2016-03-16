package io.github.michaelfedora.fedorasmarket.cmdexecutors.auction;

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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmAuctionExecutor extends FmExecutorBase {

    public static final List<String> aliases = Arrays.asList("auction", "auc");

    public static CommandSpec create(Map<List<String>, ? extends CommandCallable> children) {
        return CommandSpec.builder()
                .description(Text.of("Do auction things! (lists subcommands"))
                .permission(PluginInfo.DATA_ROOT + "." + aliases.get(0) + ".use")
                .build();
    }

    @Override
    protected String getName() {
        return aliases.get(0);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmExecutor.listSubCommandsFunc(src, FedorasMarket.getGrandChildCommands(aliases.get(0)).orElseThrow(makeExceptionSupplier("Can't find subcommands!?")), aliases.get(0));

        return CommandResult.success();
    }
}
