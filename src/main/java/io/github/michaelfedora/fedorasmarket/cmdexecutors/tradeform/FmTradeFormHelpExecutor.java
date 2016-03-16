package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmHelpExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.trade.FmTradeExecutor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * Created by Michael on 2/26/2016.
 */
public class FmTradeFormHelpExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = FmHelpExecutor.ALIASES;

    public static final String NAME = FmTradeFormExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeFormExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .description(Text.of(FmHelpExecutor.DESC))
                .extendedDescription(Text.of(FmHelpExecutor.EX_DESC))
                .permission(PERM)
                .executor(new FmTradeFormHelpExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmHelpExecutor.helpFunc(src, ctx, FedorasMarket.getGrandChildCommands(FmTradeFormExecutor.NAME).orElseThrow(makeExceptionSupplier("Can't find the subcommands :o")), FmTradeFormExecutor.NAME);

        return CommandResult.success();
    }
}
