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
import java.util.List;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmTradeFormRenameExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("rename", "ren");

    public static final String NAME = FmTradeFormExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeFormExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Rename a tradeform"))
                .permission(PERM)
                .arguments(GenericArguments.string(Text.of("oldName")),
                        GenericArguments.string(Text.of("newName")))
                .executor(new FmTradeFormRenameExecutor())
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
