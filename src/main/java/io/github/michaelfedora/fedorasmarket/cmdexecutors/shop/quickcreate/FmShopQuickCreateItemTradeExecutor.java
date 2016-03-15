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
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Michael on 3/13/2016.
 */
public class FmShopQuickCreateItemTradeExecutor extends FmShopQuickCreateExecutorBase {

    public static final List<String> aliases = Arrays.asList("itemtrade", "itrade");

    public static CommandSpec create() {
        return CommandSpec.builder()
                .description(Text.of("Create an ItemTrade shop"))
                .permission(PluginInfo.DATA_ROOT + ".shop.quickcreate.itemtrade")
                .arguments(
                        GenericArguments.integer(Text.of("item_amt")),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.integer(Text.of("item_amt2")),
                        GenericArguments.catalogedElement(Text.of("item2"), ItemType.class),
                        GenericArguments.flags()
                                .flag("s", "-server")
                                .buildWith(GenericArguments.none())
                )
                .executor(new FmShopQuickCreateItemTradeExecutor())
                .build();
    }

    @Override
    protected String getName() {
        return "shop quickcreate itemtrade";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player))
            throw makeSourceNotPlayerException();

        UUID playerId = ((Player) src).getUniqueId();

        String name = ctx.<String>getOne("name").orElseThrow(makeParamExceptionSupplier("name"));

        int itemAmount = ctx.<Integer>getOne("item_amt").orElseThrow(makeParamExceptionSupplier("item_amt"));
        ItemType itemType = ctx.<ItemType>getOne("item").orElseThrow(makeParamExceptionSupplier("item"));

        TradeParty owner = new TradeParty();
        owner.addItem(itemType, itemAmount);

        int itemAmount2 = ctx.<Integer>getOne("item_amt2").orElseThrow(makeParamExceptionSupplier("item_amt2"));
        ItemType itemType2 = ctx.<ItemType>getOne("item2").orElseThrow(makeParamExceptionSupplier("item2"));

        TradeParty customer = new TradeParty();
        customer.addItem(itemType2, itemAmount2);

        TradeForm tf = new TradeForm(TradeType.ITEM_TRADE, owner, customer);

        if(ctx.<Boolean>getOne("s").orElse(false) && src.hasPermission(PluginInfo.DATA_ROOT + ".shop.server"))
            as_server.add(playerId);

        to_apply.put(playerId, new Tuple<>("ItemTrade", tf));
        PlayerInteractListener.toRun.put(playerId, this::OnInteractSecondary);

        msg(src, "Select a block!");

        return CommandResult.success();
    }
}
