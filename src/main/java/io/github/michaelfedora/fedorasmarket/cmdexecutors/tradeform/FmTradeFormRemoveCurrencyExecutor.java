package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.trade.PartyType;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michael on 2/25/2016.
 */
public class FmTradeFormRemoveCurrencyExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("removecurrency", "remc");

    public static final String NAME = FmTradeFormExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeFormExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Remove a currency from a trade form"))
                .permission(PERM)
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.choices(Text.of("party"), PartyType.choices, true),
                        GenericArguments.string(Text.of("currency")))
                .executor(new FmTradeFormRemoveCurrencyExecutor())
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        PartyType partyType = ctx.<PartyType>getOne("party").orElseThrow(makeParamExceptionSupplier("party"));

        String currencyName = ctx.<String>getOne("currency").orElseThrow(makeParamExceptionSupplier("currency"));
        Currency currency = FmUtil.getCurrency(currencyName).orElseThrow(makeParamExceptionSupplier("currency"));

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectWithMore(conn, DatabaseQuery.DATA.v, player.getUniqueId(), DatabaseCategory.TRADEFORM, name, "LIMIT 1");

            TradeForm tradeForm;
            if(resultSet.next()) {
                tradeForm = ((SerializedTradeForm) resultSet.getObject(DatabaseQuery.DATA.v)).safeDeserialize().get();

                switch(partyType) {
                    case OWNER:
                        tradeForm.setOwnerParty(tradeForm.getOwnerParty().removeCurrency(currency));
                        break;
                    case CUSTOMER:
                        tradeForm.setCustomerParty(tradeForm.getCustomerParty().removeCurrency(currency));
                        break;
                }

                DatabaseManager.update(conn, tradeForm.serialize(), player.getUniqueId(), DatabaseCategory.TRADEFORM, name);
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
