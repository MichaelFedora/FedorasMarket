package io.github.michaelfedora.fedorasmarket.cmdexecutors.quickshop;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.trade.TradeType;
import io.github.michaelfedora.fedorasmarket.listeners.PlayerInteractListener;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeParty;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
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
public class FmQuickShopItemSellExecutor extends FmQuickShopExecutorBase {

    public static final List<String> ALIASES = Arrays.asList("itemsell", "isell");

    public static final String NAME = FmQuickShopExecutor.NAME + ' ' + ALIASES.get(0);
    public static final String PERM = FmQuickShopExecutor.PERM + '.' + ALIASES.get(0);

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Create an ItemSell shop"))
                .permission(PERM)
                .arguments(
                        GenericArguments.doubleNum(Text.of("currency_amt")),
                        GenericArguments.string(Text.of("currency")),
                        GenericArguments.integer(Text.of("item_amt")),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.flags()
                                .flag("s", "-server")
                                .buildWith(GenericArguments.none())
                )
                .executor(new FmQuickShopItemSellExecutor())
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

        double currencyAmount = ctx.<Double>getOne("currency_amt").orElseThrow(makeParamExceptionSupplier("currency_amt"));
        String currencyName = ctx.<String>getOne("currency").orElseThrow(makeParamExceptionSupplier("currency"));
        Currency currency = FmUtil.getCurrency(currencyName).orElseThrow(makeParamExceptionSupplier("currency"));

        TradeParty owner = new TradeParty();
        owner.addCurrency(currency, BigDecimal.valueOf(currencyAmount));

        int itemAmount = ctx.<Integer>getOne("item_amt").orElseThrow(makeParamExceptionSupplier("item_amt"));
        ItemType itemType = ctx.<ItemType>getOne("item").orElseThrow(makeParamExceptionSupplier("item"));

        TradeParty customer = new TradeParty();
        customer.addItem(itemType, itemAmount);

        TradeForm tf = new TradeForm(TradeType.ITEM_SELL, owner, customer);

        boolean asServer = (ctx.<Boolean>getOne("s").orElse(false) && src.hasPermission(PluginInfo.DATA_ROOT + ".shop.server"));

        to_apply.put(playerId, new Tuple<>(tf, asServer));
        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
