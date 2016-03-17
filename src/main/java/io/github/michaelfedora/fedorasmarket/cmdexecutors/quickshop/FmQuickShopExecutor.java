package io.github.michaelfedora.fedorasmarket.cmdexecutors.quickshop;

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
 * Created by Michael on 3/13/2016.
 */
public class FmQuickShopExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("quickshop", "qs");

    public static final String NAME = ALIASES.get(0);
    public static final String PERM = FmQuickShopExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create(HashMap<List<String>,CommandSpec> children) {
        return CommandSpec.builder()
                .description(Text.of("Create a shop quickly! (lists sub commands)"))
                .permission(PERM + ".use")
                .children(children)
                .executor(new FmQuickShopExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmExecutor.listSubCommandsFunc(src, FedorasMarket.getGrandChildCommands(NAME).orElseThrow(makeExceptionSupplier("Can't find subcommands?!")), NAME);

        return CommandResult.success();
    }
}
