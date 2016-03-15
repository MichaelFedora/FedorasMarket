package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop.quickcreate;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.trade.TradeType;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeParty;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Michael on 3/13/2016.
 */
public class FmShopQuickCreateCurrencyTradeExecutor extends FmShopQuickCreateBase {

    public static final List<String> aliases = Arrays.asList("currencytrade", "currt");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Create an CurrencyTrade shop"))
                .permission(PluginInfo.DATA_ROOT + ".shop.quickcreate.currencytrade")
                .arguments(
                        GenericArguments.doubleNum(Text.of("currency_amt")),
                        GenericArguments.string(Text.of("currency")),
                        GenericArguments.doubleNum(Text.of("currency_amt2")),
                        GenericArguments.string(Text.of("currency2")),
                        GenericArguments.flags()
                                .flag("s", "-server")
                                .buildWith(GenericArguments.none())
                )
                .executor(new FmShopQuickCreateCurrencyTradeExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "shop quickcreate currencytrade";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        double currencyAmount = ctx.<Double>getOne("currency_amt").orElseThrow(makeParamExceptionSupplier("currency_amt"));
        Currency currency = ctx.<Currency>getOne("currency").orElseThrow(makeParamExceptionSupplier("currency"));

        TradeParty owner = new TradeParty();
        owner.addCurrency(currency,  BigDecimal.valueOf(currencyAmount));

        double currencyAmount2 = ctx.<Double>getOne("currency_amt2").orElseThrow(makeParamExceptionSupplier("currency_amt2"));
        Currency currency2 = ctx.<Currency>getOne("currency2").orElseThrow(makeParamExceptionSupplier("currency2"));

        TradeParty customer = new TradeParty();
        customer.addCurrency(currency2, BigDecimal.valueOf(currencyAmount2));

        TradeForm tf = new TradeForm(TradeType.CURRENCY_TRADE, owner, customer);

        if(ctx.<Boolean>getOne("s").orElse(false) && src.hasPermission(PluginInfo.DATA_ROOT + ".shop.server"))
            as_server.add(playerId);

        to_apply.put(playerId, new Tuple<>("CurrencyTrade", tf));
        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
