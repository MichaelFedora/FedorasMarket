package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmTradeFormCopyExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("copy", "cp");

    public static final String NAME = FmTradeFormExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeFormExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Either clone one of your own or copy another shop's tradeform"))
                .extendedDescription(Text.of("Two things can be done with this command: The first is copying a " +
                        "tradeform from another shop, by not specifying anything (just the command) and then " +
                        "interacting with a shop; and the second is copying one of your own tradeforms, with an " +
                        "optional new name (if not specified, it will append \"(copy)\" to it"))
                .permission(PERM)
                .arguments(GenericArguments.optional(
                        GenericArguments.seq(
                                GenericArguments.string(Text.of("name")),
                                GenericArguments.optional(GenericArguments.string(Text.of("new_name")))
                        )
                ))
                .executor(new FmTradeFormCopyExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        error(src, "Not implemented yet! Sorry :c"); // TODO: Implement

        return CommandResult.success();
    }
}
