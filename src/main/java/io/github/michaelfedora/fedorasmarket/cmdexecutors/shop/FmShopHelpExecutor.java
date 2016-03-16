package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

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
 * Created by Michael on 2/29/2016.
 */
public class FmShopHelpExecutor extends FmExecutorBase {

    public static final List<String> aliases = FmHelpExecutor.aliases;
    public static final String base = "shop";

    public static CommandSpec create() {
        return CommandSpec.builder()
                .arguments(GenericArguments.optional(GenericArguments.string(Text.of("cmd"))))
                .description(Text.of(FmHelpExecutor.desc))
                .extendedDescription(Text.of(FmHelpExecutor.exDesc))
                .permission(PluginInfo.DATA_ROOT + "shop.help")
                .executor(new FmShopHelpExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "shop help";
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmHelpExecutor.helpFunc(src, ctx, FedorasMarket.getGrandChildCommands("shop").orElseThrow(makeExceptionSupplier("Can't find the subcommands :o")), "shop");

        return CommandResult.success();
    }
}
