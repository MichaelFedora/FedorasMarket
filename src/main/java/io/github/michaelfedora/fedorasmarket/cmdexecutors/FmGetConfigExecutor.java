package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.config.FmConfig;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 3/29/2016.
 */
public class FmGetConfigExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("getconfig", "getcfg");

    public static final String NAME = ALIASES.get(0);

    @Override
    public String getName() {
        return NAME;
    }

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Get a config option"))
                .extendedDescription(Text.of("Get a config option; valid args are \"maxItemStacks\", \"cleanOnStartup\", and \"validShopBlockTypes\""))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.string(Text.of("configArg")))
                .executor(new FmGetConfigExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        switch(args.<String>getOne("configArg").orElseThrow(makeParamExceptionSupplier("configArg")).toLowerCase()) {
            case "maxitemstacks":
                src.sendMessage(Text.of(TextColors.GOLD, "maxItemStacks", TextColors.RESET, ": ", FmConfig.getMaxItemStacks()));
                break;
            case "cleanonstartup":
                src.sendMessage(Text.of(TextColors.GOLD, "cleanOnStartup", TextColors.RESET, ": ", FmConfig.getCleanOnStartup()));
                break;
            case "validshopblocktypes":
                src.sendMessage(Text.of(TextColors.GOLD, "validShopBlockTypes", TextColors.RESET, ": ", FmConfig.getValidShopBlockTypes()));
                break;
            default:
                throw makeException("Not a valid configArg; do `/help getconfig` for options!");
        }

        return CommandResult.success();
    }
}
