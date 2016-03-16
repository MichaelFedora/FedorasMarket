package io.github.michaelfedora.fedorasmarket.cmdexecutors.depot;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
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
public class FmDepotHelpExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = FmHelpExecutor.ALIASES;

    public static final String NAME = FmDepotExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmDepotExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of(FmHelpExecutor.DESC))
                .extendedDescription(Text.of(FmHelpExecutor.EX_DESC))
                .permission(PERM)
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .executor(new FmDepotHelpExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmHelpExecutor.helpFunc(src, ctx, FedorasMarket.getGrandChildCommands(FmDepotExecutor.NAME).orElseThrow(makeExceptionSupplier("Can't find subcommands!?")), FmDepotExecutor.NAME);

        return CommandResult.success();
    }
}
