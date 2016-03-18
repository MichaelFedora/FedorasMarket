package io.github.michaelfedora.fedorasmarket.cmdexecutors.trade;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 3/17/2016.
 */
public class FmTradeNegotiateExecutor extends FmExecutorBase {

    public static List<String> ALIASES = Arrays.asList("send", "s");

    public static final String NAME = FmTradeExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Copy the tradeform to edit and resend"))
                .permission(PERM)
                .arguments(GenericArguments.player(Text.of("sender")),
                        GenericArguments.integer(Text.of("num")))
                .executor(new FmTradeNegotiateExecutor())
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

        Player sender = ctx.<Player>getOne("sender").orElseThrow(makeParamExceptionSupplier("sender"));
        int num = ctx.<Integer>getOne("num").orElseThrow(makeParamExceptionSupplier("num"));

        //nope

        return CommandResult.success();
    }
}
