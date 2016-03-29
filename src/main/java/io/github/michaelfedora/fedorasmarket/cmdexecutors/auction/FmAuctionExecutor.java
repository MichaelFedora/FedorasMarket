package io.github.michaelfedora.fedorasmarket.cmdexecutors.auction;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform.FmTradeFormExecutor;
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
import java.util.Optional;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmAuctionExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("auction", "auc");

    public static final String NAME = ALIASES.get(0);
    public static final String PERM = FmExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create(Map<List<String>, ? extends CommandCallable> children) {
        return CommandSpec.builder()
                .description(Text.of("Do auction things! (lists subcommands)"))
                .permission(PERM + ".use")
                .children(children)
                .executor(new FmAuctionExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmExecutor.listSubCommandsFunc(src, FedorasMarket.getGrandChildCommands(NAME).orElseThrow(makeExceptionSupplier("Can't find subcommands!?")), NAME);

        return CommandResult.success();
    }
}
