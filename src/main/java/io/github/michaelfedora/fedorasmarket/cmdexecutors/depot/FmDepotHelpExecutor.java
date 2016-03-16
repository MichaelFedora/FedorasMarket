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

    public static final List<String> aliases = FmHelpExecutor.aliases;
    public static final String base = FmDepotExecutor.aliases.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of(FmHelpExecutor.desc))
                .extendedDescription(Text.of(FmHelpExecutor.exDesc))
                .permission(PluginInfo.DATA_ROOT + '.' + base + '.' + aliases.get(0))
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .executor(new FmDepotHelpExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return base + ' ' + aliases.get(0);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmHelpExecutor.helpFunc(src, ctx, FedorasMarket.getGrandChildCommands("depot").orElseThrow(makeExceptionSupplier("Can't find subcommands!?")), "depot");

        return CommandResult.success();
    }
}
