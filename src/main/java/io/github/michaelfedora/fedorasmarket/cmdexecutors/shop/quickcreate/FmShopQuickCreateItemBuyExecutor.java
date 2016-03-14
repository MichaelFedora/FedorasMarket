package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop.quickcreate;

import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
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
import org.spongepowered.api.event.block.InteractBlockEvent;
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
public class FmShopQuickCreateItemBuyExecutor extends FmShopQuickCreateBase {

    public static final List<String> aliases = Arrays.asList("itembuy", "ibuy");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Create an ItemBuy shop"))
                .permission(PluginInfo.DATA_ROOT + ".shop.quickcreate.itembuy")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.integer(Text.of("item_amt")),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.doubleNum(Text.of("currency_amt")),
                        GenericArguments.string(Text.of("currency")),
                        GenericArguments.flags()
                                .flag("s", "-server")
                                .buildWith(GenericArguments.none())
                )
                .executor(new FmShopQuickCreateItemBuyExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "shop quickcreate itembuy";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw sourceNotPlayerException;

        UUID playerId = ((Player) src).getUniqueId();

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        int itemAmount = ctx.<Integer>getOne("item_amt").orElseThrow(makeParamExceptionSupplier("item_amt"));
        ItemType itemType = ctx.<ItemType>getOne("item").orElseThrow(makeParamExceptionSupplier("item"));

        TradeParty owner = new TradeParty();
        owner.addItem(itemType, itemAmount);

        double currencyAmount = ctx.<Double>getOne("currency_amt").orElseThrow(makeParamExceptionSupplier("currency_amt"));
        Currency currency = ctx.<Currency>getOne("currency").orElseThrow(makeParamExceptionSupplier("currency"));

        TradeParty customer = new TradeParty();
        customer.addCurrency(currency, BigDecimal.valueOf(currencyAmount));

        TradeForm tf = new TradeForm(TradeType.ITEM_BUY, owner, customer);

        if(ctx.<Boolean>getOne("s").orElse(false) && src.hasPermission(PluginInfo.DATA_ROOT + ".shop.server"))
            as_server.add(playerId);

        to_apply.put(playerId, new Tuple<>(name, tf));
        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
