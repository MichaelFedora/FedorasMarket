package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutor;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.trade.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeParty;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 2/23/2016.
 */
public class FmTradeFormCreateExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("create", "new");

    public static final String NAME = FmTradeFormExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeFormExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Create a trade form"))
                .permission(PERM)
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.optional(GenericArguments.enumValue(Text.of("type"), TradeType.class)))
                .executor(new FmTradeFormCreateExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw makeSourceNotPlayerException();
        }

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        TradeType tradeType = ctx.<TradeType>getOne("type").orElse(TradeType.CUSTOM);

        TradeForm tradeForm = new TradeForm(tradeType, new TradeParty(), new TradeParty());

        try(Connection conn = DatabaseManager.getConnection()) {

            DatabaseManager.delete(conn, player.getUniqueId(), DatabaseCategory.TRADEFORM, name);
            DatabaseManager.insert(conn, player.getUniqueId(), DatabaseCategory.TRADEFORM, name, tradeForm.serialize());

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
