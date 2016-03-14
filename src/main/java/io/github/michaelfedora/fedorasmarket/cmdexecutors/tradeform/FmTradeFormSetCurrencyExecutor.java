package io.github.michaelfedora.fedorasmarket.cmdexecutors.tradeform;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.enumtype.PartyType;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Michael on 2/25/2016.
 */
public class FmTradeFormSetCurrencyExecutor extends FmExecutorBase {

    @Override
    protected String getName() {
        return "tradeform setcurrency";
    }

    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw sourceNotPlayerException;

        Player player = (Player) src;

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        PartyType partyType = ctx.<PartyType>getOne("party").orElseThrow(makeParamExceptionSupplier("party"));

        BigDecimal amount = BigDecimal.valueOf(ctx.<Double>getOne("amount").orElseThrow(makeParamExceptionSupplier("amount")));

        Currency currency = ctx.<Currency>getOne("currency").orElse(FedorasMarket.getEconomyService().getDefaultCurrency());

        boolean success = false;

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.tradeFormDB.selectWithMore(conn, player.getUniqueId(), name, "LIMIT 1");

            TradeForm tradeForm;
            if(resultSet.next()) {
                tradeForm = ((SerializedTradeForm) resultSet.getObject("data")).safeDeserialize().get();

                switch(partyType) {
                    case OWNER:
                        tradeForm.setOwnerParty(tradeForm.getOwnerParty().setCurrency(currency, amount));
                        success = tradeForm.getOwnerParty().currencies.containsKey(currency);
                        if(success)
                            success = (amount.compareTo(tradeForm.getOwnerParty().currencies.get(currency)) == 0);
                        break;
                    case CUSTOMER:
                        tradeForm.setCustomerParty(tradeForm.getCustomerParty().setCurrency(currency, amount));
                        success = tradeForm.getCustomerParty().currencies.containsKey(currency);
                        if(success)
                            success = (amount.compareTo(tradeForm.getOwnerParty().currencies.get(currency)) == 0);
                        break;
                }

                DatabaseManager.tradeFormDB.update(conn, tradeForm.serialize(), player.getUniqueId(), name);
            }

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
