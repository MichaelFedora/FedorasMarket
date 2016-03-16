package io.github.michaelfedora.fedorasmarket.cmdexecutors.trade;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmHelpExecutor;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmTradeHelpExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = FmHelpExecutor.ALIASES;

    public static final String NAME = ALIASES.get(0);
    public static final String PERM = FmExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .description(Text.of(FmHelpExecutor.DESC))
                .extendedDescription(Text.of(FmHelpExecutor.EX_DESC))
                .permission(PERM)
                .executor(new FmTradeHelpExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmHelpExecutor.helpFunc(src, ctx, FedorasMarket.getGrandChildCommands(FmTradeExecutor.NAME).orElseThrow(makeExceptionSupplier("Can't find subcommands!?")), FmTradeExecutor.NAME);

        return CommandResult.success();
    }
}
