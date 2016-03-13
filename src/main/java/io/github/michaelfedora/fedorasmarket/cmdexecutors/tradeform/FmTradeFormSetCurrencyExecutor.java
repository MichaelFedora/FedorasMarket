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

        if(!(src instanceof Player)) {
            throw sourceNotPlayerException;
        }

        Player player = (Player) src;

        String name;
        {
            Optional<String> opt_name = ctx.<String>getOne("name");
            if (!opt_name.isPresent())
                throw makeException("bad param");
            name = opt_name.get();
        }

        PartyType partyType;
        {
            Optional<PartyType> opt_partyType = ctx.<PartyType>getOne("party");
            if (!opt_partyType.isPresent())
                throw makeException("bad param");
            partyType = opt_partyType.get();
        }

        double amount;
        {
            Optional<Double> opt_amount = ctx.<Double>getOne("amount");
            if (!opt_amount.isPresent())
                throw makeException("bad param");
            amount = opt_amount.get();
        }

        Currency currency = ctx.<Currency>getOne("currency").orElse(FedorasMarket.getEconomyService().getDefaultCurrency());

        boolean success = false;

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.tradeFormDB.selectWithMore(conn, player.getUniqueId(), name, "LIMIT 1");

            TradeForm tradeForm;
            if(resultSet.next()) {
                tradeForm = ((SerializedTradeForm) resultSet.getObject("data")).safeDeserialize().get();

                switch(partyType) {
                    case OWNER:
                        tradeForm.setOwnerParty(tradeForm.getOwnerParty().setCurrency(currency, BigDecimal.valueOf(amount)));
                        success = tradeForm.getOwnerParty().currencies.containsKey(currency);
                        break;
                    case CUSTOMER:
                        tradeForm.setCustomerParty(tradeForm.getCustomerParty().setCurrency(currency, BigDecimal.valueOf(amount)));
                        success = tradeForm.getCustomerParty().currencies.containsKey(currency);
                        break;
                }

                DatabaseManager.tradeFormDB.update(conn, tradeForm.serialize(), player.getUniqueId(), name);
            }

        } catch(SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        if(success)
            msgf(src, Text.of("Set ", currency.format(BigDecimal.valueOf(amount)), " to the transaction [", name, "]!"));
        else
            msgf(src, Text.of("Failed to set ", currency.format(BigDecimal.valueOf(amount)), " to the transaction [", name, "]!"));

        return CommandResult.success();
    }
}
