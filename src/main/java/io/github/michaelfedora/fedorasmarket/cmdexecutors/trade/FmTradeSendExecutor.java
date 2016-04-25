package io.github.michaelfedora.fedorasmarket.cmdexecutors.trade;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 3/16/2016.
 */
public class FmTradeSendExecutor extends FmExecutorBase {

    public static List<String> ALIASES = Arrays.asList("send", "s");

    public static final String NAME = FmTradeExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Send a trade to another player"))
                .permission(PERM)
                .arguments(GenericArguments.string(Text.of("tradeform")),
                        GenericArguments.player(Text.of("receiver")))
                .executor(new FmTradeSendExecutor())
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

        Player receiver = ctx.<Player>getOne("receiver").orElseThrow(makeParamExceptionSupplier("receiver"));
        String tradeFormName = ctx.<String>getOne("tradeform").orElseThrow(makeParamExceptionSupplier("tradeform"));

        TradeForm tradeForm;

        try(Connection conn = DatabaseManager.getConnection()) {

            Optional<TradeForm> opt_tf = DatabaseManager.tradeForm.get(conn, playerId.toString(), tradeFormName);

            if(!opt_tf.isPresent())
                throw makeException("Bad tradeform!");

            tradeForm = opt_tf.get();

        } catch(SQLException e) {
            throw makeException("SQLError", e, src);
        }


        FmTradeExecutor.sendTrade(playerId, receiver.getUniqueId(), tradeForm);

        return CommandResult.success();
    }
}
