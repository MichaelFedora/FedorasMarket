package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
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
import org.spongepowered.api.util.Tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Michael on 2/29/2016.
 */
public class FmShopSetTradeFormExecutor extends FmShopExecutorBase {

    public static List<String> aliases = Arrays.asList("settradeform", "settf");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Set a shop's tradeform"))
                .permission(PluginInfo.DATA_ROOT + ".shop.settradeform")
                .arguments(
                        GenericArguments.string(Text.of("tradeform")),
                       FmExecutorBase.makeServerFlag())
                .executor(new FmShopSetTradeFormExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "shop settradeform";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        String tradeFormName = ctx.<String>getOne("tradeform").orElseThrow(makeParamExceptionSupplier("tradeform"));

        TradeForm tradeForm;

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.select(conn, 1, playerId, DatabaseCategory.TRADEFORM, tradeFormName);

            if(!resultSet.next())
                throw makeException("Bad tradeform name!", src);

            tradeForm = ((SerializedTradeForm) resultSet.getObject(DatabaseQuery.DATA.v)).safeDeserialize().orElseThrow(makeExceptionSupplier("Tradeform couldn't deserialize! ",src));

        } catch (SQLException e) {
            throw makeException("SQLException", e, src);
        }

        to_apply.put(playerId, new Tuple<>(tradeForm, false));
        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select the shop!");

        return CommandResult.success();
    }
}
