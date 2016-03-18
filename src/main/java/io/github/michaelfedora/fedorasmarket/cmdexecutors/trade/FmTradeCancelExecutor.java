package io.github.michaelfedora.fedorasmarket.cmdexecutors.trade;

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
import java.util.UUID;

/**
 * Created by Michael on 3/17/2016.
 */
public class FmTradeCancelExecutor extends FmExecutorBase {

    public static List<String> ALIASES = Arrays.asList("send", "s");

    public static final String NAME = FmTradeExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Cancel a trade you've sent"))
                .permission(PERM)
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("num"))))
                .executor(new FmTradeCancelExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        int num = ctx.<Integer>getOne("num").orElse(1);

        if(num < 1)
            throw makeException("The selection is invalid, please use '/fm trade list'!");

        if(!FmTradeExecutor.removeTrade(playerId, num))
            throw makeException("Could not delete shop!");

        msg(src, "Removed the trade!");

        return CommandResult.success();
    }
}
