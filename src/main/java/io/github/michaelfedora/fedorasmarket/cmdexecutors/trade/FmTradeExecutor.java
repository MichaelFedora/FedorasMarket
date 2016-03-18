package io.github.michaelfedora.fedorasmarket.cmdexecutors.trade;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.trade.TradeData;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.*;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmTradeExecutor extends FmExecutorBase {

    private static HashMap<UUID, LinkedHashSet<TradeData>> activeTrades = new HashMap<>();

    public static final List<String> ALIASES = Arrays.asList("trade", "tr");

    public static final String NAME = ALIASES.get(0);
    public static final String PERM = FmExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create(Map<List<String>, ? extends CommandCallable> children) {
        return CommandSpec.builder()
                .description(Text.of("Do trade things! (lists subcommands)"))
                .permission(PERM + ".use")
                .children(children)
                .executor(new FmTradeExecutor())
                .build();
    }

    private static LinkedHashSet<TradeData> getOrCreateActiveTrade(UUID key) {

        if(activeTrades.containsKey(key))
            return activeTrades.get(key);
        else
            return new LinkedHashSet<>();
    }

    public static void sendTrade(UUID senderId, UUID receiverId, TradeForm tf) {

        LinkedHashSet<TradeData> senderSet = getOrCreateActiveTrade(senderId);
        LinkedHashSet<TradeData> receiverSet = getOrCreateActiveTrade(receiverId);

        senderSet.add(new TradeData(receiverId, tf, true));
        receiverSet.add(new TradeData(senderId, tf, false));

        activeTrades.put(senderId, senderSet);
        activeTrades.put(receiverId, receiverSet);
    }

    public static boolean removeTrade(UUID id, int num) {

        LinkedHashSet<TradeData> thisSet = getOrCreateActiveTrade(id);

        Optional<TradeData> opt_data = Optional.empty();

        int i = 0;
        for(TradeData td : thisSet) {
            if(++i == num) {
                opt_data = Optional.of(td);
                thisSet.remove(td);
                break;
            }
        }

        if(!opt_data.isPresent())
            return false;

        TradeData data = opt_data.get();

        LinkedHashSet<TradeData> otherSet = getOrCreateActiveTrade(data.other);
        for(TradeData td : otherSet) {
            if(td.other == id && td.tradeForm == data.tradeForm) {
                otherSet.remove(td);
                return true;
            }
        }

        return false;
    }

    public static LinkedHashSet<TradeData> getSentTrades(UUID uuid) {

        LinkedHashSet<TradeData> set = new LinkedHashSet<>();

        if(!activeTrades.containsKey(uuid))
            return set;

        activeTrades.get(uuid).forEach((v) -> {
            if(v.amSender)
                set.add(v);
        });

        return set;
    }

    public static LinkedHashSet<TradeData> getReceivedTrades(UUID uuid) {

        LinkedHashSet<TradeData> set = new LinkedHashSet<>();

        if(!activeTrades.containsKey(uuid))
            return set;

        activeTrades.get(uuid).forEach((v) -> {
            if(!v.amSender)
                set.add(v);
        });

        return set;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        FmExecutor.listSubCommandsFunc(src, FedorasMarket.getGrandChildCommands(NAME).orElseThrow(makeExceptionSupplier("Couldn't find subcommands!?")), NAME);

        return CommandResult.success();
    }
}
