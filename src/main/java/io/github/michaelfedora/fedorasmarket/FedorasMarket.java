package io.github.michaelfedora.fedorasmarket;

/**
 * Created by MichaelFedora on 1/17/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */

import com.google.inject.Inject;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.*;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.enumtype.PartyType;
import io.github.michaelfedora.fedorasmarket.enumtype.TradeType;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import me.flibio.updatifier.Updatifier;
import org.slf4j.Logger;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.text.Text;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Updatifier(repoName = "FedorasMarket", repoOwner = "MichaelFedora", version = PluginInfo.VERSION)
@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION)
public class FedorasMarket {

    private static FedorasMarket instance; // TODO: Set to an optional ;3

    public static final String ACCOUNT_SERVER_ID = "fedorasmarket:server_account";
    public static final String ACCOUNT_VIRTUAL_OWNER_ID_PREFIX = "fedorasmarket:virtual_owner_account_"; //TODO: Read up to see if these are too big
    public static final String ACCOUNT_VIRTUAL_CUSTOMER_ID_PREFIX = "fedorasmarket:virtual_customer_account_";

    @Inject
    private Logger logger;
    public static Logger getLogger() { return instance.logger; }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir; //TODO: Implement config
    public static Path getConfigDir() { return instance.configDir; }

    private EconomyService economyService;
    public static EconomyService getEconomyService() { return instance.economyService; }

    @Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
    }

    private Game game;
    public static Game getGame() { return instance.game; }

    private HashMap<List<String>, CommandSpec> subCommands;
    public static HashMap<List<String>, CommandSpec> getSubCommands() { return instance.subCommands; }
    private HashMap<String, HashMap<List<String>, CommandSpec>> grandChildCommands;
    public static Optional<HashMap<List<String>, CommandSpec>> getGrandChildCommands(String key) {

        if(instance.grandChildCommands.containsKey(key))
            return Optional.of(instance.grandChildCommands.get(key));

        return Optional.empty();
    }

    private List<String> chestNames = new ArrayList<String>();
    public static List<String> getChestNames() { return instance.chestNames; }

    private int maxItemStacks = 36;
    public static int getMaxItemStacks() { return instance.maxItemStacks; }

    @Listener
    public void onPreInit(GamePreInitializationEvent gpie) {
        instance = this;
    }

    @Listener
    public void onInit(GameInitializationEvent gie) {

        getLogger().info("== " + PluginInfo.NAME + " -- GameInitialization ==");

        game = Sponge.getGame();

        //TODO: Add read-config for chest names
        chestNames.add("minecraft:chest");
        chestNames.add("minecraft:trapped_chest");

        getLogger().info("== == FIN == ==");
    }

    @Listener
    public void onPostInit(GamePostInitializationEvent gpie) {
        getLogger().info("== " + PluginInfo.NAME + " - GamePostInitialization ==");
        subCommands = new HashMap<>();
        grandChildCommands = new HashMap<>();

        subCommands.put(Arrays.asList("help", "?"), CommandSpec.builder()
                .description(Text.of("Help Command"))
                .permission(PluginInfo.DATA_ROOT + ".help")
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page no"))))
                .executor(new FmHelpExecutor())
                .build());

        HashMap<List<String>, CommandSpec> transSubCommands = new HashMap<>();

        transSubCommands.put(Arrays.asList("help", "?"), CommandSpec.builder()
                .description(Text.of("Help Command"))
                .permission(PluginInfo.DATA_ROOT + ".trade.help")
                .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page no")))) // is this used I dun even
                .executor(new FmTradeFormHelpExecutor())
                .build());

        transSubCommands.put(Arrays.asList("create", "make", "new"), CommandSpec.builder()
                .description(Text.of("Create a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.create")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.optional(GenericArguments.enumValue(Text.of("trade_type"), TradeType.class)))
                .executor(new FmTradeFormCreateExecutor())
                .build());

        transSubCommands.put(Arrays.asList("delete", "del"), CommandSpec.builder()
                .description(Text.of("Delete a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.delete")
                .arguments(GenericArguments.string(Text.of("name")))
                .executor(new FmTradeFormDeleteExecutor())
                .build());

        transSubCommands.put(Arrays.asList("deletemany", "delm"), CommandSpec.builder()
                .description(Text.of("Delete many trade forms"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.deletemany")
                .arguments(GenericArguments.allOf(GenericArguments.string(Text.of("names"))))
                .executor(new FmTradeFormDeleteManyExecutor())
                .build());

        transSubCommands.put(Arrays.asList("list", "l"), CommandSpec.builder()
                .description(Text.of("Lists all trade forms"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.list")
                .executor(new FmTradeFormListExecutor())
                .build());

        transSubCommands.put(Arrays.asList("details", "cat"), CommandSpec.builder()
                .description(Text.of("Lists the details about a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.details")
                .arguments(GenericArguments.string(Text.of("name")))
                .executor(new FmTradeFormDetailsExecutor())
                .build());

        transSubCommands.put(Arrays.asList("apply"), CommandSpec.builder()
                .description(Text.of("Apply a trade form to a sign"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.apply")
                .arguments(GenericArguments.string(Text.of("name")))
                .executor(new FmTradeFormApplyExecutor())
                .build());

        transSubCommands.put(Arrays.asList("settradetype", "settype"), CommandSpec.builder()
                .description(Text.of("Set the TradeType of the trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.settradetype")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.enumValue(Text.of("type"), TradeType.class))
                .executor(new FmTradeFormSetTradeTypeExecutor())
                .build());

        transSubCommands.put(Arrays.asList("additem", "addi"), CommandSpec.builder()
                .description(Text.of("Add an item amount to a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.additem")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.integer(Text.of("amount")))
                .executor(new FmTradeFormAddItemExecutor())
                .build());

        transSubCommands.put(Arrays.asList("setitem", "seti"), CommandSpec.builder()
                .description(Text.of("Set an item entry in a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.setitem")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class),
                        GenericArguments.integer(Text.of("amount")))
                .executor(new FmTradeFormSetItemExecutor())
                .build());

        transSubCommands.put(Arrays.asList("removeitem", "remi"), CommandSpec.builder()
                .description(Text.of("Remove an item entry from a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.removeitem")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.catalogedElement(Text.of("item"), ItemType.class))
                .executor(new FmTradeFormRemoveItemExecutor())
                .build());

        // Get Currencies By Name
        Map<String, Currency> currencies = new TreeMap<>();
        for(Currency c : this.economyService.getCurrencies()) {
            currencies.put(c.getDisplayName().toPlain(), c);
        }

        transSubCommands.put(Arrays.asList("addcurrency", "addc"), CommandSpec.builder()
                .description(Text.of("Add a currency amount to a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.addcurrency")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.optional(GenericArguments.choices(Text.of("currency"), currencies, true)))
                .executor(new FmTradeFormAddCurrencyExecutor())
                .build());

        transSubCommands.put(Arrays.asList("setcurrency", "setc"), CommandSpec.builder()
                .description(Text.of("Sets a currency entry in a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.setcurrency")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.doubleNum(Text.of("amount")),
                        GenericArguments.optional(GenericArguments.choices(Text.of("currency"), currencies, true)))
                .executor(new FmTradeFormSetCurrencyExecutor())
                .build());

        transSubCommands.put(Arrays.asList("removecurrency", "remc"), CommandSpec.builder()
                .description(Text.of("Remove a currency from a trade form"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform.removecurrency")
                .arguments(
                        GenericArguments.string(Text.of("name")),
                        GenericArguments.enumValue(Text.of("party"), PartyType.class),
                        GenericArguments.optional(GenericArguments.choices(Text.of("currency"), currencies, true)))
                .executor(new FmTradeFormRemoveCurrencyExecutor())
                .build());


        subCommands.put(Arrays.asList("tradeform", "tform", "tf"), CommandSpec.builder()
                .description(Text.of("Do trade-form things (lists sub commands)"))
                .permission(PluginInfo.DATA_ROOT + ".tradeform")
                .executor(new FmTradeFormExecutor())
                .children(transSubCommands)
                .build());

        grandChildCommands.put("tradeform", transSubCommands);

        /*subCommands.put(Arrays.asList("offertrade", "otrade"), CommandSpec.builder()
                .description(Text.of("Offer to Trade to another player the item in your hand"))
                .permission(PluginInfo.DATA_ROOT + ".offertrade")
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.catalogedElement(Text.of("itemname"), ItemType.class),
                        GenericArguments.integer(Text.of("amount")))
                .executor(new FmTradeExecutor())
                .build());

        subCommands.put(Arrays.asList("offersell", "osell"), CommandSpec.builder()
                .description(Text.of("Offer to Trade to another player the item in your hand"))
                .permission(PluginInfo.DATA_ROOT + ".offersell")
                .arguments(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.catalogedElement(Text.of("itemname"), ItemType.class),
                        GenericArguments.integer(Text.of("amount")))
                .executor(new FmSellExecutor())
                .build());*/


        CommandSpec fmCommandSpec = CommandSpec.builder()
                .description(Text.of("FedorasMarket Command"))
                .permission(PluginInfo.DATA_ROOT + ".use")
                .children(subCommands)
                .executor(new FmExecutor())
                .build();

        Sponge.getCommandManager().register(this, fmCommandSpec, "fedmarket", "fm");

        getLogger().info("== == FIN == ==");
    }

    @Listener
    public void onLoadComplete(GameLoadCompleteEvent glce) {
        getLogger().info("== " + PluginInfo.NAME + " - GameLoadComplete ==");
        try {

            DatabaseManager.initialize();

        } catch(SQLException e) {
            getLogger().error("OnLoadComplete(:c)", e);
        }
        getLogger().info("== == FIN == ==");
    }
}
