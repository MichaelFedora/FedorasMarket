package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Michael on 2/25/2016.
 */
public class FmTradeFormSetCurrencyExecutor extends FmExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("setcurrency", "setc");

    public static final String NAME = FmTradeFormExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmTradeFormExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Sets a currency entry in a trade form"))
                .permission(PERM)
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.choices(Text.of("party"), PartyType.choices, true),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.string(Text.of("currency")))
                .executor(new FmTradeFormSetCurrencyExecutor())
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

        BigDecimal amount = BigDecimal.valueOf(ctx.<Double>getOne("amount").orElseThrow(makeParamExceptionSupplier("amount")));

        String currencyName = ctx.<String>getOne("currency").orElseThrow(makeParamExceptionSupplier("currency"));
        Currency currency = FmUtil.getCurrency(currencyName).orElseThrow(makeParamExceptionSupplier("currency"));

        boolean success = false;

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectWithMore(conn, DatabaseQuery.DATA.v, player.getUniqueId(), DatabaseCategory.TRADEFORM, name, "LIMIT 1");

            if(!resultSet.next())
                throw makeException("Couldn't find tradeform!");

            TradeForm tradeForm = ((SerializedTradeForm) resultSet.getObject(DatabaseQuery.DATA.v)).safeDeserialize().orElseThrow(makeExceptionSupplier("Bad tradeform data!"));

            Map<Currency,BigDecimal> currencies;
            switch(partyType) {
                case OWNER:
                    tradeForm.setOwnerParty(tradeForm.getOwnerParty().setCurrency(currency, amount));

                    currencies = tradeForm.getOwnerParty().getCurrencies();
                    if(currencies.containsKey(currency))
                        success = amount.compareTo(currencies.get(currency)) == 0;
                    else
                        success = amount.compareTo(BigDecimal.ZERO) == 0;
                    break;

                case CUSTOMER:
                    tradeForm.setCustomerParty(tradeForm.getCustomerParty().setCurrency(currency, amount));

                    currencies = tradeForm.getCustomerParty().getCurrencies();
                    if(currencies.containsKey(currency))
                        success = amount.compareTo(currencies.get(currency)) == 0;
                    else
                        success = amount.compareTo(BigDecimal.ZERO) == 0;
                    break;
            }

            DatabaseManager.update(conn, tradeForm.serialize(), player.getUniqueId(), DatabaseCategory.TRADEFORM, name);

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        if(success)
            msgf(src, Text.of("Set ", currency.format(amount), " to the transaction [", name, "]!"));
        else
            msgf(src, Text.of("Failed to set ", currency.format(amount), " to the transaction [", name, "]!"));

        return CommandResult.success();
    }
}
