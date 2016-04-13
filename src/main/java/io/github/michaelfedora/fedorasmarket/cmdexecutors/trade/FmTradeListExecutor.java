package io.github.michaelfedora.fedorasmarket.cmdexecutors.trade;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.trade.TradeData;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.*;

/**
 * Created by Michael on 3/18/2016.
 */
public class FmTradeListExecutor extends FmExecutorBase {

    public static List<String> ALIASES = Arrays.asList("list", "l");

    public static final String NAME = FmTradeExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeExecutor.PERM + '.' + ALIASES.get(0);

    enum Selection {
        SENT,
        RECEIVED,
        ALL;

        public static final Map<String, Selection> choices = new HashMap<>();
        static {
            choices.put("sent", SENT);
            choices.put("recieved", RECEIVED);
            choices.put("all", ALL);
        }
    }

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Lists trade requests sent to you"))
                .permission(PERM)
                .arguments(GenericArguments.optional(GenericArguments.choices(Text.of("filter"), Selection.choices, true)))
                .executor(new FmTradeListExecutor())
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

        Set<TradeData> trades = FmTradeExecutor.getReceivedTrades(playerId);

        for(TradeData td : trades) {

        }

        return CommandResult.success();
    }
}
