package io.github.michaelfedora.fedorasmarket.cmdexecutors;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.config.FmConfig;
import org.spongepowered.api.block.BlockType;
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
import java.util.Optional;

/**
 * Created by Michael on 3/29/2016.
 */
public class FmSetConfigExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("setconfig", "setcfg");

    public static final String NAME = ALIASES.get(0);

    @Override
    public String getName() {
        return NAME;
    }

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Sets the config (by flags)"))
                .permission(PluginInfo.DATA_ROOT + '.' + NAME)
                .arguments(GenericArguments.flags()
                        .valueFlag(GenericArguments.integer(Text.of("maxItemStacks")), "-mis","-maxItemStacks")
                        .valueFlag(GenericArguments.bool(Text.of("cleanOnStartup")), "-cos","-cleanOnStartup")
                        .valueFlag(GenericArguments.catalogedElement(Text.of("addBlockType"), BlockType.class), "-abt", "-addBlockType")
                        .valueFlag(GenericArguments.catalogedElement(Text.of("remBlockType"), BlockType.class), "-rbt", "-remBlockType")
                        .buildWith(GenericArguments.none()))
                .executor(new FmSetConfigExecutor())
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        Optional<Integer> opt_maxItemStacks = args.getOne("maxItemStacks");
        if(opt_maxItemStacks.isPresent()) {
            FmConfig.setMaxItemStacks(opt_maxItemStacks.get());
            src.sendMessage(Text.of("Set ", TextColors.GOLD, "maxItemStacks", TextColors.RESET, " to ", opt_maxItemStacks.get()));
        }

        Optional<Boolean> opt_cleanOnStartup = args.getOne("cleanOnStartup");
        if(opt_cleanOnStartup.isPresent()) {
            FmConfig.setCleanOnStartup(opt_cleanOnStartup.get());
            src.sendMessage(Text.of("Set ", TextColors.GOLD, "cleanOnStartup", TextColors.RESET, " to ", opt_cleanOnStartup.get()));
        }

        Optional<BlockType> opt_addBlockType = args.getOne("addBlockType");
        if(opt_addBlockType.isPresent()) {
            FmConfig.addValidShopBlockType(opt_addBlockType.get());
            src.sendMessage(Text.of(TextColors.AQUA, "Added ", TextColors.RESET, "a ", TextColors.GOLD, "Valid Shop Block Type", TextColors.RESET, ": ", opt_addBlockType.get()));
        }

        Optional<BlockType> opt_remBlockType = args.getOne("remBlockType");
        if(opt_remBlockType.isPresent()) {
            FmConfig.removeValidShopBlockType(opt_remBlockType.get());
            src.sendMessage(Text.of(TextColors.AQUA, "Removed ", TextColors.RESET, "a ", TextColors.GOLD, "Valid Shop Block Type", TextColors.RESET, ": ", opt_remBlockType.get()));
        }

        FmConfig.save();
        src.sendMessage(Text.of("Saved configuration settings to the file!"));

        return CommandResult.success();
    }
}
